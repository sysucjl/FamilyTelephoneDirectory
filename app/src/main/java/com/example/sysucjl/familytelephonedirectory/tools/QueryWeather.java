package com.example.sysucjl.familytelephonedirectory.tools;

import com.example.sysucjl.familytelephonedirectory.data.WeatherInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/4/7.
 */
public class QueryWeather {
    public WeatherInfo query(String num) throws Exception {
        String theUrl=String.format("http://ws.webxml.com.cn/WebServices/WeatherWS.asmx/getWeather?theCityCode=%s&theUserID=", num);
        //设置URL
        URL url = new URL(theUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");

        if (conn.getResponseCode() == 200)
            return parse(conn.getInputStream());
        return null;
    }


    private WeatherInfo parse(InputStream inputStream) throws Exception {
        WeatherInfo weatherInfo = new WeatherInfo();
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(inputStream));
        StringBuilder response = new StringBuilder();
        String line;


        line = reader.readLine(); // 第1行，没用
        line = reader.readLine(); // 第2行，没用
        line = reader.readLine(); weatherInfo.cityName = getLineValue(line).replace(" ", "");  // 第3行，省份城市
        line = reader.readLine(); // 第4行，没用
        line = reader.readLine(); weatherInfo.cityCode = getLineValue(line);  // 第5行，城市代码
        line = reader.readLine();
        // weatherInfo.date = getLineValue(line);  // 第6行，日期
        line = reader.readLine(); weatherInfo.liveWeather = getLineValue(line).substring(7);  // 第7行，天气实况
        weatherInfo.curTem = line.substring(line.indexOf("气温："), line.indexOf("；")); // 提取当前气温
        line = reader.readLine(); // 第8行，没用
        line = reader.readLine(); weatherInfo.UVI = getLineValue(line);  // 第9行，紫外线指数
        line = reader.readLine(); weatherInfo.CI = line;  // 第10行，感冒指数
        line = reader.readLine(); weatherInfo.DI = line;  // 第11行，穿衣指数
        line = reader.readLine(); weatherInfo.CWI = line;  // 第12行，洗车指数
        line = reader.readLine(); weatherInfo.MI = line;  // 第13行，运动指数
        line = reader.readLine(); weatherInfo.API = line;  // 第14行，空气污染指数
        line = reader.readLine(); // 第15行，没用
        line = reader.readLine();   // 第16行，天气
        int a = line.indexOf(">");
        int b = line.indexOf(" ", a);
        weatherInfo.date = line.substring(a + 1, b);// 提取日期信息
        a = line.indexOf(" ", a);
        b = line.indexOf("<", a);
        weatherInfo.weather = line.substring(a + 1, b);// 提取天气信息
        line = reader.readLine(); weatherInfo.tem = getLineValue(line);  // 第17行，气温
        line = reader.readLine(); weatherInfo.wind = getLineValue(line);  // 第18行，风向
        line = reader.readLine();
        weatherInfo.gif1 = getLineValue(line).substring(0, getLineValue(line).indexOf(".")).trim(); // 第一个天气图片
        line = reader.readLine();
        weatherInfo.gif2 = getLineValue(line).substring(0, getLineValue(line).indexOf(".")).trim();;   //第二个天气图片
        return  weatherInfo;
    }

    private String getLineValue(String s)
    {
        String s1[] = s.split(">");
        String s2[] = s1[1].split("<");
        return s2[0];
    }
}