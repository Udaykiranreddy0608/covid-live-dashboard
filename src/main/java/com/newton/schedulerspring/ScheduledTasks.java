package com.newton.schedulerspring;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

  private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


//  @Bean
//  public RestTemplate restTemplate(RestTemplateBuilder builder) {
//    return builder.build();
//  }

  //@Scheduled(fixedRate = 5000)
  public void reportCurrentTime() throws IOException {
    log.info("The time is now {}", dateFormat.format(new Date()));
    final String uri = "https://pomber.github.io/covid19/timeseries.json";

    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(uri, String.class);
    FileUtils.writeStringToFile(new File("covidData.json"), result, Charset.defaultCharset());
    System.out.println(result);
    System.exit(1);
  }
}
