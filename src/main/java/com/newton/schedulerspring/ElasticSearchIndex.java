package com.newton.schedulerspring;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
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
  private static final String ELASTIC_INDEX_NAME = "covid";
  private static final String URI = "https://pomber.github.io/covid19/timeseries.json";
  private static final String FILE_NAME = "covidData.json";

  public static void main(String[] args) throws IOException {
    int portNumber = 9200;
    String hostname = "127.0.0.1";
    String http = "http";

    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(URI, String.class);
    FileUtils.writeStringToFile(new File(FILE_NAME), result, Charset.defaultCharset());

    String covidDataString = FileUtils.readFileToString(new File(FILE_NAME), Charset.defaultCharset());
    JSONObject jsonObject = new JSONObject(covidDataString);
    Iterator<String> keys = jsonObject.keys();

    try (RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(new HttpHost(hostname, portNumber, http)))) {
      deleteIndex(client);
      BulkRequest bulkRequest = new BulkRequest();
      while (keys.hasNext()) {
        String key = keys.next();
        if (jsonObject.get(key) instanceof JSONArray) {
          JSONArray objects = (JSONArray) jsonObject.get(key);
          for (Object object : objects) {
            JSONObject jsonObject1 = (JSONObject) object;
            LOGGER.info("Processing for Country : " + key);
            bulkRequest.add(new IndexRequest(ELASTIC_INDEX_NAME).id(UUID.randomUUID().toString())
                .source(XContentType.JSON,
                    "country", key,
                    "date", jsonObject1.get("date"),
                    "confirmed", jsonObject1.get("confirmed"),
                    "deaths", jsonObject1.get("deaths"),
                    "recovered", jsonObject1.get("recovered")));
          }
        }
        BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        bulkRequest = new BulkRequest();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void deleteIndex(RestHighLevelClient client) {
    try {
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("elasticIndexName");
      AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
      LOGGER.info(deleteIndexResponse.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
