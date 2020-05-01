package com.newton.schedulerspring;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.UUID;

public class ElasticSearchIndex {
  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchIndex.class);

  public static void main(String[] args) throws IOException {
    final String uri = "https://pomber.github.io/covid19/timeseries.json";

    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(uri, String.class);
    FileUtils.writeStringToFile(new File("covidData.json"), result, Charset.defaultCharset());

    String string[] = {"a","b"};
    String covidDataString = FileUtils.readFileToString(new File("covidData.json"), Charset.defaultCharset());
    JSONObject jsonObject = new JSONObject(covidDataString);
    Iterator<String> keys = jsonObject.keys();

    try (RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")))) {

      BulkRequest bulkRequest = new BulkRequest();
      while (keys.hasNext()) {
        String key = keys.next();
        if (jsonObject.get(key) instanceof JSONArray) {
          JSONArray objects = (JSONArray) jsonObject.get(key);
          objects.forEach(v -> {
            JSONObject jsonObject1 = (JSONObject) v;
            LOGGER.info("Processing for Country : " + key);
            bulkRequest.add(new IndexRequest("covid").id(UUID.randomUUID().toString())
                .source(XContentType.JSON, "country", key, "date", jsonObject1.get("date"), "confirmed",
                    jsonObject1.get("confirmed"), "deaths", jsonObject1.get("deaths"),
                    "recovered", jsonObject1.get("recovered")));
          });
        }
        BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    //      BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//      System.out.println(bulkItemResponses.getIngestTook());
//      System.out.println(bulkItemResponses.getIngestTookInMillis());

//    IndexResponse indexResponse = client
//        .index(new IndexRequest("covid").id("7").source(XContentType.JSON, "country", "uk"), RequestOptions.DEFAULT);
//    System.out.println(indexResponse.status());

  }
}
