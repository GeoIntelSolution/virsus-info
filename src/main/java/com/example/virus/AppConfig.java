package com.example.virus;

import com.example.virus.vo.dto;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class AppConfig {
    public static String jsonPath;
    public static dto dto=null;


    public static dto getDTO(){
        if(dto==null){
            try {
                File file = new File(AppConfig.jsonPath + "/province.json");

                List<String> strings = IOUtils.readLines(new FileInputStream(file), Charset.forName("utf-8"));
                StringWriter sw =new StringWriter();
                for (String string : strings) {
                    sw.append(string);
                }
                dto = new Gson().fromJson(sw.toString(), dto.class);
                for (Map.Entry<String, String> stringStringEntry : dto.getData().entrySet()) {
                    System.out.println(stringStringEntry.getKey() );
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }

        return dto;
    }
}
