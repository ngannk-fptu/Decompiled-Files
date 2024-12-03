/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

public interface FragmentationInterceptorMBean {
    public int getMaxSize();

    public long getExpire();

    public void setMaxSize(int var1);

    public void setExpire(long var1);
}

