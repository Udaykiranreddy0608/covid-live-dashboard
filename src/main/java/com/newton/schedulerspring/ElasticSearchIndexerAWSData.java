package com.newton.schedulerspring;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.UUID;

public class ElasticSearchIndexerAWSData {
  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchIndexerAWSData.class);
  private static String[] HEADERS =
      {"uid", "fips", "iso2", "iso3", "code3", "admin2", "latitude", "longitude", "province_state",
          "country_region", "date", "confirmed", "deaths", "recovered"};
  private static String index = "aws_covid";

  public static void main(String[] args) throws IOException {

    Reader in = new FileReader(
        "jhu_csse_covid_19_timeseries_merged.csv");
    Iterable<CSVRecord> records = CSVFormat.DEFAULT
        .withHeader(HEADERS)
        .withFirstRecordAsHeader()
        .parse(in);

    try (RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")))) {
      deleteIndex(client, index);
      BulkRequest bulkRequest = new BulkRequest();
      int count = 0;
      for (CSVRecord record : records) {
        bulkRequest.add(new IndexRequest(index).id(UUID.randomUUID().toString())
            .source(XContentType.JSON,
                "uid", record.get("uid"),
                "fips", record.get("fips"),
                "iso2", record.get("iso2"),
                "code3", record.get("code3"),
                "admin2", record.get("admin2"),
                "latitude", record.get("latitude"),
                "longitude", record.get("longitude"),
                "province_state", record.get("province_state"),
                "country_region", record.get("country_region"),
                "date", record.get("date"),
                "confirmed", record.get("confirmed"),
                "deaths", record.get("deaths"),
                "recovered", record.get("recovered")));
        count++;
        if (count > 10000) {
          LOGGER.info("Processing for records  : " + count);
          BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
          bulkRequest = new BulkRequest();
        }
      }
      BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
      bulkRequest = new BulkRequest();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void deleteIndex(RestHighLevelClient client, String index) {
    try {
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
      AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
      LOGGER.info(deleteIndexResponse.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
