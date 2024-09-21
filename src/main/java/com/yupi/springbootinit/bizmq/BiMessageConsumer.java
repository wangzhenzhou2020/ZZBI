package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AiManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * spring-amqp实践
 */
@Component
@Slf4j
public class BiMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = BiMqConstant.BI_QUEUE_NAME,
                    durable = "true"
            ),
            exchange = @Exchange(value = BiMqConstant.BI_EXCHANGE_NAME),
            key = {BiMqConstant.BI_ROUTING_KEY}))
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 如果java程序中断，队列中存在ready消息，会重发。
        log.info("receiveMessage message = {}", message);
        // 抛出异常会导致自动 拒绝消息
        ThrowUtils.throwIf(StringUtils.isBlank(message),ErrorCode.SYSTEM_ERROR, "mq 消息为空");
        // 1.修改图表任务状态
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        ThrowUtils.throwIf(chart == null,ErrorCode.NOT_FOUND_ERROR, "图表任务为空");
        // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            chartService.handleChartUpdateError(chart.getId(), "更新图表为执行中状态失败");
        }
        // 2.调用 AI
        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(chart));
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            chartService.handleChartUpdateError(chart.getId(), "AI 生成错误, splits.length < 3");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        // todo 建议定义状态为枚举值
        updateChartResult.setStatus("succeed");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            chartService.handleChartUpdateError(chart.getId(), "更新图表为成功状态失败");
        }
    }

    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart) {
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        // 构造用户输入
        // 拼接分析目标
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");
        // 拼接图表类型
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        // 拼接原始数据
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }

//    // 指定程序监听的消息队列和确认机制
//    @SneakyThrows
//    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
//    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
//        log.info("receiveMessage message = {}", message);
//        // 如果java程序中断，队列中存在ready消息，会重发。
//        if (StringUtils.isBlank(message)) {
//            // 如果失败，消息拒绝
//            channel.basicNack(deliveryTag, false, false);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
//        }
//        long chartId = Long.parseLong(message);
//        Chart chart = chartService.getById(chartId);
//        if (chart == null) {
//            channel.basicNack(deliveryTag, false, false);
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表为空");
//        }
//        // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
//        Chart updateChart = new Chart();
//        updateChart.setId(chart.getId());
//        updateChart.setStatus("running");
//        boolean b = chartService.updateById(updateChart);
//        if (!b) {
//            channel.basicNack(deliveryTag, false, false);
//            chartService.handleChartUpdateError(chart.getId(), "更新图表执行中状态失败");
//            return;
//        }
//        // 调用 AI
//        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(chart));
//        String[] splits = result.split("【【【【【");
//        if (splits.length < 3) {
//            channel.basicNack(deliveryTag, false, false);
//            chartService.handleChartUpdateError(chart.getId(), "AI 生成错误");
//            return;
//        }
//        String genChart = splits[1].trim();
//        String genResult = splits[2].trim();
//        Chart updateChartResult = new Chart();
//        updateChartResult.setId(chart.getId());
//        updateChartResult.setGenChart(genChart);
//        updateChartResult.setGenResult(genResult);
//        // todo 建议定义状态为枚举值
//        updateChartResult.setStatus("succeed");
//        boolean updateResult = chartService.updateById(updateChartResult);
//        if (!updateResult) {
//            channel.basicNack(deliveryTag, false, false);
//            chartService.handleChartUpdateError(chart.getId(), "更新图表成功状态失败");
//        }
//        // 消息确认
//        channel.basicAck(deliveryTag, false);
//    }

//    private void handleChartUpdateError(long chartId, String execMessage) {
//        Chart updateChartResult = new Chart();
//        updateChartResult.setId(chartId);
//        updateChartResult.setStatus("failed");
//        updateChartResult.setExecMessage("execMessage");
//        boolean updateResult = chartService.updateById(updateChartResult);
//        if (!updateResult) {
//            log.error("更新图表失败状态失败" + chartId + "," + execMessage);
//        }
//    }

}
