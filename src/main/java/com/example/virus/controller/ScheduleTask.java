package com.example.virus.controller;

import com.example.virus.AppConfig;
import com.example.virus.repository.MongoDB;
import com.example.virus.utils.GeoJsonUtil;
import com.example.virus.vo.Event;
import com.example.virus.vo.feature.FeatureCollection;
import com.google.gson.Gson;
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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@EnableScheduling
public class ScheduleTask {
    private static final DateFormat dt= new SimpleDateFormat("HH:mm:ss");

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
    @Autowired
    MongoDB mongoDB;

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
        }

        Elements dt = document.select(".areaBlock1___3V3UU");
//        TaskInfo taskInfo =new TaskInfo()
        List<Event> eventList=new ArrayList<>();

        String name="";
        for (Element element : dt) {
            if(element==dt.get(0)){
                continue;//skip first row
            }
//            System.out.println(element.html());
            Elements p = element.select("p");
            Event event =new Event();
            event.setUpdateTime(date);

            Element province = p.get(0);
            name=province.html();
            int i = name.indexOf(">");
            if(i!=-1){
                name=name.substring(i+1,name.length());
                System.out.println(name);
                event.setName(name);
                event.setType("确诊");
            }

            Element data = p.get(1);
            Integer integer = Integer.valueOf(data.html());
            event.setQuantity(integer);

            event.setDescription(String.format("发现 %s 有确诊 %d 病人", event.getName(),event.getQuantity()));
            eventList.add(event);

        }
        FeatureCollection featureCollection = GeoJsonUtil.ConvertEvent2Feature(eventList);
        GeoJsonUtil.WriteDown(AppConfig.jsonPath,featureCollection);

        mongoDB.persistEvents(eventList);
    }

//    @RequestMapping(name = "/",method = RequestMethod.GET)
//    public String index(){
//        return new Gson().toJson("hello world");
//    }
}
