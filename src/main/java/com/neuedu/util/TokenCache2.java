package com.neuedu.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TokenCache2 {
    /*private static Logger logger = LoggerFactory.getLogger(TokenCache.class);*/
    public static final String TOKEN_PREFIX = "token_";
    //生成本地缓存，初始化为1000，最大为10000，当超过10000时就会使用LRU算法（最小使用算法）进行清除，有效期是12小时
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)  //缓存有效期12小时
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载.
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });
    public static void setKey(String key,String value){
        localCache.put(key,value);
    }
    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if("null".equals(value))
                return null;
            return value;
        }catch (Exception e){
           /* logger.error("localCache get error",e);*/
        }
        return null;
    }
}

