package com.newton.schedulerspring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
  public static Logger logger = LoggerFactory.getLogger(TestController.class);
  public static long count = 0;

  @GetMapping(value = "/date")
  public long test() {
    long date = System.currentTimeMillis();
    logger.info("Serving count : {} \t date: {}", count++, date);
    return date;
  }
}
