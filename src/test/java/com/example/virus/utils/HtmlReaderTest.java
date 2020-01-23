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
        Elements dt = document.select(".mapTitle___2QtRg");
//        TaskInfo taskInfo =new TaskInfo()
        String name="";
        for (Element element : dt) {
            System.out.println(element.html());
            String html = element.html();
            String[] strings = html.split(" ");
            System.out.println(String.format("%s %s", strings[1],strings[2]));
            //if it's not latest data,don't update
            name=String.format("%s %s", strings[1],strings[2]);
        }

        //获取消息

        Elements details = document.select(".descList___3iOuI");
        List<Event> eventList=new ArrayList<>();
        for (Element detail : details) {
            Event event =new Event();
            event.setUpdateTime(name);
            for (Element child : detail.children()) {
//                System.out.println(child.html());
                String childText = child.html();
                int i = childText.indexOf("</i>");
                if(i==-1) continue;
                String text = childText.substring(i+4, childText.length());
                System.out.println(text);
                String[] s = text.split(" ");
                event.setName(s[0]);
                event.setType(s[1]);
                event.setQuantity(Integer.valueOf(s[2]));
                event.setDescription(text);
                eventList.add(event);
            }
        }
        //create geojson

        eventList.forEach(event -> {
            System.out.println(event);
        });



    }
}