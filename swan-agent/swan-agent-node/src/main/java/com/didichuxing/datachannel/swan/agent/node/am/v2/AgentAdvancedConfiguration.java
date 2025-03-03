package com.didichuxing.datachannel.swan.agent.node.am.v2;

public class AgentAdvancedConfiguration {

    /**
     * agent 限流开始阈值 默认 1kb
     */
    private Long agentLimitStartThreshold = 20000000L;

    /**L
     * agent 限流最小阈值 默认 0 byte
     */
    private Long agentLimitMinThreshold   = 1000000L;

    public Long getAgentLimitStartThreshold() {
        return agentLimitStartThreshold;
    }

    public void setAgentLimitStartThreshold(Long agentLimitStartThreshold) {
        this.agentLimitStartThreshold = agentLimitStartThreshold;
    }

    public Long getAgentLimitMinThreshold() {
        return agentLimitMinThreshold;
    }

    public void setAgentLimitMinThreshold(Long agentLimitMinThreshold) {
        this.agentLimitMinThreshold = agentLimitMinThreshold;
    }
}
