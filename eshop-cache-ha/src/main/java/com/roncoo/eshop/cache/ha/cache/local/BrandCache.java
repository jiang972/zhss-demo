package com.roncoo.eshop.cache.ha.cache.local;

import java.util.HashMap;
import java.util.Map;

/**
 * 品牌缓存类
 */
public class BrandCache {

    public static Map<Long,String> brandMap = new HashMap<>();

    static {
        brandMap.put(1L,"iphone");
    }

    public static String getBrandName(Long brandId){
        return brandMap.get(brandId);
    }
}
