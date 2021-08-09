package com.roncoo.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class FailureModeCommand extends HystrixCommand<Boolean> {

    private Boolean flag;

    public FailureModeCommand(Boolean flag){
        super(HystrixCommandGroupKey.Factory.asKey("FailureModeGroup"));
        this.flag = flag;
    }

    @Override
    protected Boolean run() throws Exception {
        if (flag){
            throw new Exception();
        }
        return true;
    }
}
