package com.roncoo.eshop.cache.ha.controller;

import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.hystrix.command.FailureModeCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试用的类
 */
@Controller
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(String name){
        return "hello"+name;
    }


    public static void main(String[] args) throws Exception{
//        for (int i = 0; i < 15; i++){
//            String url = "http://127.0.0.1:8081/getProductInfo?productId=1";
//            String response = HttpClientUtils.sendGetRequest(url);
//            System.out.println("第"+i+"次请求,结果为"+response);
//        }
//        for (int i = 0; i < 25; i++){
//            String url = "http://127.0.0.1:8081/getProductInfo?productId=-1";
//            String response = HttpClientUtils.sendGetRequest(url);
//            System.out.println("第"+i+"次请求,结果为"+response);
//        }
//
//        System.out.println("尝试等待5秒");
//        Thread.sleep(5000);
//
//        for (int i = 0; i < 10; i++){
//            String url = "http://127.0.0.1:8081/getProductInfo?productId=-1";
//            String response = HttpClientUtils.sendGetRequest(url);
//            System.out.println("第"+i+"次请求,结果为"+response);
//        }
//
//        System.out.println("尝试等待5秒");
//        Thread.sleep(5000);
//
//        for (int i = 0; i < 10; i++){
//            String url = "http://127.0.0.1:8082/getProductInfo?productId=1";
//            String response = HttpClientUtils.sendGetRequest(url);
//            System.out.println("第"+i+"次请求,结果为"+response);
//        }

        //HttpClientUtils.sendGetRequest("http://127.0.0.1:8081/getProductInfos?productIds=1,1,2,3,4");


        FailureModeCommand failureModeCommand = new FailureModeCommand(true);
        failureModeCommand.execute();
    }
}
