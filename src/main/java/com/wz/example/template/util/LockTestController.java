package com.wz.example.template.util;

import com.wz.example.template.model.resposne.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/lock/test")
public class LockTestController {

    private Logger logger = LoggerFactory.getLogger(LockTestController.class);

    @Autowired
    private RedisDistributedLock redisDistributedLock;

    private static final String lockKey = "TEST1";

    @PostMapping("/run1")
    public RestResponse run1(@RequestBody Map<String, Object> paramMap) {

        logger.debug(String.format("[%s] Incoming post request", Thread.currentThread().getName()));
        if (redisDistributedLock.lock2(lockKey, 1 * 1000)) {
            logger.debug(String.format("[%s] Transaction is executing !!!", Thread.currentThread().getName()));
            try {
                Thread.sleep(5 * 1000);
                return new RestResponse();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                redisDistributedLock.releaseLock2(lockKey);
                logger.debug(String.format("[%s] Transaction has ended !!!", Thread.currentThread().getName()));
            }
        }

        return RestResponse.error("500", "服务正忙，别再来了！");
    }
}
