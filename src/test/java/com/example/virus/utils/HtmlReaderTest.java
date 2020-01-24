package com.example.virus.utils;

import com.example.virus.vo.Event;
import com.example.virus.vo.TaskInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import java.util.List;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class HtmlReaderTest {
    @Test
    public void readFile() throws IOException {
        File file =new File("D:/javahere/demo/imgs/1.html");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String len="";
        StringWriter sw =new StringWriter();
        while ((len=bufferedReader.readLine())!=null){
//            System.out.println(len);
            sw.append(len);
        }
        String result =sw.toString();
//        System.out.println(result);

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

        //获取消息



        //create geojson

        eventList.forEach(event -> {
            System.out.println(event);
        });



    }
}