package com.emile.testJedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

@Component
public class RedisInvalidator implements ApplicationRunner {


    @Autowired
    Jedis jedis;

    @Autowired
    RedisTemplate<String, RedisStored> redisTemplate;

    @Override
    public void run(ApplicationArguments args){

        ScanParams scanParams = new ScanParams().count(100).match("*");
        String cur = SCAN_POINTER_START;
        do {
            ScanResult<String> scanResult = jedis.scan(cur, scanParams);
            scanResult.getResult().stream().forEach(key -> {
                RedisStored stored = redisTemplate.opsForValue().get(key);
                if(stored != null && stored.getToken().getExpired()){
                    System.out.println("token is expired, invalidating");
                    redisTemplate.delete(key);
                }
            });
            cur = scanResult.getStringCursor();
        } while (!cur.equals(SCAN_POINTER_START));
    }
}
