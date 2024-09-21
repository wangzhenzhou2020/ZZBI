package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupi.springbootinit.model.dto.chart.ChartQueryRequest;
import com.yupi.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author wangzhenzhou
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-08-28 10:37:59
*/
public interface ChartService extends IService<Chart> {
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

    public void handleChartUpdateError(long chartId, String execMessage);
    public boolean recordChartFailed(Chart chart);

}
