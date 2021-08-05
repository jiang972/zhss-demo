package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

/**
 * 获取商品信息
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    @Override
    protected ProductInfo run(){
        String url = "http://localhost:8082/getProductInfo?productId="+productId;
        String response = HttpClientUtils.sendGetRequest(url);
        ProductInfo productInfo = JSONObject.parseObject(response, ProductInfo.class);
        return productInfo;
    }

    /**
     * GroupKey:组级别，一个服务接口集合的组，能统计失败次数，调用次数之类的
     * CommandKey：接口级别，每个接口提供一个key
     * ThreadPoolKey：处理CommandKey的线程池
     * ThreadPoolPropertiesDefaults : 设置线程池大小，一般用默认的10个足以,也可以设置缓冲队列的大小，队列默认的也是10
     * @param productId
     */
    public GetProductInfoCommand(Long productId) {
       // super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));

        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetCityNamePool"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetCityNameCommand"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(15)
                        .withQueueSizeRejectionThreshold(10)
                )
        );

        this.productId = productId;
    }
}
