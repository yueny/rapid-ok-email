package com.yueny.rapid.email.cluster;

import com.yueny.rapid.email.config.EmailInnerConfigureData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * AbstractLoadBalance
 */
@Slf4j
public abstract class AbstractLoadBalance implements LoadEmailBalance {
    /**
     * Calculate the weight according to the uptime proportion of warmup time
     * the new weight will be within 1(inclusive) to weight(inclusive)
     *
     * @param uptime the uptime in milliseconds
     * @param warmup the warmup time in milliseconds
     * @param weight the weight of an invoker
     * @return weight which takes warmup into account
     */
    static int calculateWarmupWeight(int uptime, int warmup, int weight) {
        int ww = (int) ((float) uptime / ((float) warmup / (float) weight));
        return ww < 1 ? 1 : (ww > weight ? weight : ww);
    }

    @Override
    public EmailInnerConfigureData select(List<EmailInnerConfigureData> invokers) {
        // 如果未配置, 则返回空
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }

        if (invokers.size() == 1) {
            return invokers.get(0);
        }

        EmailInnerConfigureData config = invokers.get(0);
        if(config == null){
            return null;
        }

        if(config.isDebug()){
            log.debug("选取预发送邮件的邮箱服务[0]:{}", config);
        }

        return config;

//        return doSelect(invokers, url, invocation);
    }

//    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation);
//
//
//    /**
//     * Get the weight of the invoker's invocation which takes warmup time into account
//     * if the uptime is within the warmup time, the weight will be reduce proportionally
//     *
//     * @param invoker    the invoker
//     * @param invocation the invocation of this invoker
//     * @return weight
//     */
//    protected int getWeight(Invoker<?> invoker, Invocation invocation) {
//        int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT);
//        if (weight > 0) {
//            long timestamp = invoker.getUrl().getParameter(Constants.REMOTE_TIMESTAMP_KEY, 0L);
//            if (timestamp > 0L) {
//                int uptime = (int) (System.currentTimeMillis() - timestamp);
//                int warmup = invoker.getUrl().getParameter(Constants.WARMUP_KEY, Constants.DEFAULT_WARMUP);
//                if (uptime > 0 && uptime < warmup) {
//                    weight = calculateWarmupWeight(uptime, warmup, weight);
//                }
//            }
//        }
//        return weight >= 0 ? weight : 0;
//    }

}
