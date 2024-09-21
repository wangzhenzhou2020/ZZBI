package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Slf4j
public class Slf4jService {
    static Logger logger = LoggerFactory.getLogger(Slf4jService.class);

    public static void main(String[] args) {
        log.info("1");
        logger.info("2");
    }
}

