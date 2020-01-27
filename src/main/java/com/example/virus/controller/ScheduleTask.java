package com.example.virus.controller;

import com.example.virus.AppConfig;
import com.example.virus.repository.MongoDB;
import com.example.virus.utils.GeoJsonUtil;
import com.example.virus.vo.CityItem;
import com.example.virus.vo.Event;
import com.example.virus.vo.Summary;
import com.example.virus.vo.dto;
import com.example.virus.vo.feature.*;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@EnableScheduling
public class ScheduleTask {
    private static final DateFormat dt= new SimpleDateFormat("HH:mm:ss");
    private static  FeatureCollection fc =null;
    private static List<String> specifiedCities=Arrays.asList("上海","澳门","香港","台湾");

    private String handleText2(String text){
        return text.equalsIgnoreCase("")?"0":text;
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
//    @Autowired
//    MongoDB mongoDB;

    @Scheduled(fixedRate = 1000*60*5)
    public void queryFromAuthority() throws IOException {
        System.out.println ("Scheduling Tasks Examples: The time is now " + dt.format (new Date()));

        //use jsoup 获取
        Document document = Jsoup.connect("https://3g.dxy.cn/newh5/view/pneumonia").get();
        System.out.println(document.title());

        Elements title = document.select(".mapTitle___2QtRg");
        String date="";
        for (Element element : title) {
            System.out.println(element.html());
            String html = element.html();
            String[] strings = html.split(" ");
            System.out.println(String.format("%s %s", strings[1],strings[2]));
            //if it's not latest data,don't update
            date=String.format("%s %s", strings[1],strings[2]);
            date=handleText(date);
        }

        Element getListByCountryTypeService1 = document.getElementById("getListByCountryTypeService1");
        String block = getListByCountryTypeService1.html();
        int istart = getListByCountryTypeService1.html().indexOf('[');
        int iend = getListByCountryTypeService1.html().lastIndexOf(']');
        String content = block.substring(istart, iend + 1);
//        System.out.println(content);

        Summary newSummaries = new Gson().fromJson(content, Summary.class);
        System.out.println(newSummaries.size());
        fc = GeoJsonUtil.ConvertEvent2Feature3(newSummaries,date);
        GeoJsonUtil.WriteDown(AppConfig.jsonPath,fc);

//        mongoDB.persistEvents2(newSummaries);
    }

    private void handleData(String date,Document doc,String query,List<Event> events){
        List<Event> provinceList= new ArrayList<>();
        Elements targetList = doc.select(query);
        String provinceName = "";
        for (Element element : targetList) {
            Elements p = element.select(".areaBlock1___3V3UU").select("p");
            provinceName = p.get(0).html();
            int ii = provinceName.indexOf(">");
            if(ii!=-1){
                provinceName=provinceName.substring(ii+1,provinceName.length());
                System.out.println(provinceName);
            }
            for (Element city : element.select(".areaBlock2___27vn7")) {
                Event event =new Event();
                Elements data = city.select("p");


                if(data.size()==0){
                    continue;
                }
                String cityName = handleText(data.get(0).html());
                event.setName(cityName);
                event.setType("确症");
                event.setProvinceName(provinceName);
                event.setQuantity(Integer.valueOf(handleText2(data.get(1).html())));
                event.setDeath(Integer.valueOf(handleText2(data.get(2).html())));
                event.setPossible(Integer.valueOf(handleText2(data.get(3).html())));
                event.setDescription(String.format("%s 发现 %s 有确诊 %d 位病人,死亡 %d 例,治愈 %d 例",
                        date,
                        event.getName(),
                        event.getQuantity(),
                        event.getDeath(),
                        event.getPossible()
                ));

                System.out.println(event);
                //此处生成相应的geojson
                provinceList.add(event);
//                events.add(event);
            }
            FeatureCollection featureCollection = GeoJsonUtil.ConnectEventWithFeature(provinceList);
            String finalProvinceName = provinceName;
//            new Thread(()->{
                GeoJsonUtil.WriteDownFullPath2(AppConfig.jsonPath+ String.format("%scities%s%s.json",
                        File.separator,File.separator,finalProvinceName),featureCollection);
//            }).start();
            events.addAll(provinceList);
            provinceList.clear();
        }
        //
    }

    @Scheduled(fixedRate = 1000*60*6,initialDelay=1000*15)
    public void queryForCity() throws IOException{
        Document document1 = Jsoup.connect("https://3g.dxy.cn/newh5/view/pneumonia").get();

        Elements title = document1.select(".mapTitle___2QtRg");
        String date="";
        for (Element element : title) {
            System.out.println(element.html());
            String html = element.html();
            String[] strings = html.split(" ");
            System.out.println(String.format("%s %s", strings[1],strings[2]));
            //if it's not latest data,don't update
            date=String.format("%s %s", strings[1],strings[2]);
            date=handleText(date);
        }

        Element getListByCountryTypeService1 = document1.getElementById("getAreaStat");
        String block = getListByCountryTypeService1.html();
        int istart = getListByCountryTypeService1.html().indexOf('[');
        int iend = getListByCountryTypeService1.html().lastIndexOf(']');
        String content = block.substring(istart, iend + 1);
//        System.out.println(content);

        Summary newSummaries = new Gson().fromJson(content, Summary.class);
        System.out.println(newSummaries.size());
        String finalDate = date;
        newSummaries.forEach(newSummary -> {
            System.out.println(newSummary.getProvinceShortName()+"is generating");
            if (newSummary.getCities()!=null&&newSummary.getCities().size()!=0) {
                Map<String,String> data= findFeatureByProvinceName(newSummary.getProvinceShortName());
                FeatureCollection collection =new FeatureCollection();
                Feature f;
                Map<String,Object> props;
                if(newSummary.getCities().size()==0){
                    System.out.println("has no more city name"+newSummary.getProvinceShortName());
                }

                for (CityItem city : newSummary.getCities()) {
                    f=new Feature();
                    props=new HashMap<>();
                    String cityName = city.getCityName();
                    props.put("name",cityName);
                    String desc=String.format("%s 发现 %s 有确诊 %d 位病人,死亡 %d 例,治愈 %d 例,疑似 %d 例",
                            finalDate,
                            cityName,
                            city.getConfirmedCount(),
                            city.getDeadCount(),
                            city.getCuredCount(),
                            city.getSuspectedCount()
                    );
                    props.put("description",desc);
                    props.put("quantity",city.getConfirmedCount()==0?(city.getSuspectedCount()==0?0:1):city.getConfirmedCount());
                    f.setProperties(props);

                    //props block end

                    //geometry begin
                    String defaultShapeString="";
                    boolean inIfBlocked=false;
                    System.out.println("targetName is "+cityName.substring(0, 2));


                    for (Map.Entry<String, String> pair : data.entrySet()) {
//                        System.out.println(pair.getKey() + ":" + pair.getValue());
//                        System.out.println("i1"+pair.getKey());
                        if(pair.getKey().contains(cityName.substring(0,2))){
                            EnableGeomerty(f,pair.getValue());
                            inIfBlocked=true;
                            break;//found and exit
                        }else if(pair.getKey().contains("市区")){
                            defaultShapeString=pair.getValue();
                        }
                    }

                    //防止数据丢失，比如重庆九龙坡区
                    if(!inIfBlocked&&!defaultShapeString.equalsIgnoreCase("")){
                        EnableGeomerty(f,defaultShapeString);
                    }

                    //add to the featureCollection and write down
                    collection.addFeature(f);

                    if(inIfBlocked){
                        System.out.println("found  the geometry data");
                    }else{
                        System.out.println("not found"+defaultShapeString);
                    }
                }
                //end of the feature collection construct and write it down
                String filePath = String.format("%s%scities%s%s.json",
                        AppConfig.jsonPath,
                        File.separator,
                        File.separator,
                        newSummary.getProvinceShortName());
                GeoJsonUtil.WriteDownFullPath2(filePath,collection);
            }

        });
//        mongoDB.persistCityEvents2(newSummaries);
        //only specified for four cities

        if(fc!=null){
            for (Feature feature : fc.getFeatures()) {
                String name = feature.getProperties().get("name").toString();
                FeatureCollection specified =new FeatureCollection();
                if(specifiedCities.contains(name)){
                    String filePath = String.format("%s%scities%s%s.json",
                            AppConfig.jsonPath,
                            File.separator,
                            File.separator,
                            name);
                    specified.addFeature(feature);
                    GeoJsonUtil.WriteDownFullPath2(filePath,specified);
                }
            }
        }
    }


    private void EnableGeomerty(Feature f,String shapeString){
        Geometry geometry =null;
        if(shapeString.contains("MultiPolygon")){
            geometry=new Gson().fromJson(shapeString, MultiPolygon.class);
        }else   if (shapeString.contains("Polygon")) {
            geometry=new Gson().fromJson(shapeString, Polygon.class);
        }

        if(geometry!=null){
            f.setGeometry(geometry);
        }else{
            throw  new RuntimeException(f.getProperties().get("name")+"has no geometry data");
        }
    }

    private Map<String, String> findFeatureByProvinceName(String provinceShortName) {
        String targetPath = String.format("%s%sprovinces%s%s.json", AppConfig.jsonPath,File.separator,File.separator, provinceShortName);
        System.out.println(targetPath);
        File file = new File(targetPath);
        if(!file.exists()){
            System.out.println(String.format("%s file not found", targetPath));
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String all =IOUtils.toString(fileInputStream, StandardCharsets.UTF_8.name());

            dto dto = new Gson().fromJson(all, dto.class);
            Map<String, String> data = dto.getData();
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
