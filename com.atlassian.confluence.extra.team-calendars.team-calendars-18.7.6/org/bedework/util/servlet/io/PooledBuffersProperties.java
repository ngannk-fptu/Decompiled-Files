/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet.io;

import org.bedework.util.config.ConfInfo;
import org.bedework.util.jmx.MBeanInfo;

@ConfInfo(elementName="pooled-buffers-properties")
public interface PooledBuffersProperties {
    public void setSmallBufferSize(int var1);

    @MBeanInfo(value="Small buffer size")
    public int getSmallBufferSize();

    public void setMediumBufferSize(int var1);

    @MBeanInfo(value="Medium buffer size")
    public int getMediumBufferSize();

    public void setLargeBufferSize(int var1);

    @MBeanInfo(value="Large buffer size")
    public int getLargeBufferSize();

    public void setSmallBufferPoolSize(int var1);

    @MBeanInfo(value="Small buffer pool size")
    public int getSmallBufferPoolSize();

    public void setMediumBufferPoolSize(int var1);

    @MBeanInfo(value="Medium buffer pool size")
    public int getMediumBufferPoolSize();

    public void setLargeBufferPoolSize(int var1);

    @MBeanInfo(value="Large buffer pool size")
    public int getLargeBufferPoolSize();
}

