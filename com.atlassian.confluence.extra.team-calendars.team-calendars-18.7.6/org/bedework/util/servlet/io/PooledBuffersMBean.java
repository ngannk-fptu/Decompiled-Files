/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet.io;

import org.bedework.util.jmx.ConfBaseMBean;
import org.bedework.util.jmx.MBeanInfo;
import org.bedework.util.servlet.io.PooledBuffersProperties;

public interface PooledBuffersMBean
extends ConfBaseMBean,
PooledBuffersProperties {
    @MBeanInfo(value="Small buffer statistics")
    public String getSmallBufferPoolStats();

    @MBeanInfo(value="Medium buffer statistics")
    public String getMediumBufferPoolStats();

    @MBeanInfo(value="Large buffer statistics")
    public String getLargeBufferPoolStats();
}

