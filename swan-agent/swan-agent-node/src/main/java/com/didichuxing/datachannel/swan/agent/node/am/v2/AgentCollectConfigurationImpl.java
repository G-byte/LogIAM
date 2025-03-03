package com.didichuxing.datachannel.swan.agent.node.am.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web.FileNameSuffixMatchRuleDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web.LogContentFilterRuleDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web.LogSliceRuleDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.*;
import com.didichuxing.datachannel.agentmanager.common.enumeration.host.HostTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskLimitPriorityLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskStatusEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskTypeEnum;
import com.didichuxing.datachannel.swan.agent.common.api.*;
import com.didichuxing.datachannel.swan.agent.common.beans.LogPath;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.*;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.*;
import com.didichuxing.datachannel.swan.agent.common.constants.Tags;
import com.didichuxing.datachannel.swan.agent.sink.kafkaSink.KafkaTargetConfig;
import com.didichuxing.datachannel.swan.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.swan.agent.source.log.config.MatchConfig;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author pengzhanglong
 * @date 2020/12/27 9:59
 * @desc
 */
public class AgentCollectConfigurationImpl extends AgentCollectConfiguration {

    /**
     * 日志模型限流等级 - 高
     */
    private static final Integer MODEL_LIMIT_LEVEL_HIGH   = 9;
    /**
     * 日志模型限流等级 - 低
     */
    private static final Integer MODEL_LIMIT_LEVEL_LOW    = 1;
    /**
     * 日志模型限流等级 - 中
     */
    private static final Integer MODEL_LIMIT_LEVEL_MIDDLE = 5;

    private static final Logger  LOGGER                   = LoggerFactory
                                                              .getLogger(AgentCollectConfigurationImpl.class);

    /**
     * 将自身转化为AgentConfig对象并返回
     *
     * @return 返回自身转化为的AgentConfig对象
     */
    public AgentConfig convertToAgentConfig() {
        AgentConfiguration agentConfiguration = getAgentConfiguration();
        String advancedConfigurationJsonString = agentConfiguration.getAdvancedConfigurationJsonString();
        AgentAdvancedConfiguration agentAdvancedConfiguration = null;
        if (StringUtils.isNotBlank(advancedConfigurationJsonString)) {
            agentAdvancedConfiguration = JSON.parseObject(advancedConfigurationJsonString, AgentAdvancedConfiguration.class);
        }
        if (null == agentAdvancedConfiguration) {//未配置 或 解析出错
            agentAdvancedConfiguration = new AgentAdvancedConfiguration();//采用默认配置
        }
        AgentConfig agentConfig = new AgentConfig();
        /*
         * set agentConfig 对应一级属性值
         */
        agentConfig.setHostname(agentConfiguration.getHostName());
        agentConfig.setVersion(agentConfiguration.getAgentConfigurationVersion());
        /*
         * set offset config, use default offset config
         */
        agentConfig.setOffsetConfig(new OffsetConfig());
        /*
         * set error log config
         */
        ErrorLogConfig errorLogConfig = new ErrorLogConfig();//error 配置
        ProducerConfiguration errorLogsProducerConfiguration = agentConfiguration.getErrorLogsProducerConfiguration();
        errorLogConfig.setNameServer(errorLogsProducerConfiguration.getNameServer());
        errorLogConfig.setProperties(errorLogsProducerConfiguration.getProperties());
        errorLogConfig.setTopic(errorLogsProducerConfiguration.getTopic());
        errorLogConfig.setSwitchConfig(ServiceSwitch.ON.getStatus());
        agentConfig.setErrorLogConfig(errorLogConfig);
        /*
         * set limit config
         */
        LimitConfig limitConfig = new LimitConfig();
        limitConfig.setCpuThreshold(agentConfiguration.getCpuLimitThreshold());
        limitConfig.setStartThreshold(agentAdvancedConfiguration.getAgentLimitStartThreshold());
        limitConfig.setMinThreshold(agentAdvancedConfiguration.getAgentLimitMinThreshold());
        agentConfig.setLimitConfig(limitConfig);
        /*
         * set metrics config
         */
        MetricConfig metricConfig = new MetricConfig();
        ProducerConfiguration metricsProducerConfiguration = agentConfiguration.getMetricsProducerConfiguration();
        metricConfig.setNameServer(metricsProducerConfiguration.getNameServer());
        metricConfig.setProperties(metricsProducerConfiguration.getProperties());
        metricConfig.setTopic(metricsProducerConfiguration.getTopic());
        metricConfig.setSwitchConfig(ServiceSwitch.ON.getStatus());
        metricConfig.setTransfer(true);//TODO：
        agentConfig.setMetricConfig(metricConfig);
        /*
         * set model config
         */
        Map<HostInfo, List<LogCollectTaskConfiguration>> hostName2LogCollectTaskConfigurationMap = getHostName2LogCollectTaskConfigurationMap();
        List<ModelConfig> modelConfigs = new ArrayList<>();
        for (Map.Entry<HostInfo, List<LogCollectTaskConfiguration>> entry : hostName2LogCollectTaskConfigurationMap.entrySet()) {
            HostInfo hostInfo = entry.getKey();//待采集主机信息
            List<LogCollectTaskConfiguration> logCollectTaskConfigurations = entry.getValue();//待采集主机对应的日志采集任务信息集
            for (LogCollectTaskConfiguration logCollectTaskConfiguration : logCollectTaskConfigurations) {
                ModelConfig modelConfig = buildModelConfig(hostInfo, logCollectTaskConfiguration);
                modelConfigs.add(modelConfig);
            }
        }
        agentConfig.setModelConfigs(modelConfigs);
        return agentConfig;
    }

    /**
     * 根据给定主机 & 日志采集任务构建对应日志模型配置对象
     *
     * @param hostInfo                    主机对象
     * @param logCollectTaskConfiguration 日志采集任务对象
     * @return 根据给定主机 & 日志采集任务构建的对应日志模型配置对象
     */
    private ModelConfig buildModelConfig(HostInfo hostInfo, LogCollectTaskConfiguration logCollectTaskConfiguration) {

        /*
         * 构建 日志采集任务高级配置信息
         */
        LogCollectTaskAdvancedConfiguration logCollectTaskAdvancedConfiguration = null;//日志采集任务高级配置信息
        String advancedConfigurationJsonString = logCollectTaskConfiguration.getAdvancedConfigurationJsonString();
        if (StringUtils.isNotBlank(advancedConfigurationJsonString)) {
            logCollectTaskAdvancedConfiguration = JSON.parseObject(advancedConfigurationJsonString, LogCollectTaskAdvancedConfiguration.class);
        }
        if (null == logCollectTaskAdvancedConfiguration) {//未配置 或 解析出错
            logCollectTaskAdvancedConfiguration = new LogCollectTaskAdvancedConfiguration();//采用默认配置
        }
        /*
         * 构建 modelConfig
         */
        ModelConfig modelConfig = new ModelConfig(Tags.TASK_LOG2KAFKA);
        modelConfig.setCollectType(CollectType.COLLECT_IN_NORMAL_SERVER.getStatus());
        modelConfig.setHostname(hostInfo.getHostName());
        modelConfig.setVersion(logCollectTaskConfiguration.getConfigurationVersion());
        /*
         * set channel config
         */
        ChannelConfig channelConfig = new ChannelConfig();
        channelConfig.setMaxBytes(logCollectTaskAdvancedConfiguration.getChannelMaxBytes());
        channelConfig.setMaxNum(logCollectTaskAdvancedConfiguration.getChannelMaxNum());
        modelConfig.setChannelConfig(channelConfig);
        /*
         * set common config
         */
        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setEncodeType(logCollectTaskAdvancedConfiguration.getEncodeType());
        commonConfig.setModelId(logCollectTaskConfiguration.getLogCollectTaskId());
        commonConfig.setModelType(
                logCollectTaskConfiguration.getLogCollectTaskType().equals(LogCollectTaskTypeEnum.NORMAL_COLLECT.getCode()) ? LogConfigConstants.COLLECT_TYPE_PERIODICITY : LogConfigConstants.COLLECT_TYPE_TEMPORALITY
        );
        if (commonConfig.getModelType().equals(LogConfigConstants.COLLECT_TYPE_TEMPORALITY)) {
            commonConfig.setEndTime(new Date(logCollectTaskConfiguration.getCollectEndTimeBusiness()));
            commonConfig.setStartTime(new Date(logCollectTaskConfiguration.getCollectStartTimeBusiness()));
        }
        commonConfig.setStop(
                !logCollectTaskConfiguration.getLogCollectTaskStatus().equals(LogCollectTaskStatusEnum.RUNNING.getCode())
        );
        commonConfig.setVersion(logCollectTaskConfiguration.getConfigurationVersion());
        modelConfig.setCommonConfig(commonConfig);
        /*
         * set event metrics config
         */
        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOtherMetrics(logCollectTaskAdvancedConfiguration.getOtherMetrics());  // map转化
        eventMetricsConfig.setOtherEvents(logCollectTaskAdvancedConfiguration.getOtherEvents());   // map转化
        modelConfig.setEventMetricsConfig(eventMetricsConfig);
        /*
         * set limit config
         */
        ModelLimitConfig modelLimitConfig = new ModelLimitConfig();
        Integer limitPriority = logCollectTaskConfiguration.getLimitPriority();
        if (limitPriority.equals(LogCollectTaskLimitPriorityLevelEnum.HIGH.getCode())) {
            modelLimitConfig.setLevel(MODEL_LIMIT_LEVEL_HIGH);
        } else if (limitPriority.equals(LogCollectTaskLimitPriorityLevelEnum.MIDDLE.getCode())) {
            modelLimitConfig.setLevel(MODEL_LIMIT_LEVEL_MIDDLE);
        } else {
            modelLimitConfig.setLevel(MODEL_LIMIT_LEVEL_LOW);
        }
        modelLimitConfig.setStartThrehold(logCollectTaskAdvancedConfiguration.getStartThrehold());
        modelLimitConfig.setMinThreshold(logCollectTaskAdvancedConfiguration.getMinThreshold());
        modelConfig.setModelLimitConfig(modelLimitConfig);
        /*
         * set source config
         */
        LogSourceConfig logSourceConfig = new LogSourceConfig();
        /*
         * set match config
         */
        MatchConfig matchConfig = new MatchConfig();
        matchConfig.setFileType(FileType.File.getStatus());
        matchConfig.setBusinessType(StandardLogType.Normal.getType());
        matchConfig.setMatchType(FileMatchType.Regex.getStatus());
        String fileNameSuffixMatchRuleLogicJsonString = logCollectTaskConfiguration.getFileNameSuffixMatchRuleLogicJsonString();
        if (StringUtils.isNotBlank(fileNameSuffixMatchRuleLogicJsonString)) {
            FileNameSuffixMatchRuleDTO fileFilter = JSONObject.parseObject(fileNameSuffixMatchRuleLogicJsonString, FileNameSuffixMatchRuleDTO.class);  //文件过滤
            if (fileFilter != null) {
                matchConfig.setFileSuffix(fileFilter.getSuffixMatchRegular());
            }
        }
        logSourceConfig.setMatchConfig(matchConfig);
        /*
         * set log file content split rule logic
         */
        String logContentSliceRuleLogicJsonString = logCollectTaskConfiguration.getLogContentSliceRuleLogicJsonString();
        if (StringUtils.isNotBlank(logContentSliceRuleLogicJsonString)) {
            LogSliceRuleDTO logSliceRuleDTO = JSONObject.parseObject(logContentSliceRuleLogicJsonString, LogSliceRuleDTO.class);
            if (logSliceRuleDTO != null) {
                //日志切分规则
                String timeFormat = logSliceRuleDTO.getSliceTimestampFormat();
                if (timeFormat != null && !timeFormat.equals("")) {
                    logSourceConfig.setTimeFormat(timeFormat);  //时间戳格式
                }
                String timeStartFlag = logSliceRuleDTO.getSliceTimestampPrefixString();
                if (timeStartFlag != null && !timeStartFlag.equals("")) {
                    logSourceConfig.setTimeStartFlag(timeStartFlag);  //切片时间戳前缀字符串
                }
                Integer timeStartFlagIndex = logSliceRuleDTO.getSliceTimestampPrefixStringIndex();
                if (timeStartFlagIndex != null) {
                    logSourceConfig.setTimeStartFlagIndex(timeStartFlagIndex); //切片时间戳前缀字符串左起第几个，index计数从1开始
                }
            }
        }
        //set log paths
        if (CollectionUtils.isNotEmpty(logCollectTaskConfiguration.getFileLogCollectPathList())) {//文件型采集
            List<LogPath> logPaths = new ArrayList<>(logCollectTaskConfiguration.getFileLogCollectPathList().size());
            for (FileLogCollectPathConfiguration fileLogCollectPathConfiguration : logCollectTaskConfiguration.getFileLogCollectPathList()) {
                LogPath logPath = new LogPath();
                logPath.setLogModelId(logCollectTaskConfiguration.getLogCollectTaskId());
                logPath.setPath(fileLogCollectPathConfiguration.getPath());
                logPath.setPathId(fileLogCollectPathConfiguration.getPathId());
                if (hostInfo.getHostType().equals(HostTypeEnum.CONTAINER.getCode())) {//容器采集case须设置其 dockerPath
                    String containerPath = fileLogCollectPathConfiguration.getRealPath();//容器路径
                    logPath.setDockerPath(containerPath);
                    logPath.setRealPath(containerPath);
                } else {//非容器路径
                    logPath.setPath(fileLogCollectPathConfiguration.getPath());
                }
                logPaths.add(logPath);
            }
            logSourceConfig.setLogPaths(logPaths);
            FileLogCollectPathConfiguration fileLogCollectPathConfiguration = logCollectTaskConfiguration.getFileLogCollectPathList().get(0);

        }
        logSourceConfig.setMaxErrorLineNum(logCollectTaskAdvancedConfiguration.getMaxErrorLineNum());
        logSourceConfig.setMaxModifyTime(logCollectTaskAdvancedConfiguration.getMaxModifyTime());
        logSourceConfig.setMaxThreadNum(logCollectTaskAdvancedConfiguration.getMaxThreadNum());
        logSourceConfig.setOrderTimeMaxGap(logCollectTaskAdvancedConfiguration.getOrderTimeMaxGap());
        logSourceConfig.setReadFileType(logCollectTaskAdvancedConfiguration.getReadFileType());
        logSourceConfig.setReadTimeOut(logCollectTaskAdvancedConfiguration.getReadTimeOut());
        modelConfig.setSourceConfig(logSourceConfig);
        /*
         * set target config
         */
        KafkaTargetConfig targetConfig = new KafkaTargetConfig();
        targetConfig.setAsync(logCollectTaskAdvancedConfiguration.getAsync());
        ProducerConfiguration kafkaClusterConfiguration = logCollectTaskConfiguration.getLogProducerConfiguration();
        targetConfig.setBootstrap(kafkaClusterConfiguration.getNameServer());
        targetConfig.setClusterId(kafkaClusterConfiguration.getReceiverId().intValue());
        String contentFilterJsonString = logCollectTaskConfiguration.getLogContentFilterRuleLogicJsonString();
        if (StringUtils.isNotBlank(contentFilterJsonString)) {
            LogContentFilterRuleDTO contentFilter = JSONObject.parseObject(contentFilterJsonString, LogContentFilterRuleDTO.class);
            if (contentFilter != null) {
                Integer filterOprType = contentFilter.getLogContentFilterType();
                if (filterOprType != null) {
                    targetConfig.setFilterOprType(filterOprType);
                }
                targetConfig.setFilterRule(contentFilter.getLogContentFilterExpression());
            }
        }
        targetConfig.setFlushBatchSize(logCollectTaskAdvancedConfiguration.getFlushBatchSize());
        targetConfig.setFlushBatchTimeThreshold(logCollectTaskAdvancedConfiguration.getFlushBatchTimeThreshold());
        targetConfig.setKeyFormat(logCollectTaskAdvancedConfiguration.getKeyFormat());
        targetConfig.setKeyStartFlag(logCollectTaskAdvancedConfiguration.getKeyStartFlag());
        targetConfig.setKeyStartFlagIndex(logCollectTaskAdvancedConfiguration.getKeyStartFlagIndex());
        targetConfig.setMaxContentSize(logCollectTaskAdvancedConfiguration.getMaxContentSize());
        targetConfig.setProperties(logCollectTaskConfiguration.getLogProducerConfiguration().getProperties());
        targetConfig.setRegularPartKey(logCollectTaskAdvancedConfiguration.getRegularPartKey());
        targetConfig.setSendBatchSize(logCollectTaskAdvancedConfiguration.getSendBatchSize());
        targetConfig.setSendBatchTimeThreshold(logCollectTaskAdvancedConfiguration.getSendBatchTimeThreshold());
        targetConfig.setTopic(logCollectTaskConfiguration.getLogProducerConfiguration().getTopic());
        targetConfig.setTransFormate(logCollectTaskAdvancedConfiguration.getTransFormate());
        targetConfig.setSinkNum(logCollectTaskAdvancedConfiguration.getSinkNum());
        modelConfig.setTargetConfig(targetConfig);

        return modelConfig;
    }
}
