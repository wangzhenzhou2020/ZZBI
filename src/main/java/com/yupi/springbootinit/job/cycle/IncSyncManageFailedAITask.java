package com.yupi.springbootinit.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 处理失败的任务
 *
 * 
 * 
 */
// todo 取消注释开启任务
@Component
@Slf4j
public class IncSyncManageFailedAITask {

    @Autowired
    private ChartService chartService;

    @Autowired
    private AiManager aiManager;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)  //
    public void run() {
        // find and sort by time
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "failed");
        queryWrapper.orderByDesc("updateTime");
        List<Chart> list = chartService.list(queryWrapper);
        List<String> collect = list.stream().map(Chart::getId).map(Object::toString).collect(Collectors.toList());
        log.info("AI定时任务启动"+String.join(",",collect));

        for (Chart chart : list) {
            // 构造用户输入
            StringBuilder userInput = new StringBuilder();
            userInput.append("分析需求：").append("\n");
            // 拼接分析目标
            String userGoal = chart.getGoal();
            if (StringUtils.isNotBlank(chart.getChartType())) {
                userGoal += "，请使用" + chart.getChartType();
            }
            userInput.append(userGoal).append("\n");
            // 压缩后的数据
            userInput.append("原始数据：").append("\n");
            userInput.append(chart.getChartData()).append("\n");

            // 处理任务队列满了后，抛异常的情况
            try {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
                    Chart updateChart = new Chart();
                    updateChart.setId(chart.getId());
                    updateChart.setStatus("running");
                    boolean b = chartService.updateById(updateChart);
                    if (!b) {
                        chartService.handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
                        return;
                    }
                    // 调用 AI
                    String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, userInput.toString());
                    String[] splits = result.split("【【【【【");
                    if (splits.length < 3) {
                        chartService.handleChartUpdateError(chart.getId(), "AI 生成错误");
                        return;
                    }
                    // 保存结果
                    String genChart = splits[1].trim();
                    // genChart校验
                    if ('{' != genChart.charAt(0) || '}' != genChart.charAt(genChart.length() - 1)) {
                        log.error("定时任务AI结果有误,chart id:" + chart.getId());
                        return;
                    }

                    String genResult = splits[2].trim();
                    Chart updateChartResult = new Chart();
                    updateChartResult.setId(chart.getId());
                    updateChartResult.setGenChart(genChart);
                    updateChartResult.setGenResult(genResult);
                    // todo 建议定义状态为枚举值
                    updateChartResult.setStatus("succeed");
                    boolean updateResult = chartService.updateById(updateChartResult);
                    if (!updateResult) {
                        chartService.handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
                    }
                }, threadPoolExecutor);
                // 任务超时
                try {
                    future.get(20, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    boolean b = chartService.recordChartFailed(chart); // 标记chart为失败
                    if (b) {
                        log.error("定时任务超时,chart id:" + chart.getId());
                    } else {
                        log.error("定时任务超时且未登记失败,chart id:" + chart.getId());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    log.error("定时任务未知异常,chart id:" + chart.getId());
                } finally {
                    future.cancel(true); // 中断任务
                }
            } catch (RejectedExecutionException e) {  // 线程池队列满了
                boolean b = chartService.recordChartFailed(chart); // 标记chart为失败
                if (b) {
                    log.error("定时任务繁忙,chart id:" + chart.getId());
                } else {
                    log.error("定时任务繁忙且未登记失败,chart id:" + chart.getId());
                }
            }
        }
    }
}
