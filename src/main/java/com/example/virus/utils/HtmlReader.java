package com.example.virus.utils;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.nio.charset.MalformedInputException;

public class HtmlReader {
    private static class innerWebClient{
        private static final  WebClient webClient = new WebClient();
    }


    public static HtmlPage getHtmlPage(String url) throws InterruptedException {
        WebClient webClient = innerWebClient.webClient;
        webClient.setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.setActiveXNative(false);
        webClient.setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
        webClient.setJavaScriptEnabled(true); //很重要，启用JS
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
        HtmlPage page = null;
        try{
            page =(HtmlPage) webClient.getPage(url);
        }catch (MalformedInputException e){
            e.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }

        webClient.waitForBackgroundJavaScript(10000);
        Thread.sleep(3000);
        return  page;
    }
}
