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
            date=handleText(date);
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
            Integer integer = Integer.valueOf(handleText2(data.html()));
            event.setQuantity(integer);
            Element death = p.get(2);
            Integer ideath = Integer.valueOf(handleText2(death.html()));
            event.setDeath(ideath);

            Element posible = p.get(3);
            Integer iposible = Integer.valueOf(handleText2(posible.html()));
            event.setPossible(iposible);
//            Elements select = dt.select(".remark___242Bm");
//            System.out.println(select.html());
            event.setDescription(String.format("%s 发现 %s 有确诊 %d 位病人,死亡 %d 例,治愈 %d 例",
                    date,
                    event.getName(),
                    event.getQuantity(),
                    ideath,
                    iposible
                    ));
            eventList.add(event);

        }
        FeatureCollection featureCollection = GeoJsonUtil.ConvertEvent2Feature(eventList);
        GeoJsonUtil.WriteDown(AppConfig.jsonPath,featureCollection);

        mongoDB.persistEvents(eventList);
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
                GeoJsonUtil.WriteDownFullPath2(AppConfig.jsonPath+ String.format("\\cities\\%s.json",
                        finalProvinceName),featureCollection);
//            }).start();
            events.addAll(provinceList);
            provinceList.clear();
        }
        //
    }

    @Scheduled(fixedRate = 1000*60*5)
    public void queryForCity() throws IOException{
        Document document = Jsoup.connect("https://3g.dxy.cn/newh5/view/pneumonia").get();
        //获取必要数据
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
        //省份
        List<Event> events =new ArrayList<>();
        handleData(date,document,".expand___wz_07",events);

        handleData(date,document,".fold___xVOZX",events);
        mongoDB.persistCityEvents(events);
    }

//    @RequestMapping(name = "/",method = RequestMethod.GET)
//    public String index(){
//        return new Gson().toJson("hello world");
//    }
}
