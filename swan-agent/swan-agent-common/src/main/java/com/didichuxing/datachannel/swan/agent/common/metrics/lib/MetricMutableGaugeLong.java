/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.datachannel.swan.agent.common.metrics.lib;

import com.didichuxing.datachannel.swan.agent.common.metrics.MetricsRecordBuilder;

/**
 * A mutable long gauge
 */
public class MetricMutableGaugeLong extends MetricMutableGauge<Long> {

    private volatile long value;

    /**
     * Construct a mutable long gauge metric
     * @param name  of the gauge
     * @param description of the gauge
     * @param initValue the initial value of the gauge
     */
    public MetricMutableGaugeLong(String name, String description, long initValue) {
        super(name, description);
        this.value = initValue;
    }

    public synchronized void incr() {
        ++value;
        setChanged();
    }

    /**
     * Increment by delta
     * @param delta of the increment
     */
    public synchronized void incr(long delta) {
        value += delta;
        setChanged();
    }

    public synchronized void decr() {
        --value;
        setChanged();
    }

    /**
     * decrement by delta
     * @param delta of the decrement
     */
    public synchronized void decr(long delta) {
        value -= delta;
        setChanged();
    }

    /**
     * Set the value of the metric
     * @param value to set
     */
    public void set(long value) {
        this.value = value;
        setChanged();
    }

    public void snapshot(MetricsRecordBuilder builder, boolean all) {
        if (all || changed()) {
            builder.addGauge(name, description, value);
            clearChanged();
        }
    }

}
