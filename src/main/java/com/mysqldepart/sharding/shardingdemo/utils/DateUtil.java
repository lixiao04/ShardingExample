package com.mysqldepart.sharding.shardingdemo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil{
    private static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");

    private static final SimpleDateFormat yearJoinMonthFormat = new SimpleDateFormat("yyyyMM");


    public static String getYearByMillisecond(long millisecond) {

        return yearFormat.format(new Date(millisecond));
    }


    public static String getMonthByMillisecond(long millisecond) {

        return monthFormat.format(new Date(millisecond));
    }


    public static String getYearJoinMonthByMillisecond(long millisecond) {

        return yearJoinMonthFormat.format(new Date(millisecond));
    }
}
