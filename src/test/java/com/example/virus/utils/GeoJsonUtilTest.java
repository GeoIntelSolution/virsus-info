package com.example.virus.utils;

import com.example.virus.vo.Event;
import com.example.virus.vo.dto;
import com.example.virus.vo.feature.Feature;
import com.example.virus.vo.feature.Geometry;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.*;

public class GeoJsonUtilTest {
    @Test
    public void testGeojson() throws IOException {
        GeoJsonUtil geoJsonUtil = new GeoJsonUtil();
        InputStream resourceAsStream = GeoJsonUtil.class.getClassLoader().getResourceAsStream("Event");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line=null;
        StringWriter sw =new StringWriter();
        while ((line=bufferedReader.readLine())!=null){
            sw.append(line);
        }

        Event event = new Gson().fromJson(sw.toString(), Event.class);
        Feature feature = geoJsonUtil.ConvertEvent2Feature(event);
        String s = new Gson().toJson(feature);
        System.out.println(s);


    }

    private String handleText(String text){
        int i = text.indexOf("<span");
        while (i!=-1){
            int i1 = text.indexOf(">", i);
            text=text.substring(0,i)+text.substring(i1+1,text.length());
//            System.out.println(text);
            i = text.indexOf("<span");
        }
//        text.replaceAll("</span>","");
        i=text.indexOf("</span");
        while (i!=-1){
            int i1 = text.indexOf(">", i);
            text=text.substring(0,i)+text.substring(i1+1,text.length());
//            System.out.println(text);
            i = text.indexOf("</span");
        }

       return text;
    }
    @Test
    public void readata(){
        String text="广西 <span>确诊 <span style=\\\"color: #4169e2\\\">2</span> 例，疑似 <span style=\\\"color: #4169e2\\\">1</span> 例</span>";
        System.out.println(handleText(text));
    }

    @Test
    public void convertEvent2Feature() {
    }

    @Test
    public void testConvertEvent2Feature() {
    }

    @Test
    public void writeDown() {
    }

    @Test
    public void getAllProvince() {
        Map<String, String> allProvince = GeoJsonUtil.getAllProvince();
        dto dto = new dto();
        dto.setData(allProvince);
        for (Map.Entry<String, String> stringGeometryEntry : allProvince.entrySet()) {
            System.out.println(stringGeometryEntry.getKey()+" "+stringGeometryEntry.getValue());
        }
        File file =new File("D:\\source\\virus\\virus\\src\\main\\resources\\static\\province.json");
        String s = new Gson().toJson(dto);
        try {
            FileOutputStream metaWriter = new FileOutputStream(file);
            IOUtils.write(s,metaWriter, StandardCharsets.UTF_8.name());
            metaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}