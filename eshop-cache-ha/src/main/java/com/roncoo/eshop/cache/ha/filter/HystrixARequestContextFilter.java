package com.roncoo.eshop.cache.ha.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import javax.servlet.*;
import java.io.IOException;

/**
 * 请求上下文过滤器
 */
public class HystrixARequestContextFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try{
            filterChain.doFilter(servletRequest,servletResponse);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            context.shutdown();
        }

    }

    @Override
    public void destroy() {

    }
}
