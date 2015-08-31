package com.alibaba.rocketmq.client.consumer.cacheable;

import com.alibaba.rocketmq.client.ClientStatus;
import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;

public class ProcessMessageTask implements Runnable {

    private static final Logger LOGGER = ClientLogger.getLog();

    private MessageExt message;

    private MessageHandler messageHandler;

    private CacheableConsumer cacheableConsumer;

    public ProcessMessageTask(MessageExt message, //Message to process.
                              MessageHandler messageHandler, //Message handler.
                              CacheableConsumer cacheableConsumer
    ) {
        this.message = message;
        this.messageHandler = messageHandler;
        this.cacheableConsumer = cacheableConsumer;
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();

            cacheableConsumer.getTxIdSet().add(message.getKeys());
            LOGGER.info("TX-ID Count: {}", cacheableConsumer.getTxIdSet().size());

            int result = messageHandler.handle(message);
            if (0 == result) {
                cacheableConsumer.getStatistics().addValue(System.currentTimeMillis() - start);
                cacheableConsumer.getSuccessCounter().incrementAndGet();
            } else if (result > 0) {
                cacheableConsumer.getFailureCounter().incrementAndGet();

                // Stash failure handling messages, which will be re-handled later after popping out from local store.
                cacheableConsumer.getLocalMessageStore().stash(message);
            } else {
                LOGGER.error("Unable to process returning result: " + result);
            }
        } catch (Exception e) {
            cacheableConsumer.getLocalMessageStore().stash(message);
            LOGGER.error("Yuck! Business processing logic is buggy; stash the message for now.");
            LOGGER.error("ProcessMessageTask failed! Automatic retry scheduled.", e);
        } finally {
            cacheableConsumer.getInProgressMessageQueue().remove(message);
        }

        try {
            if (cacheableConsumer.getStatus() == ClientStatus.SUSPENDED && cacheableConsumer.mayResume()) {
                cacheableConsumer.resume();
            }
        } catch (Throwable e) {
            LOGGER.error("Error to resume consumer client", e);
        }
    }

}
