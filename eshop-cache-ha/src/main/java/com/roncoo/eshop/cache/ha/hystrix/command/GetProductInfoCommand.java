package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

/**
 * 获取商品信息
 */
public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    private static HystrixCommandKey COMMANDKEY  = HystrixCommandKey.Factory.asKey("GetCityNameCommand");

    @Override
    protected ProductInfo run() throws Exception {
        //如果缓存有值就直接返回，不会调用这个run方法
        System.out.println("GetProductInfoCommand方法开始");
        if (productId == -1){
            throw new Exception();
        }
        String url = "http://localhost:8082/getProductInfo?productId="+productId;
        String response = HttpClientUtils.sendGetRequest(url);
        ProductInfo productInfo = JSONObject.parseObject(response, ProductInfo.class);
        return productInfo;
    }

    /**
     * GroupKey
     * CommandKey
     * ThreadPoolKey
     * ThreadPoolPropertiesDefaults
     * @param productId
     */
    public GetProductInfoCommand(Long productId) {
        //组级别，一个服务接口集合的组，能统计失败次数，调用次数之类的
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                //接口级别，每个接口提供一个key
                .andCommandKey(COMMANDKEY)
                //处理CommandKey的线程池
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetCityNamePool"))
                //设置线程池大小，一般用默认的10个足以,也可以设置缓冲队列的大小，队列默认的也是10
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(15)
                        .withQueueSizeRejectionThreshold(10)
                )
                //短路器配置
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        //短路器默认就是开启的
                        .withCircuitBreakerEnabled(true)
                        //短路器滑动窗口内最少的流量设置为30
                        .withCircuitBreakerRequestVolumeThreshold(30)
                        //短路器开启之后需要在多长时间内直接reject请求，之后就变成half-open
                        .withCircuitBreakerSleepWindowInMilliseconds(3000)
                        //异常请求量的百分比，当10s内异常请求达到这个百分比时，就触发打开短路器
                        .withCircuitBreakerErrorThresholdPercentage(40)
                        //强制打开/关闭短路器，默认关闭，一般无需配置
                       //.withCircuitBreakerForceOpen().withCircuitBreakerForceClosed()
                        //请求超时时间设置为5s
                        .withExecutionTimeoutInMilliseconds(5000)
                        //对于降级的请求并发数量控制，就是信号量控制的
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(15)
                )
        );

        this.productId = productId;
    }

    /**
     * 重写覆盖了这个方法就能开启缓存
     * @return
     */
//    @Override
//    protected String getCacheKey() {
//        return "product_info_"+productId;
//    }


    /**
     * 手动清理缓存方法，更新商品之后可以用这个清理掉缓存
     * @param productId
     */
    public void flushCache(Long productId){
        HystrixRequestCache.getInstance(COMMANDKEY,
                HystrixConcurrencyStrategyDefault.getInstance()).clear(String.valueOf(productId));
    }


    @Override
    protected ProductInfo getFallback() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setName("降级商品");
        return productInfo;
    }
}
