package com.example.virus.utils;

import com.example.virus.AppConfig;
import com.example.virus.vo.Event;
import com.example.virus.vo.dto;
import com.example.virus.vo.feature.*;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class GeoJsonUtil {

    private static String handleSuffix(String text){
        String nameWithoutSuffix=null;
        if (text.contains("省")) {
            nameWithoutSuffix=text.replace("省","");
        }else if(text.contains("市")){
            nameWithoutSuffix=text.replace("市","");
        }else if(text.contains("自治区")){
            nameWithoutSuffix=text.replace("自治区","");
        }else{
            nameWithoutSuffix=text;
        }

        return nameWithoutSuffix;
    }

    public static Feature ConvertEvent2Feature2(Event event){
        System.out.println(event.getName());
        String nameWithoutSuffix=null;
        nameWithoutSuffix=handleSuffix(event.getName());
        Feature result =new Feature();
        Map<String,Object> props =new HashMap<>();
        props.put("name",event.getName());
        props.put("updateTime",event.getUpdateTime());
        props.put("description",event.getDescription());
        props.put("type",event.getType());
        props.put("quantity",event.getQuantity());
        result.setProperties(props);

        Map<String, String> data = AppConfig.getDTO().getData();
        String  shapeString= data.get(nameWithoutSuffix);
        Geometry geometry =null;
        if(shapeString.contains("MultiPolygon")){
            geometry=new Gson().fromJson(shapeString, MultiPolygon.class);
        }else   if (shapeString.contains("Polygon")) {
            geometry=new Gson().fromJson(shapeString, Polygon.class);
        }

        if(geometry!=null){
            result.setGeometry(geometry);
            return result;
        }else{
            throw  new RuntimeException(nameWithoutSuffix+"has no geometry data");
        }
    }
    public static Feature ConvertEvent2Feature(Event event) {
        System.out.println(event.getName());
        String nameWithoutSuffix=null;
        if (event.getName().contains("省")) {
            nameWithoutSuffix=event.getName().replace("省","");
        }else if(event.getName().contains("市")){
            nameWithoutSuffix=event.getName().replace("市","");
        }else if(event.getName().contains("自治区")){
            nameWithoutSuffix=event.getName().replace("自治区","");
        }else{
             nameWithoutSuffix=event.getName();
        }
        Feature result =new Feature();
        Map<String,Object> props =new HashMap<>();
        props.put("name",event.getName());
        props.put("updateTime",event.getUpdateTime());
        props.put("description",event.getDescription());
        props.put("type",event.getType());
        props.put("quantity",event.getQuantity());

        result.setProperties(props);

        //fetch geojson

        //todo: read this only once;
        try{
            Class.forName("org.postgis.DriverWrapper");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/admin","postgres","gis3857");
            Statement statement = connection.createStatement();
            String sql = String.format("select st_asgeojson(st_forcepolygonccw(ST_SimplifyPreserveTopology(geom,0.001))) as data from china where nl_name_1 ~* '%s'", nameWithoutSuffix);
            ResultSet resultSet = statement.executeQuery(sql);
            String shapeString=null;
            while (resultSet.next()){
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    shapeString = resultSet.getString(i);
                }
            }

            if(shapeString==null||shapeString.equalsIgnoreCase("")){
                System.out.println(sql);
            }else{

                if(shapeString.contains("MultiPolygon")){
                    result.setGeometry(new Gson().fromJson(shapeString, MultiPolygon.class));
                }else   if (shapeString.contains("Polygon")) {
                    result.setGeometry(new Gson().fromJson(shapeString, Polygon.class));
                }
            }




        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static FeatureCollection ConvertEvent2Feature(List<Event> events){
        FeatureCollection featureCollection =new FeatureCollection();
        events.forEach(event -> {
            featureCollection.addFeature(GeoJsonUtil.ConvertEvent2Feature2(event));
        });
        return featureCollection;
    }


    public static void WriteDown(String filePath,FeatureCollection featureCollection){
        File file =new File(filePath+"/latest.json");
        String s = new Gson().toJson(featureCollection);
        try {
            FileOutputStream metaWriter = new FileOutputStream(file);
            IOUtils.write(s,metaWriter, StandardCharsets.UTF_8.name());
            metaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String,String> getAllProvince(){

        Map<String,String> result =new HashMap<>();
        try{
            Class.forName("org.postgis.DriverWrapper");
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/admin","postgres","gis3857");
            Statement statement = connection.createStatement();
            String sql = String.format("select st_asgeojson(st_forcepolygonccw(ST_SimplifyPreserveTopology(geom,0.001))) as data ,nl_name_1 as name from china ");
            ResultSet resultSet = statement.executeQuery(sql);
            String shapeString=null,name=null;
            while (resultSet.next()){
                    shapeString = resultSet.getString("data");
                    name=resultSet.getString("name");
                    if(name.indexOf("|")!=-1){
                        name=name.split("\\|")[1];
                        name=handleSuffix(name);
                    }
                    if(shapeString==null||shapeString.equalsIgnoreCase("")){
                        System.out.println(sql);
                    }else{
                        result.put(name,shapeString);
//                        if(shapeString.contains("MultiPolygon")){
//                            result.put(name,new Gson().fromJson(shapeString, MultiPolygon.class));
//                        }else   if (shapeString.contains("Polygon")) {
//                            result.put(name,new Gson().fromJson(shapeString, Polygon.class));
//                        }
                    }
            }






        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
