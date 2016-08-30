package com.alibaba.rocketmq.example.smoking;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendCallback;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Producer {

    private static Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    public static void main(String[] args) {
        DefaultMQProducer producer = new DefaultMQProducer("Smoking");
        producer.setNamesrvAddr("172.30.30.233:9876");
        try {
            producer.start();
            byte[] body = new byte[1024];
            Arrays.fill(body, (byte)'x');
            Message message = new Message("TestTopic", body);
            while (true) {
                try {
                    producer.send(message, new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            LOGGER.debug(sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable e) {
                            LOGGER.error("Send failed", e);
                        }
                    });
                } catch (RemotingException e) {
                    LOGGER.error("Send failed", e);
                } catch (InterruptedException e) {
                    LOGGER.warn("Thread interrupted", e);
                    break;
                }
            }
        } catch (MQClientException e) {
            LOGGER.error("Start producer failed", e);
        } finally {
            producer.shutdown();
        }
    }

}