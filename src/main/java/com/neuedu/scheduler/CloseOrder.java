package com.neuedu.scheduler;

import com.neuedu.service.OrderService;
import com.neuedu.util.PropertiesUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定时关闭订单
 */
@Component
public class CloseOrder {

    @Autowired
    OrderService orderService;

    @Scheduled(cron = "0 0/30 * * * *")
    public void colseorder() {
        System.out.println("++++++++++++++关闭订单++++++++++++++++");
        //关闭1个小时订单
        Integer timeout =Integer.parseInt(PropertiesUtils.readKey("close.order.time"));
        String close = com.neuedu.util.DateUtils.dateToStr(DateUtils.addHours(new Date(),-timeout));
        orderService.colseOrder(close);
    }
}
