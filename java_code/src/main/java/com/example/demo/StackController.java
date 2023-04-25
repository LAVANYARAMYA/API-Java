package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


@RestController
@RequestMapping("/api")
public class StackController  {
    private static final String STACKOVERFLOW_URL = "https://api.stackexchange.com/2.3/search?order=desc&sort=activity&intitle=";

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/stackoverflow")
    public List<String> search(@RequestParam("input") String keyword) {
        System.out.println(keyword);
        RestTemplate restTemplate = new RestTemplate();
        String searchStackUrl = STACKOVERFLOW_URL +"'"+ URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "'"+"&site=stackoverflow";
        System.out.println(searchStackUrl);
          StringBuilder stackResponse = new StringBuilder();
           String WebUI="";
           int i=0;
           List<String> webui_list = new ArrayList<String>();
           ObjectMapper mapper = new ObjectMapper();
           String stackOutput  = new String();
                 try {
                     HttpURLConnection connection = (HttpURLConnection) new URL(searchStackUrl).openConnection();
                     connection.setRequestMethod("GET");

                     InputStream responseStream = connection.getInputStream();
                     if ("gzip".equals(connection.getContentEncoding())) {
                         responseStream = new GZIPInputStream(responseStream);
                     }
                     BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

                     String line;
                     while ((line = reader.readLine()) != null) {
                         stackResponse.append(line);
                     }
                     reader.close();

                     stackOutput=   stackResponse.toString();
                     System.out.println(stackOutput);


                 }catch (IOException e)
                 {
                     e.printStackTrace();
                 }



              try {
                    JsonNode responseJson = mapper.readTree(stackOutput);


                           JsonNode firsthits = responseJson.get("items");
                          if(firsthits.size()>0) {
                            for ( i = 0; i <= 25 && i < firsthits.size(); i++) {
                                JsonNode firstResult = firsthits.get(i);
                                String title = firstResult.get("title").asText();
                                String webui = firstResult.get("link").asText();
                                webui_list.add(webui);
                                webui_list.add(title);
                            }
                          }
                         else {
                              String s1="Keyword not found";
                              webui_list.add(s1);
                          }

                  } catch (JSONException e) {
                          e.printStackTrace();
                  } catch (JsonMappingException e) {
                  throw new RuntimeException(e);
                  } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);}

        System.out.println(webui_list);


                return webui_list;
    }

}



