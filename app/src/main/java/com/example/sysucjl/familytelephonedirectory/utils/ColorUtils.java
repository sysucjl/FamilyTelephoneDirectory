package com.example.sysucjl.familytelephonedirectory.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/6.
 */
public class ColorUtils implements Serializable{

    public static Map<Integer, String> colorMap;

    static {
        colorMap = new HashMap<Integer, String>();
        colorMap.put(0, "#c51915");
        colorMap.put(1, "#cb4318");
        colorMap.put(2, "#ed6c20");
        colorMap.put(3, "#fe8b26");
        colorMap.put(4, "#9bcb28");
        colorMap.put(5, "#c9b754");
        colorMap.put(6, "#c89d38");
        colorMap.put(7, "#cb7620");
        colorMap.put(8, "#88b143");
        colorMap.put(9, "#53a627");
        colorMap.put(10, "#67981b");
        colorMap.put(11, "#689f38");
        colorMap.put(12, "#20ab66");
        colorMap.put(13, "#66b494");
        colorMap.put(14, "#1898a7");
        colorMap.put(15, "#70b4b9");
        colorMap.put(16, "#3cb7e4");
        colorMap.put(17, "#199bcb");
        colorMap.put(18, "#039be5");
        colorMap.put(19, "#178acf");
        colorMap.put(20, "#8266ca");
        colorMap.put(21, "#754cb3");
        colorMap.put(22, "#6568ca");
        colorMap.put(23, "#4285f4");
        colorMap.put(24, "#aa69cb");
        colorMap.put(25, "#993bcb");
        colorMap.put(26, "#9e44b7");
        colorMap.put(27, "#fd464b");
        colorMap.put(28, "#a39598");
        colorMap.put(29, "#a47c82");
        colorMap.put(30, "#c45e7e");
        colorMap.put(31, "#e91e63");
        colorMap.put(32, "#757575");
        colorMap.put(33, "#333333");
    }

    public static String getColor(int hashCode){
        String color = colorMap.get(Math.abs(hashCode % 34));
        if(color != null)
            return color;
        else {
            System.out.println("wrong "+ hashCode%34);
            return "#0288d1";
        }
    }
}
