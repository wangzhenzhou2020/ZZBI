package com.yupi.springbootinit;

import cn.hutool.aop.ProxyUtil;
import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.vo.BiResponse;
import com.yupi.springbootinit.service.ChartService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * 主类测试
 *
 * 
 * 
 */
@SpringBootTest
class MainApplicationTests {
    @Autowired
    private ChartService chartService;

    private ExecutorService executorService = new ThreadPoolExecutor(
            16,
            100,
            1000,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(1000)
    );


    @Test
    void contextLoads() {
//        String response = sendMessageToChatGPT("你好，ChatGPT！你今天怎么样？");
//        createChatCompletion("你好，ChatGPT！你今天怎么样？");
//        System.out.println("ChatGPT的回复: " + response);

//        SqlRunner.db().update("select * from chart");

    }



}
