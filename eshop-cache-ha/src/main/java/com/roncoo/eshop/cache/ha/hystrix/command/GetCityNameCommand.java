package com.roncoo.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.roncoo.eshop.cache.ha.cache.local.LocationCache;

public class GetCityNameCommand extends HystrixCommand<String> {

    private Long cityId;

    public GetCityNameCommand(Long cityId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetCityNameGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        //指定使用信号量隔离
                        .withExecutionIsolationStrategy(
                                HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE
                        )
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(15)
                )
        );
        this.cityId = cityId;
    }

    //信号量隔离，最好是这种不需要网络请求的，也就不会涉及超时
    @Override
    protected String run() throws Exception {
        return LocationCache.getCityName(cityId);
    }
}
