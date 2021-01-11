package com.example.register.discover.loadbalance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.hash;

/**
 * 负载均衡,ZK选择节点服务
 */
public class LoadBalance
{

    //轮询的方式记录每个
    private static ConcurrentHashMap<String,Integer> routeCountEachJob = new ConcurrentHashMap<>();

    //LRU方式实现负载均衡
    private static ConcurrentHashMap<String,LinkedHashMap> lruMap = new ConcurrentHashMap<>();

    private static long CACHE_VALID_TIME = 0;

    /**
     * 随机的方式来获得服务节点
     * @param addressList
     * @return
     */
    public static String routeRandom(List<String> addressList,int size){
        String address = addressList.get(ThreadLocalRandom.current().nextInt(size));
        return address;
    }

    /**
     * 轮询方式
     */
    public static String routeRound(String serviceKey,List<String> addressList){
        String[] addressArr = addressList.toArray(new String[addressList.size()]);
        // 每次调用则该服务调用次数加1,通过该服下的节点数量进行hash计算选择的节点
        int i = count(serviceKey) % addressArr.length;
        System.out.println(i);
        String finalAddress = addressArr[i];
        return finalAddress;
    }

    /**
     * LRU 最近最少使用
     * 使用linkHashMap维护
     */

    public static  String routeLRU(String serviceKey,List<String> addressList){
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            lruMap.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000*60*60*24;//一天
        }
        // init lru
        LinkedHashMap<String, String> lruItem = lruMap.get(serviceKey);
        if (lruItem == null) {
            /**
             * LinkedHashMap
             * hashMap是无序的，linkedHashMap是有序的(通过table和双向列表实现)
             *      a、accessOrder：ture=访问顺序排序（get/put时排序）/ACCESS-LAST；false=插入顺序排期/FIFO；
             *      b、removeEldestEntry：新增元素时将会调用，返回true时会删除最老元素；可封装LinkedHashMap并重写该方法，比如定义最大容量，超出是返回true即可实现固定长度的LRU算法；
             *      map的key 是address value也是address
             */
            lruItem = new LinkedHashMap<String, String>(16, 0.75f, true){
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    if(super.size() > 3){
                        return true;
                    }else{
                        return false;
                    }
                }
            };
            lruMap.putIfAbsent(serviceKey, lruItem);
        }

        // put
        for (String address: addressList) {
            if (!lruItem.containsKey(address)) {
                lruItem.put(address, address);
            }
        }
        // load
        String eldestKey = lruItem.entrySet().iterator().next().getKey();//获取列表中首个key名
        String eldestValue = lruItem.get(eldestKey);//LRU算法关键体现在这里，实现了固定长度的LRU算法，每次去取的值的时候会重新对lruItem进行顺序变化
        return eldestValue;
    }

    /**
     * 一致性hash
     * 主要是避免当server中途宕机的时候，查找发生错乱
     */
    public static  String routeConsistentHash(String serviceKey,List<String> addressList){

        //hash节点个数
        Integer VIRTUAL_NODE_NUM = 5;
        //存放服务虚拟节点的hash值和对应的地址 addressRing即为hash环
        TreeMap<Long, String> addressRing = new TreeMap<Long, String>();
        for (String address: addressList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                long addressHash = hash("SHARD-" + address + "-NODE-" + i);
                addressRing.put(addressHash, address);
            }
        }
        long jobHash = hash(serviceKey);

        //将addressHash值大于jobHash值的adress都取出来
        SortedMap<Long, String> lastRing = addressRing.tailMap(jobHash);

//        System.out.println(lastRing);
        if (!lastRing.isEmpty()) {
            //如果这个地址不为空，就返回这个的第一个
            return lastRing.get(lastRing.firstKey());
        }
        System.out.println(lastRing.firstKey());
        //返回没有减少的地址的第一个
        return addressRing.firstEntry().getValue();
    }

    /**
     * 记录每个地址
     * @param serviceKey
     * @return
     */
    private static int count(String serviceKey) {
        // cache clear 每天清理一下
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountEachJob.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 24*60*60*1000;//一天的时间
        }
        Integer count = routeCountEachJob.get(serviceKey);
        count = (count==null || count>1000000)?(new Random().nextInt(100)):++count;  // 初始化时主动Random一次，缓解首次压力
        routeCountEachJob.put(serviceKey, count);
        System.out.println("count:"+count);
        return count;
    }


}
