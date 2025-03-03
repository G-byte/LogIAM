package com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * Kafka集群信息
 */
@Data
public class ReceiverDO extends BaseDO {

    /**
     * Kafka集群信息唯一标识
     */
    private Long id;
    /**
     * kafka 集群名
     */
    private String kafkaClusterName;
    /**
     * kafka 集群 broker 配置
     */
    private String kafkaClusterBrokerConfiguration;
    /**
     * kafka 集群对应生产端初始化配置
     */
    private String kafkaClusterProducerInitConfiguration;
    /**
     * kafka集群id
     */
    private Long kafkaClusterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKafkaClusterName() {
        return kafkaClusterName;
    }

    public void setKafkaClusterName(String kafkaClusterName) {
        this.kafkaClusterName = kafkaClusterName;
    }

    public String getKafkaClusterBrokerConfiguration() {
        return kafkaClusterBrokerConfiguration;
    }

    public void setKafkaClusterBrokerConfiguration(String kafkaClusterBrokerConfiguration) {
        this.kafkaClusterBrokerConfiguration = kafkaClusterBrokerConfiguration;
    }

    public String getKafkaClusterProducerInitConfiguration() {
        return kafkaClusterProducerInitConfiguration;
    }

    public void setKafkaClusterProducerInitConfiguration(String kafkaClusterProducerInitConfiguration) {
        this.kafkaClusterProducerInitConfiguration = kafkaClusterProducerInitConfiguration;
    }

    public Long getKafkaClusterId() {
        return kafkaClusterId;
    }

    public void setKafkaClusterId(Long kafkaClusterId) {
        this.kafkaClusterId = kafkaClusterId;
    }

}
