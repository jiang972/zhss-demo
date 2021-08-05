package com.roncoo.eshop.cache.ha.cache.local;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地缓存类
 */
public class LocationCache {

    public static Map<Long,String> cityMap = new HashMap<>();

    static {
        cityMap.put(1L,"beijing");
    }

    public static String getCityName(Long cityId){
        return cityMap.get(cityId);
    }

}
