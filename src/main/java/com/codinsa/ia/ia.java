package com.codinsa.ia;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ia{
    private final String USER_AGENT = "Mozilla/5.0";
    private final String SERVER_ADRESS = "http://localhost:8080/";

    public static void main(String[] args) throws Exception {

        ia http = new ia();

        System.out.println("Init Game *****************");
        Map<String,String> m= new HashMap<>();
        m.put("IAName","LOL");
        String jsonTest=http.sendPost("/IA/Join",m,"");

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(jsonTest);
        JSONObject token = (JSONObject) obj;
        String token1=token.get("token").toString();

        System.out.println("Join example");
        System.out.println(token.get("id"));
        System.out.println(token.get("status"));
        System.out.println(token1);
        System.out.println();

        m.put("IAName","LOL2");
        jsonTest=http.sendPost("/IA/Join",m,"");
        token = (JSONObject) parser.parse(jsonTest);
        String token2=token.get("token").toString();
        m.clear();

        //String resultInit=http.sendGet("Start/Game",m,"");
        m.put("Token",token1);
        boolean stop=false;
        do{
            String resultWait=http.sendGet("Wait",m,"");
            obj = parser.parse(resultWait);
            JSONObject waitObject = (JSONObject) obj;
            System.out.println(resultWait);
            String wait=waitObject.get("wait").toString();
            stop=(wait.equals("true"));
            System.out.println("Attente OK");
            Thread.sleep(1000);
        }while(!stop);

        System.out.println("Start Game *****************");
        System.out.println("Game Board*****************");
        String resultBoard=http.sendGet("Get/Board",m,"");

        System.out.println(resultBoard);
        String resultVisible=http.sendGet("Get/Visible",m,"");
        System.out.println(resultVisible);
        m.clear();

        m.put("Token",token2);
        resultVisible=http.sendGet("Get/Visible",m,"");
        System.out.println(resultVisible);
        m.clear();

        m.put("Token",token1);
        //String resultAction=http.sendPost("PlayAction",m,"[{\"owner\":1,\"from\":0,\"to\":2,\"qtCode\":15}]\n");
        String resultAction=http.sendPost("PlayAction",m,"[]\n");
        System.out.println(resultAction);
        int res = 0;
        while(res < 300)
        {
            m.clear();
            m.put("Token",token1);
            resultAction=http.sendGet("Get/Turn",m,"");
            System.out.println(resultAction);

        }
        String resultEnd=http.sendPost("End/Turn",m,"");
        System.out.println(resultEnd);


        m.put("Token",token2);
        String resultAction2=http.sendPost("PlayAction",m,"[{\"owner\":1,\"from\":0,\"to\":2,\"qtCode\":15}]\n");
        //String resultAction=http.sendPost("PlayAction",m,"[]\n");
        System.out.println(resultAction2);

        resultEnd=http.sendPost("End/Turn",m,"");
        System.out.println(resultEnd);

    }

    // HTTP GET request
    private String sendGet(String endpoint, Map<String,String> param, String body) throws Exception {

        StringBuilder stringBuilder = new StringBuilder(SERVER_ADRESS+endpoint);
        stringBuilder.append("?");
        for (Map.Entry<String, String> entry : param.entrySet()) {
            stringBuilder.append(URLEncoder.encode(entry.getKey(),"UTF-8") + "=" + URLEncoder.encode(entry.getValue(),"UTF-8")+"&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        URL obj = new URL(stringBuilder.toString());

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Charset", "UTF-8");

        System.out.println("\nSending request to URL : " + stringBuilder.toString());
        System.out.println("Response Code : " + con.getResponseCode());
        System.out.println("Response Message : " + con.getResponseMessage());

        if(body!=null&&!body.isEmpty()){
            con.setDoOutput(true);
            OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
            wr.write(body);
        }

        BufferedReader in = new BufferedReader(
        new InputStreamReader(con.getInputStream()));
        String line;
        StringBuffer response = new StringBuffer();

        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();

    }

    private String sendPost(String endpoint, Map<String,String> param, String body) throws Exception {

        StringBuilder stringBuilder = new StringBuilder(SERVER_ADRESS+endpoint);
        stringBuilder.append("?");
        for (Map.Entry<String, String> entry : param.entrySet()) {
            stringBuilder.append(URLEncoder.encode(entry.getKey(),"UTF-8") + "=" + URLEncoder.encode(entry.getValue(),"UTF-8")+"&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        URL obj = new URL(stringBuilder.toString());

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "UTF-8");
        if(body!=null&&!body.isEmpty()){
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();
        }

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + endpoint);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
