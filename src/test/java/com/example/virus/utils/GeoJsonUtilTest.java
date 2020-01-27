package com.example.virus.utils;

import com.example.virus.AppConfig;
import com.example.virus.vo.Event;
import com.example.virus.vo.SourceItem;
import com.example.virus.vo.Sources;
import com.example.virus.vo.dto;
import com.example.virus.vo.feature.Feature;
import com.example.virus.vo.feature.Geometry;
import com.google.gson.Gson;

import java.util.*;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    @Test
    public void getCitesByProvince() {
        Map<String, String> provinceMap = GeoJsonUtil.getAllProvince();
        for (Map.Entry<String, String> pro : provinceMap.entrySet()) {
            String provinceName = pro.getKey();
            Map<String, String> allProvince = GeoJsonUtil.getCitesByProvince(provinceName);
            System.out.println(allProvince.size());
            dto dto =new dto();
            dto.setData(allProvince);
//            for (Map.Entry<String, String> stringStringEntry : allProvince.entrySet()) {
//                System.out.println(stringStringEntry.getKey()+" "+stringStringEntry.getValue());
//            }
            File file =new File(String.format("D:\\source\\virus\\virus\\src\\main\\resources\\static\\%s.json", provinceName));
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

    @Test
    public void connectEventWithFeature() throws IOException {
        GeoJsonUtil geoJsonUtil = new GeoJsonUtil();
        InputStream resourceAsStream = GeoJsonUtil.class.getClassLoader().getResourceAsStream("Event2");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line=null;
        StringWriter sw =new StringWriter();
        while ((line=bufferedReader.readLine())!=null){
            sw.append(line);
        }
        Event event = new Gson().fromJson(sw.toString(), Event.class);
        AppConfig.jsonPath="D:\\source\\virusmap\\public\\json";
        event.setProvinceName("湖北");
        Feature feature = GeoJsonUtil.ConnectEventWithFeature(event.getProvinceName(),event);
        System.out.println(feature);
        System.out.println(new Gson().toJson(feature));
    }

    @Test
    public  void testReadData(){
//        String cname="重庆";
//        String tname="chongqing";
        Map<String,String> maps =new HashMap<>();
        maps.put("重庆","chongqing");
        maps.put("北京","beijing");
        maps.put("天津","tianjin");
        maps.put("上海","shanghai");
        for (Map.Entry<String, String> stringStringEntryq : maps.entrySet()) {
            String cname=stringStringEntryq.getKey();
            String tname=stringStringEntryq.getValue();
            Map<String, String> ok = GeoJsonUtil.getCitesByCity(cname,tname);
            for (Map.Entry<String, String> stringStringEntry : ok.entrySet()) {
                System.out.println(stringStringEntry.getKey());
            }
            dto dto = new dto();
            dto.setData(ok);
            System.out.println(new Gson().toJson(dto));
//        String path= String.format("D:\\source\\virus\\virus\\src\\main\\resources\\static\\%s.json", cname);
            File file =new File(String.format("D:\\source\\virus\\virus\\src\\main\\resources\\static\\special\\%s.json", cname));
            String s = new Gson().toJson(dto);
            try {
                FileOutputStream metaWriter = new FileOutputStream(file);
                IOUtils.write(s,metaWriter, StandardCharsets.UTF_8.name());
                metaWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        GeoJsonUtil.WriteDown();
    }

    @Test
    public void getDataName(){
        String tname="gadm36_chn_2";
        Map<String, String> provinceMap = GeoJsonUtil.getAllProvince();
        for (Map.Entry<String, String> pro : provinceMap.entrySet()) {
            String cname = pro.getKey().substring(0,2);

            Map<String, String> ok = GeoJsonUtil.getCitesByCity2(cname,tname);

            System.out.println(cname+""+ok.entrySet().size());
            for (Map.Entry<String, String> stringStringEntry : ok.entrySet()) {
                System.out.println(stringStringEntry.getKey());
            }
            dto dto = new dto();
            dto.setData(ok);
//            System.out.println(new Gson().toJson(dto));
//        String path= String.format("D:\\source\\virus\\virus\\src\\main\\resources\\static\\%s.json", cname);
            File file =new File(String.format("D:\\source\\virus\\virus\\src\\main\\resources\\static\\special\\%s.json", cname));
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


    @Test
    public void WriteSource(){
        Sources sources = new Sources();
        Map<String, SourceItem> data =new HashMap<>();
        Map<String, String> provinceMap = GeoJsonUtil.getAllProvince();
        for (Map.Entry<String, String> pro : provinceMap.entrySet()) {
            String cname = pro.getKey().substring(0, 2);
            data.put(cname,new SourceItem("geojson", String.format("http://localhost:8081/%s.json", cname)));
        }

        File file =new File("D:\\source\\virus\\virus\\src\\main\\resources\\static\\styles\\sources1.json");
        String s = new Gson().toJson(data);
        try {
            FileOutputStream metaWriter = new FileOutputStream(file);
            IOUtils.write(s,metaWriter, StandardCharsets.UTF_8.name());
            metaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WriteLayer() throws IOException {
        File file = new File("D:\\source\\virus\\virus\\src\\main\\resources\\static\\styles\\temp1.txt");
        BufferedReader reader =new BufferedReader(new FileReader(file));
        String line =null;
        StringWriter sw =new StringWriter();
        while ((line=reader.readLine())!=null){
            sw.append(line);
        }
        List<String> strings =new ArrayList<>();
        String content = sw.toString();
        Map<String, String> provinceMap = GeoJsonUtil.getAllProvince();
        for (Map.Entry<String, String> pro : provinceMap.entrySet()) {
            String cname = pro.getKey().substring(0, 2);
            String temp1 = content.replace("安徽", cname);
            String replace = temp1.replace("virus-city", cname);
            strings.add(replace);
        }


        String all = String.join(",", strings);
        File file1=new File("D:\\source\\virus\\virus\\src\\main\\resources\\static\\styles\\layers.json");
        try {
            FileOutputStream metaWriter = new FileOutputStream(file1);
            IOUtils.write(all,metaWriter, StandardCharsets.UTF_8.name());
            metaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Test
    public void testSet(){

    }
}