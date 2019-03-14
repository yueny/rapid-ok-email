package com.yueny.rapid.email.cluster;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * random load balance.
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "random";

//    @Override
//    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
//        // Number of invokers
//        int length = invokers.s          `    ize();
//        // Every invoker has the same weight?
//        boolean sameWeight = true;
//        // the weight of every invokers
//        int[] weights = new int[length];
//        // the first invoker's weight
//        int firstWeight = getWeight(invokers.get(0), invocation);
//        weights[0] = firstWeight;
//        // The sum of weights
//        int totalWeight = firstWeight;
//        for (int i = 1; i < length; i++) {
//            int weight = getWeight(invokers.get(i), invocation);
//            // save for later use
//            weights[i] = weight;
//            // Sum
//            totalWeight += weight;
//            if (sameWeight && weight != firstWeight) {
//                sameWeight = false;
//            }
//        }
//        if (totalWeight > 0 && !sameWeight) {
//            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on totalWeight.
//            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
//            // Return a invoker based on the random value.
//            for (int i = 0; i < length; i++) {
//                offset -= weights[i];
//                if (offset < 0) {
//                    return invokers.get(i);
//                }
//            }
//        }
//        // If all invokers have the same weight value or totalWeight=0, return evenly.
//        return invokers.get(ThreadLocalRandom.current().nextInt(length));
//    }

}
