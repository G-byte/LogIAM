package com.didichuxing.datachannel.agentmanager.common.bean.domain.host;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.BaseDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机信息
 */
@Data
public class HostAgentDO extends BaseDO {

    /**
     * 主机信息唯一标识
     */
    private Long hostId;
    /**
     * 主机名
     */
    private String hostName;
    /**
     * 主机 ip
     */
    private String hostIp;
    /**
     * 主机类型
     * 0：主机
     * 1：容器
     */
    private Integer hostType;
    /**
     * agent健康等级
     */
    private Integer agentHealthLevel;
    /**
     * 主机所属机器单元
     */
    private String hostMachineZone;
    /**
     * 主机创建时间
     */
    private Date hostCreateTime;
    /**
     * agent 版本 id
     */
    private Long agentVersionId;
    /**
     * Agent对象id
     */
    private Long agentId;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getAgentVersionId() {
        return agentVersionId;
    }

    public void setAgentVersionId(Long agentVersionId) {
        this.agentVersionId = agentVersionId;
    }

    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public Integer getHostType() {
        return hostType;
    }

    public void setHostType(Integer hostType) {
        this.hostType = hostType;
    }

    public Integer getAgentHealthLevel() {
        return agentHealthLevel;
    }

    public void setAgentHealthLevel(Integer agentHealthLevel) {
        this.agentHealthLevel = agentHealthLevel;
    }

    public String getHostMachineZone() {
        return hostMachineZone;
    }

    public void setHostMachineZone(String hostMachineZone) {
        this.hostMachineZone = hostMachineZone;
    }

    public Date getHostCreateTime() {
        return hostCreateTime;
    }

    public void setHostCreateTime(Date hostCreateTime) {
        this.hostCreateTime = hostCreateTime;
    }
}