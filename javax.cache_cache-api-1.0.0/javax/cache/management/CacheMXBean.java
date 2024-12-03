/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.management;

import javax.management.MXBean;

@MXBean
public interface CacheMXBean {
    public String getKeyType();

    public String getValueType();

    public boolean isReadThrough();

    public boolean isWriteThrough();

    public boolean isStoreByValue();

    public boolean isStatisticsEnabled();

    public boolean isManagementEnabled();
}

