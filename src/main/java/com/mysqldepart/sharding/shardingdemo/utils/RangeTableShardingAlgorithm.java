package com.mysqldepart.sharding.shardingdemo.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.text.SimpleDateFormat;
import java.util.*;

public class RangeTableShardingAlgorithm  implements RangeShardingAlgorithm<Long> {
    private static  SimpleDateFormat dateformat=new SimpleDateFormat("yyyyMM");
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> rangeShardingValue) {
        System.out.println("查找某个范围的数据时 返回表名");
        final ArrayList<String> result = new ArrayList<>();
        final Range<Long> range = rangeShardingValue.getValueRange();
        long startMillisecond = range.lowerEndpoint();
        long endMillisecond = range.upperEndpoint();

//        // 起始年和结束年
//        int startYear = Integer.parseInt(DateUtil.getYearByMillisecond(startMillisecond));
//        int endYear = Integer.parseInt(DateUtil.getYearByMillisecond(endMillisecond));
//        // 起始月和结束月
//        int startMonth = Integer.parseInt(DateUtil.getMonthByMillisecond(startMillisecond));
//        int endMonth = Integer.parseInt(DateUtil.getMonthByMillisecond(endMillisecond));
//
//        int startYearJoinMonth = Integer.parseInt(DateUtil.getYearJoinMonthByMillisecond(startMillisecond));
//        int endYearJoinMonth = Integer.parseInt(DateUtil.getYearJoinMonthByMillisecond(endMillisecond));
//        return startYear == endYear ? theSameYear(startMonth, endMonth, availableTargetNames, result)
//                : differentYear(startYear, endYear, startMonth, endMonth, startYearJoinMonth, endYearJoinMonth, availableTargetNames, result);
//
        //动态策略
        Date startTime=new Date(startMillisecond);
        Date endTime=new Date(endMillisecond);
        Date now = new Date();
        if (startTime.after(now)){
            startTime = now;
        }
        if (endTime.after(now)){
            endTime = now;
        }
        Collection<String> tables = getRoutTable(rangeShardingValue.getLogicTableName(), startTime, endTime);

        if (tables != null && tables.size() >0) {
            result.addAll(tables);
        }
        return result;


    }
//动态获取表名方法-----------------------------
private Collection<String> getRoutTable(String logicTableName, Date startTime, Date endTime) {
    Set<String> rouTables = new HashSet<>();
    if (startTime != null && endTime != null) {
        List<String> rangeNameList = getRangeNameList(startTime, endTime);
        for (String YearMonth : rangeNameList) {
            rouTables.add(logicTableName +"_"+ YearMonth);
        }
    }
    return rouTables;
}

    private static List<String> getRangeNameList(Date startTime, Date endTime) {
        List<String> result = Lists.newArrayList();
        // 定义日期实例
        Calendar dd = Calendar.getInstance();

        dd.setTime(startTime);

        while(dd.getTime().before(endTime)) {
            result.add(dateformat.format(dd.getTime()));
            // 进行当前日期按月份 + 1
            dd.add(Calendar.MONTH, 1);
        }
        return result;
    }



//------------------静态获取表名-------------------
//    // 同年，但可能不同月
//    private Collection<String> theSameYear(int startMonth, int endMonth, Collection<String> availableTargetNames, ArrayList<String> result) {
//        System.out.println("开始月："+startMonth+"结束月："+endMonth);
//        return startMonth == endMonth ? theSameMonth(startMonth, availableTargetNames, result) : differentMonth(startMonth, endMonth, availableTargetNames, result);
//    }
//
//    // 同年同月
//    private Collection<String> theSameMonth(int startMonth, Collection<String> availableTargetNames, ArrayList<String> result) {
//
//        String startMonthStr = String.valueOf(startMonth);
//        if (startMonthStr.length() == 1) startMonthStr = "0" + startMonthStr;
//        for (String availableTargetName : availableTargetNames) {
//            if (availableTargetName.endsWith(startMonthStr)) {
//                result.add(availableTargetName);
//            }
//        }
//        return result;
//    }
//
//    // 同年不同月
//    private Collection<String> differentMonth(int startMonth, int endMonth, Collection<String> availableTargetNames, ArrayList<String> result) {
//
//        for (String availableTargetName : availableTargetNames) {
//            for (int i = startMonth; i <= endMonth; i++) {
//                String monthStr = String.valueOf(i);
//                if (monthStr.length() == 1) monthStr = "0" + monthStr;
//                if (availableTargetName.endsWith(monthStr)) result.add(availableTargetName);
//            }
//        }
//        return result;
//    }
//
//
//    // 不同年，跨年，最少两个月，需要考虑跨两年以上的情况
//    private Collection<String> differentYear(int startYear, int endYear, int startMonth, int endMonth, int startYearJoinMonth, int endYearJoinMonth, Collection<String> availableTargetNames, ArrayList<String> result) {
//
//        return endYear - startYear == 1 ? twoYears(startYear, endYear, startMonth, endMonth, startYearJoinMonth, endYearJoinMonth, availableTargetNames, result) : moreThanTwoYears(startYear, endYear, startMonth, endMonth, availableTargetNames, result);
//    }
//
//
//    // 两年
//    private Collection<String> twoYears(int startYear, int endYear, int startMonth, int endMonth, int startYearJoinMonth, int endYearJoinMonth, Collection<String> availableTargetNames, ArrayList<String> result) {
//
//        int endCondition;
//        endCondition = Integer.parseInt(startYear + "12");
//        for (int i = startYearJoinMonth; i <= endCondition; i++) {
//            for (String availableTargetName : availableTargetNames) {
//                // 如果多库此算法sharding会匹配两次，需要年份加月份来判断，只使用月份的话有问题
//                if (availableTargetName.endsWith(String.valueOf(i))) result.add(availableTargetName);
//            }
//        }
//
//        endCondition = Integer.parseInt(endYear + "01");
//        for (int i = endYearJoinMonth; i >= endCondition; i--) {
//            for (String availableTargetName : availableTargetNames) {
//                if (availableTargetName.endsWith(String.valueOf(i))) result.add(availableTargetName);
//            }
//        }
//        return result;
//    }
//
//
//    // 两年以上
//    private Collection<String> moreThanTwoYears(int startYear, int endYear, int startMonth, int endMonth, Collection<String> availableTargetNames, ArrayList<String> result) {
//        return null;
//    }
}
