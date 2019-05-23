package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //initialdelay == milliseconds to delay before the first execution of a fixedRate() or fixedDelay() task.
    //fixedRate == with a fixed period in milliseconds between invocations
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("current time is", dateFormat.format(new Date()));
    }
}
