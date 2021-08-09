package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;
import jdk.management.resource.ResourceType;

import java.util.Collection;
import java.util.List;

public class GetProductInfosCollapser extends HystrixCollapser<List<ProductInfo>, ProductInfo,Long> {

    private Long productId;

    public GetProductInfosCollapser(Long productId) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("GetProductInfosCollapser"))
                .andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter()
                        //设置一次batch最多多少请求就会立刻发送请求，默认无限大，全看时间窗
                        .withMaxRequestsInBatch(100)
                        //设置时间窗，默认是10ms，把10ms内的请求合并起来
                        .withTimerDelayInMilliseconds(20)
                )
        );
        this.productId = productId;
    }

    @Override
    public Long getRequestArgument() {
        return productId;
    }

    @Override
    protected HystrixCommand<List<ProductInfo>> createCommand(Collection<CollapsedRequest<ProductInfo, Long>> requests) {

        StringBuilder paramsBuilder = new StringBuilder();
        for (CollapsedRequest<ProductInfo,Long> request : requests){
            paramsBuilder.append(request.getArgument()).append(",");
        }
        String params = paramsBuilder.toString();
        params = params.substring(0,params.length()-1);

        System.out.println("createCommand方法执行："+params);
        //上面的只是为了打印params而已
        return new BatchCommand(requests);
    }

    @Override
    protected void mapResponseToRequests(List<ProductInfo> batchResponse,
                                         Collection<CollapsedRequest<ProductInfo, Long>> requests) {
        //请求合并方法
        int count = 0;
        for (CollapsedRequest<ProductInfo, Long> request : requests){
            System.out.println("请求合并:"+batchResponse.get(count).toString());
            request.setResponse(batchResponse.get(count++));
        }
    }

    @Override
    protected String getCacheKey() {
        return"productInfo_"+productId;
    }

    private static class BatchCommand extends HystrixCommand<List<ProductInfo>>{

        public final Collection<CollapsedRequest<ProductInfo,Long>> requests;

        public BatchCommand(Collection<CollapsedRequest<ProductInfo,Long>> requests){
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfosCollapserBatchCommand"))
            );
            this.requests = requests;
        }


        @Override
        protected List<ProductInfo> run() throws Exception {
            //一个批次内的商品id拼接在一起
            StringBuilder paramsBuilder = new StringBuilder();
            for (CollapsedRequest<ProductInfo,Long> request : requests){
                paramsBuilder.append(request.getArgument()).append(",");
            }
            String params = paramsBuilder.toString();
            params = params.substring(0,params.length()-1);

            //一个请求
            String url = "http://localhost:8082/getProductInfos?productIds="+params;
            String response = HttpClientUtils.sendGetRequest(url);
            List<ProductInfo> productInfos = JSONArray.parseArray(response, ProductInfo.class);

            for (ProductInfo productInfo : productInfos){
                System.out.println("productInfo:"+productInfo.toString());
            }

            return productInfos;
        }
    }
}
