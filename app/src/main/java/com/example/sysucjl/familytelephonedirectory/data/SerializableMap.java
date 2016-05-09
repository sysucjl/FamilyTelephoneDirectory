package com.example.sysucjl.familytelephonedirectory.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/8.
 */
public class SerializableMap implements Serializable {

    private Map<String, Integer> map;

    public Map<String, Integer> getMap()
    {
        return map;
    }
    public void setMap(Map<String, Integer> map)
    {
        this.map=map;
    }
}
