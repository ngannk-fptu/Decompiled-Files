/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.pool.PoolStats
 */
package org.bedework.util.http.service;

import org.apache.http.pool.PoolStats;
import org.bedework.util.http.service.HttpConfig;
import org.bedework.util.jmx.ConfBaseMBean;
import org.bedework.util.jmx.MBeanInfo;

public interface HttpOutMBean
extends ConfBaseMBean,
HttpConfig {
    public PoolStats getConnStats();

    @MBeanInfo(value="(Re)load the configuration")
    public String loadConfig();
}

