package com.didichuxing.datachannel.swan.agent.engine.conf;

import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ComponentConfig;

/**
 * @description: 配置接口
 * @author: huangjw
 * @Date: 19/6/28 17:54
 */
public interface Configurable {
    void configure(ComponentConfig config);

    boolean onChange(ComponentConfig newOne);
}
