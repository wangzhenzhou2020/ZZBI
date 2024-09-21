package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel 相关工具类
 */
@Slf4j
public class ExcelUtils {

    /**
     * excel 转 csv
     *
     * @param multipartFile
     * @return
     */
    public static String excelToCsv(MultipartFile multipartFile) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        if (CollUtil.isEmpty(list)) {
            return "";
        }
        // 转换为 csv
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap
                .values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        for (int i = 1; i < list.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) list.get(i);
            List<String> dataList = dataMap
                    .values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 把表格文件数据存储为数据库的一张表 ，by zz
     *
     * @param multipartFile
     * @return
     */
    public static boolean excelToDBTable(MultipartFile multipartFile, String tableName) {
        // 读取数据
        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("表格处理错误", e);
        }
        ThrowUtils.throwIf(CollUtil.isEmpty(list), ErrorCode.SYSTEM_ERROR, "表格数据为空");
        // 列固定属性
        final String firstLine = String.format("create table if not exists %s (", tableName);
        final String columnProperty = " varchar(1024) null";
        final String endLine = ");";
        // 读取表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
        List<String> headerList = headerMap
                .values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        List<String> tempinterLines = headerList.stream().map(s -> s + columnProperty).collect(Collectors.toList());
        String interLines = String.join(",", tempinterLines);
        final String createTableSql = firstLine + interLines + endLine;
//        SqlRunner 直接建表。
        SqlRunner db = SqlRunner.db();
        boolean createTableResult = db.insert(createTableSql);  // todo 这样建表无法检测是否成功（即使成功了也是false）
//        ThrowUtils.throwIf(!createResult, ErrorCode.SYSTEM_ERROR,"新建表失败");
        // 往新表 插入数据
        String insertSqlFirstLine = String.format("insert into %s ( %s ) values ", tableName, String.join(",",
                headerList));

//        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取数据
        StringBuilder valuesLine = new StringBuilder();
        List<Map<Integer, String>> contentList = list.subList(1, list.size());  // 数据内容
        for (int i = 0; i < contentList.size(); i++) {
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap) contentList.get(i);
            List<String> dataList = dataMap
                    .values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            String valueLine = "(" + String.join(",", dataList) + "),";
            valuesLine.append(valueLine);
            if ((i!=0 && i % 1000 == 0 )|| i == contentList.size() - 1) {
                valuesLine.replace(valuesLine.length() - 1, valuesLine.length(), ";");
                String insertLinesSql = insertSqlFirstLine + valuesLine.toString();
                try {
                    boolean insert = db.insert(insertLinesSql);  //todo 这样无法检测是否成功
                }catch (RuntimeException e){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存元素数据出错");
                }
                valuesLine = new StringBuilder();
            }
        }
        return true;
    }

    public static void main(String[] args) {
        excelToCsv(null);
    }
}
