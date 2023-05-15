package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api")
public class ElasticSearchController {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);

    @Value("${elasticsearch.url}")
    private  final String KIBANA_URL = null;

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/elasticsearch")
    public List<String> search(
            @RequestParam("input") String keyword,
            @RequestParam("utcDateStart") String startdateString,
            @RequestParam("utcDateStart1") String enddateString,
            @RequestParam("utcTimeStart") String startTime,
            @RequestParam("utcTimeStart1") String endTime,
            @RequestParam("service") String service)

    {

        logger.info("Entered into elasticsearch api call");

        RestTemplate restTemplate = new RestTemplate();

       // String searchKibanaUrl = "http://localhost:9200/logstash*/_search?q=@timestamp:[" ;
        //String timeUrl = startdateString +"T" + "'"+startTime+ "'"+ ":00.000Z TO " + "'"+enddateString+ "'" + "T" +  "'"+endTime + "'"+ ":00.000Z]&q='"+keyword+"'"+"&format=json" + "&size=50";



        String words[]=keyword.split(" ");


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            sb.append(words[i]);
            if (i != words.length - 1) {
                sb.append(" AND ");
            }
        }




       // System.out.println(words);
       String keyword2=sb.toString();
        //int i=0;
       // for(String keyword1: words)
       // {


         //  keyword2=String.join(" AND ",keyword2,keyword1);
       // }

        System.out.println(keyword2);
        String searchKibanaUrl = KIBANA_URL+ "" +keyword2 + ""+ " AND @timestamp: [" ;

        String timeUrl = startdateString +"T" +startTime+ ".000Z TO " +enddateString+ "T" +endTime + ".000Z]";
        String serviceUrl=" AND kubernetes.labels.app='"+service+"'";


       // String url_link= "http://localhost:9200/logstash*/_search?q=trace AND @timestamp: [2023-04-16T18:30:00.000Z TO 2023-04-17T11:30:00.000Z]";

        String url_link= searchKibanaUrl+timeUrl+serviceUrl+"&size=50";

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
            logger.info("Fetching the elasticsearch logs");
            if (noOfLogs > 0) {
                for (JsonNode hit : hits) {
                    String log = hit.get("_source").get("log").asText();
                    obtained_logs.add(log);
                }

                System.out.println(obtained_logs);

            } else {
                obtained_logs.add("Keyword not found");
            }
            System.out.println(obtained_logs.size());
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



