package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ElasticSearchController {

   // private static final String KIBANA_URL = "http://localhost:9200/fluentd/_search?q=";

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/elasticsearch")
    public List<String> search(
            @RequestParam("input") String keyword,
            @RequestParam("startdateString") String startdateString,
            @RequestParam("enddateString") String enddateString,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime)
    {

        RestTemplate restTemplate = new RestTemplate();

        String searchKibanaUrl = "http://localhost:9200/logstash*/_search?q=@timestamp:[" ;
        String timeUrl = startdateString +"T" + "'"+startTime+ "'"+ ":00.000Z TO " + "'"+enddateString+ "'" + "T" +  "'"+endTime + "'"+ ":00.000Z]&format=json" + "&size=50&&q='"+keyword+ "'";

        String url_link= searchKibanaUrl+timeUrl;



       System.out.println(url_link);

        //kibana
        ObjectMapper mapper = new ObjectMapper();
        String kibanaResponse = restTemplate.getForObject(url_link, String.class);
        System.out.println(kibanaResponse);
        System.out.println(kibanaResponse.length());

        List<String> obtained_logs = new ArrayList<String>();


        //kibana try block
        try {


            JsonNode responseJson = mapper.readTree(kibanaResponse);
            // JSONArray results = jsonObj.getJSONArray("hits");

            JsonNode firsthits = responseJson.get("hits");
            int noOfLogs = firsthits.get("total").get("value").asInt();
            JsonNode hits = firsthits.get("hits");
            System.out.println(noOfLogs);
            if (noOfLogs > 0) {
                for (JsonNode hit : hits) {
                    String log = hit.get("_source").get("log").asText();
                    obtained_logs.add(log);
                }

                System.out.println(obtained_logs);

            } else {
                obtained_logs.add("Keyword not found");
            }
        }

            catch(JSONException e){
                e.printStackTrace();
            } catch(JsonMappingException e){
                throw new RuntimeException(e);
            } catch(JsonProcessingException e){
                throw new RuntimeException(e);
            }


      // System.out.println(List.of(webui_list,title_list));

        return obtained_logs;

       // return webui_list;



    }

}



