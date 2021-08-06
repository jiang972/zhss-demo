package com.roncoo.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.*;
import com.roncoo.eshop.cache.ha.cache.local.BrandCache;

public class GetBrandNameCommand extends HystrixCommand<String> {

    @SuppressWarnings("unused")
    private Long brandId;

    public GetBrandNameCommand(Long brandId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("BrandInfoService"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetBrandInfoPool"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetBrandInfoCommand"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(15)
                        .withQueueSizeRejectionThreshold(10)
                )
                //对于降级的请求并发数量控制，默认就是信号量
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withFallbackIsolationSemaphoreMaxConcurrentRequests(15))
        );
        this.brandId = brandId;
    }

    @Override
    protected String run() throws Exception {
        throw new Exception();
    }

    @Override
    protected String getFallback() {
        System.out.println("Brand降级");
        return BrandCache.getBrandName(brandId);
    }
}
