/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObjectInfoMBean;

public class DefaultPooledObjectInfo
implements DefaultPooledObjectInfoMBean {
    private final PooledObject<?> pooledObject;

    public DefaultPooledObjectInfo(PooledObject<?> pooledObject) {
        this.pooledObject = pooledObject;
    }

    @Override
    public long getCreateTime() {
        return this.pooledObject.getCreateTime();
    }

    @Override
    public String getCreateTimeFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        return sdf.format(this.pooledObject.getCreateTime());
    }

    @Override
    public long getLastBorrowTime() {
        return this.pooledObject.getLastBorrowTime();
    }

    @Override
    public String getLastBorrowTimeFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        return sdf.format(this.pooledObject.getLastBorrowTime());
    }

    @Override
    public String getLastBorrowTrace() {
        StringWriter sw = new StringWriter();
        this.pooledObject.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @Override
    public long getLastReturnTime() {
        return this.pooledObject.getLastReturnTime();
    }

    @Override
    public String getLastReturnTimeFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        return sdf.format(this.pooledObject.getLastReturnTime());
    }

    @Override
    public String getPooledObjectType() {
        return this.pooledObject.getObject().getClass().getName();
    }

    @Override
    public String getPooledObjectToString() {
        return this.pooledObject.getObject().toString();
    }

    @Override
    public long getBorrowedCount() {
        if (this.pooledObject instanceof DefaultPooledObject) {
            return ((DefaultPooledObject)this.pooledObject).getBorrowedCount();
        }
        return -1L;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DefaultPooledObjectInfo [pooledObject=");
        builder.append(this.pooledObject);
        builder.append("]");
        return builder.toString();
    }
}

