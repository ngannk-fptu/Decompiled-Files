/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

public interface MessageDispatchInterceptorMBean {
    public int getOptionFlag();

    public boolean isAlwaysSend();

    public void setAlwaysSend(boolean var1);

    public long getMaxQueueSize();

    public long getCurrentSize();

    public long getKeepAliveTime();

    public int getMaxSpareThreads();

    public int getMaxThreads();

    public int getPoolSize();

    public int getActiveCount();

    public long getTaskCount();

    public long getCompletedTaskCount();
}

