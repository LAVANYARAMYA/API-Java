package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import org.json.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ConfluenceController {


    @Value("${confluence.url}")
    private  final String CONFLUENCE_URL = null;
    @Value("${confluence.username}")
    private final String CONFLUENCE_USERNAME =null;
    @Value("${confluence.password}")
    private  final String CONFLUENCE_PASSWORD = null;


    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/confluence")
    public List<String> search(@RequestParam("input") String keyword,
                               @RequestParam("service") String service) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(CONFLUENCE_USERNAME, CONFLUENCE_PASSWORD));
        System.out.println(service);
        String searcConfluencehUrl = CONFLUENCE_URL + "/rest/api/content/search?cql=type=page+and+text~' " + keyword +"'"+ "OR text~'"+service+"'";
        System.out.println(searcConfluencehUrl);

        //kibana
        String confluenceResponse = restTemplate.getForObject(searcConfluencehUrl, String.class);
        System.out.println(confluenceResponse);
        String WebUI="";
        int i=0;
        List<String> webui_list = new ArrayList<String>();


        //confluence try block
        try {
            JSONObject jsonObj = new JSONObject(confluenceResponse);
            JSONArray results = jsonObj.getJSONArray("results");

            if(results.length()>0) {
                while (i < results.length()) {
                    if (i <= 25 ) {
                        JSONObject firstResult = results.getJSONObject(i);
                        String title = firstResult.getString("title");
                       // title_list.add(title);
                        JSONObject links = firstResult.getJSONObject("_links");
                        String webui = links.getString("webui");
                        WebUI = CONFLUENCE_URL + webui;
                        webui_list.add(WebUI);
                        webui_list.add(title);
                        i++;
                    }
                }
            }
           else {
                String s1="Keyword not found";
                webui_list.add(s1);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(webui_list);


        return webui_list;

       



    }

}



