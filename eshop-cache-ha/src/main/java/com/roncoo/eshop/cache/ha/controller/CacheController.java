package com.roncoo.eshop.cache.ha.controller;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixObservableCommand;
import com.roncoo.eshop.cache.ha.hystrix.command.GetCityNameCommand;
import com.roncoo.eshop.cache.ha.hystrix.command.GetProductInfoCommand;
import com.roncoo.eshop.cache.ha.hystrix.command.GetProductInfosCommand;
import com.roncoo.eshop.cache.ha.model.ProductInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import rx.Observable;
import rx.Observer;

/**
 * 缓存服务的接口
 * @author Administrator
 *
 */
@Controller
public class CacheController {

	@RequestMapping("/change/product")
	@ResponseBody
	public String changeProduct(Long productId) {
		// 拿到一个商品id
		// 调用商品服务的接口，获取商品id对应的商品的最新数据
		// 用HttpClient去调用商品服务的http接口
		String url = "http://127.0.0.1:8082/getProductInfo?productId=" + productId;
		String response = HttpClientUtils.sendGetRequest(url);
		System.out.println(response);  
		
		return "success";
	}


	/**
	 * nginx开始缓存全部失效，nginx发送很多请求直接到缓存服务要拉取最新数据
	 * @param productId
	 * @return
	 */
	@RequestMapping("/getProductInfo")
	@ResponseBody
	public String getProductInfo(Long productId) {
		HystrixCommand<ProductInfo> getProductInfoCommand = new GetProductInfoCommand(productId);
		ProductInfo productInfo = getProductInfoCommand.execute();

		//从缓存中拿到城市id对应的名字
		Long cityId = productInfo.getCityId();

		//将查询方法，使用信号量隔离
		GetCityNameCommand getCityNameCommand = new GetCityNameCommand(cityId);
		String cityName = getCityNameCommand.execute();
		//String cityName = LocationCache.getCityName(cityId);

		productInfo.setCityName(cityName);

		System.out.println(productInfo);
		return "success";
	}


	@RequestMapping("/getProductInfos")
	@ResponseBody
	public String getProductInfos(String productIds) {
		HystrixObservableCommand<ProductInfo> getProductInfosCommand = new GetProductInfosCommand(productIds.split(","));
		Observable<ProductInfo> observable = getProductInfosCommand.observe();
		observable.subscribe(new Observer<ProductInfo>() {
			@Override
			public void onCompleted() {
				System.out.println("获取到全部数据了");
			}

			@Override
			public void onError(Throwable throwable) {
				throwable.printStackTrace();
			}

			@Override
			public void onNext(ProductInfo productInfo) {
				System.out.println(productInfo);
			}
		});

		return "success";
	}
}
