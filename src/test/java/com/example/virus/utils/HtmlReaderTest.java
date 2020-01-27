package com.example.virus.utils;

import com.example.virus.vo.*;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.MouseEvent;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.nio.charset.Charset;
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
        Element root = document.select(".areaBox___3jZkr").get(0);
        Elements dt = root.select(".areaBlock1___3V3UU");
        Elements expands = dt.select(".expand___wz_07");
//        Elements remark = expand.select(".remark___242Bm");
//        TaskInfo taskInfo =new TaskInfo()
        List<Event> eventList=new ArrayList<>();

        String name="";
        int ii=0;
        for (Element element : dt) {
            if(element==dt.get(0)){
                continue;//skip first row
            }
            ii++;

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
            Elements select = dt.select(".remark___242Bm");
            System.out.println(select.html());
            event.setDescription(String.format("发现 %s 有确诊 %d 病人", event.getName(),event.getQuantity()));
            eventList.add(event);

        }

        eventList.forEach(event -> {
            System.out.println(event);
        });



    }


    @Test
    public void readHtmlScript() throws IOException {
        Document document1 = Jsoup.connect("https://3g.dxy.cn/newh5/view/pneumonia").get();
        Element getListByCountryTypeService1 = document1.getElementById("getListByCountryTypeService1");
        String block = getListByCountryTypeService1.html();
        int istart = getListByCountryTypeService1.html().indexOf('[');
        int iend = getListByCountryTypeService1.html().lastIndexOf(']');
        String content = block.substring(istart, iend + 1);
        System.out.println(content);

        Summary newSummaries = new Gson().fromJson(content, Summary.class);
        System.out.println(newSummaries.size());

    }

    @Test
    public void testMockHtmlClick() throws IOException, InterruptedException {
        // 获取指定网页实体
        HtmlPage page = HtmlReader.getHtmlPage("https://3g.dxy.cn/newh5/view/pneumonia");
        Document document1 = Jsoup.connect("https://3g.dxy.cn/newh5/view/pneumonia").get();
        File file1 = new File("b.txt");
        FileOutputStream fileOutputStream1 = new FileOutputStream(file1);
        IOUtils.write(document1.html(),fileOutputStream1, Charset.forName("utf-8"));


        List<HtmlElement> divLists=null;
//      List<HtmlElement> divLists =(List<HtmlElement>)page.getByXPath("div[@class='areaBlock1___3V3UU']");
        divLists = (List<HtmlElement>) page.getByXPath("//div[contains(@class, 'fold___xVOZX')]");
        System.out.println(divLists.size());


        Page click1 = divLists.get(13).click();
        HtmlPage ok = (HtmlPage) click1;
//        System.out.println(ok.asXml());
        File file = new File("a.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        IOUtils.write(ok.asXml(),fileOutputStream, Charset.forName("utf-8"));

        Document document = Jsoup.parse(ok.asXml());
        Elements areaBlock2___27vn7 = document.getElementsByClass("areaBlock2___27vn7");
        for (Element element : areaBlock2___27vn7) {
            System.out.println(element.html());
            Element city = element.getElementsByClass("subBlock1___j0DGa").get(0);
            System.out.println(city.html());
            Element quantity = element.getElementsByClass("subBlock2___E7-fW").get(0);
            System.out.println(quantity.html());
            Element death = element.getElementsByClass("subBlock4___ANk6l").get(0);
            System.out.println(death.html());
            Element heal = element.getElementsByClass("subBlock3___3mcDz").get(0);
            System.out.println(heal.html());
        }
//        System.out.println(ok.getByXPath("//div[contains(@class,'areaBlock2___27vn7')").size());
//        Document Document = Jsoup.parse(pageXml);



    }


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


    private void handleData(String date,Document doc,String query,List<Event> events){
        Elements targetList = doc.select(query);
        for (Element element : targetList) {
            Elements p = element.select(".areaBlock1___3V3UU").select("p");
            String provinceName = p.get(0).html();
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

                events.add(event);
            }
        }
    }
    @Test
    public void testReadData() throws IOException{
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



    }

    @Test
    public void testReadCityData() throws IOException{
        Document document1 = Jsoup.connect("https://3g.dxy.cn/newh5/view/pneumonia").get();
        Element getListByCountryTypeService1 = document1.getElementById("getAreaStat");
        String block = getListByCountryTypeService1.html();
        int istart = getListByCountryTypeService1.html().indexOf('[');
        int iend = getListByCountryTypeService1.html().lastIndexOf(']');
        String content = block.substring(istart, iend + 1);
        System.out.println(content);

        Summary newSummaries = new Gson().fromJson(content, Summary.class);
        System.out.println(newSummaries.size());



    }
}