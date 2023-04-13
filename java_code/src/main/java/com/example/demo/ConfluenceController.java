package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import org.json.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api")
public class ConfluenceController {


    private static final String CONFLUENCE_URL = "https://lavanyathirumalaisamy.atlassian.net/wiki";
    private static final String CONFLUENCE_USERNAME = "lavanyaramya1947@gmail.com";
    private static final String CONFLUENCE_PASSWORD = "ATATT3xFfGF0iXxDdUj39DEksNsMos9GNgCyyzdbg9owurB-voD9WEazHbgmtDNMSvtmDgWL2F_qn9gdjbjKJtXIxuhoOs8vOyMIEfs1R8Enp5Yd9idfkkjMrJDPYmekrpV6r3ANVbllHQ_NE1nkuSqE1nUSYfjztmJWgjduI83KP7kcE8EkGmI=09B5D78C";



    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/confluence")
    public List<String> search(@RequestParam("input") String keyword) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(CONFLUENCE_USERNAME, CONFLUENCE_PASSWORD));
        String searcConfluencehUrl = CONFLUENCE_URL + "/rest/api/content/search?cql=type=page+and+text~'" + keyword + "'";
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



