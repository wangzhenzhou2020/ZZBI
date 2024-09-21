package com.yupi.springbootinit.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChartMapperTest {

    @Autowired
    private ChartMapper chartMapper;
    @Test
    void queryChartData() {
        String chartId = "1830536006905004033";
        String querySql = String.format("select * from chart_%s", chartId);
        List<Map<Object, Object>> resultData = chartMapper.queryChartData(querySql);
        System.out.println(resultData);
    }
}