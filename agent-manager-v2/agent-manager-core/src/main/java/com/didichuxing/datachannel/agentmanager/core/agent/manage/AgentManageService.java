package com.didichuxing.datachannel.agentmanager.core.agent.manage;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanelGroup;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;

import java.util.List;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent管理服务接口
 */
public interface AgentManageService {

    /**
     * 创建一个 AgentPO 对象
     * @param agent  待创建 AgentPO 对象
     * @param operator 操作人
     * @return 创建成功的agent对象id
     */
    Long createAgent(AgentDO agent, String operator);

    /**
     * 删除主机名为hostName的Agent对象
     * @param hostName 主机名
     * @param checkAgentCompleteCollect 是否检查待删除Agent是否已采集完其需要采集的所有日志
     *        true：将会校验待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，如未采集完，将导致删除该Agent对象失败，直到采集完其所有的待采集文件
     *        false：将会忽略待删除Agent所采集的所有日志采集任务是否都已采集完其所有的待采集文件，直接删除Agent对象（注意：将导致日志采集不完整情况，请谨慎使用）
     * @param uninstall 是否卸载Agent true：卸载 false：不卸载
     * @param operator 操作人
     */
    void deleteAgentByHostName(String hostName, boolean checkAgentCompleteCollect, boolean uninstall, String operator);

    /**
     * 根据id删除对应agent对象
     * @param id 待删除agent对象 id
     * @param checkAgentCompleteCollect 删除agent时，是否检测该agent是否存在未被采集完的日志，如该参数值设置为true，当待删除agent存在未被采集完的日志时，将会抛出异常，不会删除该agent
     * @param uninstall 是否卸载 agent，该参数设置为true，将添加一个该agent的卸载任务
     * @param operator 操作人
     */
    void deleteAgentById(Long id, boolean checkAgentCompleteCollect, boolean uninstall, String operator);

    /**
     * 根据id集批量删除对应agent对象集
     * @param ids 待删除agent对象 id 集
     * @param checkAgentCompleteCollect 删除agent时，是否检测该agent是否存在未被采集完的日志，如该参数值设置为true，当待删除agent存在未被采集完的日志时，将会抛出异常，不会删除该agent
     * @param uninstall 是否卸载 agent，该参数设置为true，将添加agent卸载任务
     * @param operator 操作人
     * 注：只要其中一个agent删除失败，将导致所有agent删除失败
     */
    void deleteAgentByIds(List<Long> ids, boolean checkAgentCompleteCollect, boolean uninstall, String operator);

    /**
     * 更新Agent对象
     * @param agentDO 待更新Agent对象
     * @param operator 操作人
     */
    void updateAgent(AgentDO agentDO, String operator);

    /**
     * 根据给定主机名获取主机名为hostName的Agent对象
     * @param hostName 主机名
     * @return 返回根据给定主机名获取到的主机名为hostName的Agent对象
     */
    AgentDO getAgentByHostName(String hostName);

    /**
     * 根据id查询对应agent对象
     * @param id agent对象 id
     * @return 返回根据id查询到的对应agent对象，如不存在，返回 null
     */
    AgentDO getById(Long id);

    /**
     * 根据 agentVersionId 获取该版本对应所有 agent 列表
     * @param agentVersionId AgentVersion 对象 id 值
     * @return 返回根据 agentVersionId 获取到的该版本对应所有 agent 列表
     */
    List<AgentDO> getAgentsByAgentVersionId(Long agentVersionId);

    /**
     * 根据 agent id 获取给定时间范围内对应 agent 运行时指标信息
     * @param agentId agent id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回根据 agent id 获取到的给定时间范围内对应 agent 运行时指标信息
     */
    List<MetricPanelGroup> listAgentMetrics(Long agentId, Long startTime, Long endTime);

    /**
     * 检查给定 Agent 健康度，并将检查结果信息更新至表 Agent
     * @param agentDO 待检查 agent 对象
     * @return 返回给定 agent 健康度检查结果，如给定Agent无须被诊断（如：处于 agent check 黑名单），返回 AgentHealthLevelEnum.GREEN
     */
    AgentHealthLevelEnum checkAgentHealth(AgentDO agentDO);

    /**
     * @return 返回系统全量Agent对象集
     */
    List<AgentDO> list();

    /**
     * 根据给定kafkaClusterId获取对应KafkaCluster对象关联的Agent对象集
     * @param kafkaClusterId KafkaCluster对象id值
     * @return 返回根据给定kafkaClusterId获取到的对应KafkaCluster对象关联的Agent对象集
     */
    List<AgentDO> getAgentListByKafkaClusterId(Long kafkaClusterId);

    /**
     * 根据给定路径 & 文件后缀匹配正则在给定主机匹配符合匹配规则的文件名集
     * @param hostName 主机名
     * @param path 对应目录或文件路径
     * @param suffixMatchRegular 文件后缀名匹配规则
     * @return 返回根据给定路径 & 文件后缀匹配正则匹配到的符合匹配规则的文件名集
     */
    List<String> listFiles(String hostName, String path, String suffixMatchRegular);

}
