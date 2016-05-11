package com.example.sysucjl.familytelephonedirectory.tools;

//INPUT:最低温 最高温 (最大)风力 天气（e.g.多云转阵雨） 对方姓名 所在城市
//OUTPUT:天气短信（String)

import com.example.sysucjl.familytelephonedirectory.data.WeatherInfo;

import java.util.Random;

/**
 * Created by Administrator on 2016/5/7.
 */
public class CareSMS {
    static int TEMP1,TEMP2,WIND;
    static String weather = new String();
    static String NAME,CITY;
    static String mess = new String();
    static boolean isRainy = false,isSnowy = false,isFoggy = false,isCloudy = false,isSunny = false;
    static String hotA[] = {"温度有点高，","感觉有点热了，","气温挺高的，","最高温超过28℃了，"};
    static String hotB[] = {"记得要多喝水。","在室外要注意防晒。","不要长时间待在室外。","如果太热的话可以开空调啦！","可以多吃一些水果。","小心别中暑了。","午后可以多休息一会。","可以喝一些清凉的饮料。","注意防暑降温。"};
    static String Rainy[] = {"雨天路滑，走路要小心","雨天出行要注意安全","出门记得带伞","雨天出门最好穿双不易湿的鞋子","雨天走路要小心","记得随身带把伞","下雨天路上要格外小心","别忘了带伞哦","记得带伞，记得带伞，记得带伞，重要的事情说三遍"};
    static String Foggy[] = {"雾天出行要注意安全","雾天视线不好要小心","雾气太大路上要注意安全","雾天走路要格外小心"};
    static String Snowy[] = {"下雪天出行要注意安全","雪天挺冷的，要多穿件衣服","出门记得戴围巾和手套","要做好保暖防寒工作"};
    static String Cloudy[] = {"看起来应该不会下雨","可以放心出门啦","应该是不会下雨的"};
    static String Windy[] = {"风有些大。","阳台的花草或者衣服记得收好。","如果风沙太大，尽量不要外出。"};
    static String Sunny[] = {"又是一个美好的晴天","晴朗的天气总是让人觉得很开心","让太阳晒干所有的阴郁吧","可以洗洗衣物晒晒被子哦","好好享受好天气吧","希望好天气能给你带来美好的心情"};
    static String Snowy_Rainy[] = {"记得带把伞防御雨雪","要尽量减少在室外的时间","出行要格外小心","一定要做好防雨防寒工作","要注意保暖防寒"};
    static String Windy_Rainy[] = {"风雨很大，打伞要小心","尽量别在室外行走","出行尽量选择公共交通工具","出行一定要格外小心","风雨天打伞要小心"};

    //WeatherInfo weatherInfo = new WeatherInfo();

    public String getCareSMS(WeatherInfo weatherinfo /*String[] args*/) {
        //TEMP1 = Integer.parseInt(weatherinfo.)   //TEMP1 = Integer.parseInt(args[0]);
        String re = weatherinfo.curTem;
        //int a = 3;
        //int b = weatherinfo.curTem.length()-1;
        //String re = tem.substring(a, b);
        TEMP2 = Integer.parseInt(re);  //TEMP2 = Integer.parseInt(args[1]);
        //WIND = Integer.parseInt(weatherinfo.wind); //WIND = Integer.parseInt(args[2]);
        weather = new String(weatherinfo.weather); //weather = new String(args[3]);
        String liveWeather = weatherinfo.liveWeather;
        //NAME = new String(args[4]);
        CITY = new String(weatherinfo.cityName);//CITY = new String(args[5]);
        String s1 = null,s2 = "",s3 = "",s4 = "";
        int num;
        badWeather(weather);
        if(isSunny == false){
            if(isCloudy == true){
                num = getRand()%Cloudy.length;
                s1 = Cloudy[num];
            }
            if(isRainy == true){
                num = getRand()%Rainy.length;
                s1 = Rainy[num];
            }
            if(isSnowy == true){
                num = getRand()%Snowy.length;
                s1 = Snowy[num];
            }
            if(isFoggy == true){
                num = getRand()%Foggy.length;
                s1 = Foggy[num];
            }
            if(isRainy == true && isSnowy == true){
                num = getRand()%Snowy_Rainy.length;
                s1 = Snowy_Rainy[num];
            }
//            if(isRainy == true && isWindy(WIND) == true){
//                num = getRand()%Windy_Rainy.length;
//                s1 = Windy_Rainy[num];
//            }
        }
        else{
            num = getRand()%Sunny.length;
            s1 = Sunny[num];
        }

        if(isHot(TEMP2)){
            num = getRand()%hotA.length;
            s3 = hotA[num];
            num = getRand()%hotB.length;
            s4 = hotB[num];
        }
        if(isWindy(WIND)&&(!isRainy)){
            num = getRand()%Windy.length;
            s2 = Windy[num];
        }

        mess = "今天"+CITY+"的天气是："+weather+"，"+s1+"。" + "气温是"+TEMP2+"℃ "+s3+s4;
        System.out.print(mess);
        return mess;
    }

    static int getRand(){
        Random rnd= new Random();
        return rnd.nextInt(10);
    }

    static boolean isHot(int TEMP){
        if (TEMP >= 28)
            return true;
        else return false;
    }

    static boolean isWindy(int WIND){
        if (WIND >= 5)
            return true;
        else return false;
    }

    static void badWeather (String weather0){
        String reg1=".*雨.*";
        String reg2=".*雪.*";
        String reg3=".*雾.*";
        String reg4=".*云.*";
        String reg5=".*阴.*";
        if(weather.matches(reg1)) isRainy = true;
        if(weather.matches(reg2)) isSnowy = true;
        if(weather.matches(reg3)) isFoggy = true;
        if(weather.matches(reg4) || weather.matches(reg5)) isCloudy = true;
        if(isRainy == false && isSnowy == false && isFoggy == false && isCloudy == false ) isSunny = true;
    }
}
