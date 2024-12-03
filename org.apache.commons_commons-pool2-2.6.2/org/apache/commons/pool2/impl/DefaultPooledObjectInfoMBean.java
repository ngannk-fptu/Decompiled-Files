/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

public interface DefaultPooledObjectInfoMBean {
    public long getCreateTime();

    public String getCreateTimeFormatted();

    public long getLastBorrowTime();

    public String getLastBorrowTimeFormatted();

    public String getLastBorrowTrace();

    public long getLastReturnTime();

    public String getLastReturnTimeFormatted();

    public String getPooledObjectType();

    public String getPooledObjectToString();

    public long getBorrowedCount();
}

