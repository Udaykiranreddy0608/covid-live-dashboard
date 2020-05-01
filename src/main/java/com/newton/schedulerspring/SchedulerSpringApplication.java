package com.newton.schedulerspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SchedulerSpringApplication {

  public static void main(String[] args) {
    SpringApplication.run(SchedulerSpringApplication.class, args);
  }

}
