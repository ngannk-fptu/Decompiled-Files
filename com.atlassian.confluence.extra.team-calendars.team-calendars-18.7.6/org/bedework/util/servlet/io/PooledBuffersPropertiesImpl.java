/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet.io;

import org.bedework.util.config.ConfInfo;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.misc.ToString;
import org.bedework.util.servlet.io.PooledBuffersProperties;

@ConfInfo(elementName="pooled-buffer-properties", type="org.bedework.calfacade.configs.PooledBufferProperties")
public class PooledBuffersPropertiesImpl
extends ConfigBase<PooledBuffersPropertiesImpl>
implements PooledBuffersProperties {
    private int smallBufferSize;
    private int mediumBufferSize;
    private int largeBufferSize;
    private int smallBufferPoolSize;
    private int mediumBufferPoolSize;
    private int largeBufferPoolSize;

    @Override
    public int getSmallBufferSize() {
        return this.smallBufferSize;
    }

    @Override
    public void setSmallBufferSize(int val) {
        this.smallBufferSize = val;
    }

    @Override
    public int getMediumBufferSize() {
        return this.mediumBufferSize;
    }

    @Override
    public void setMediumBufferSize(int val) {
        this.mediumBufferSize = val;
    }

    @Override
    public int getLargeBufferSize() {
        return this.largeBufferSize;
    }

    @Override
    public void setLargeBufferSize(int val) {
        this.largeBufferSize = val;
    }

    @Override
    public int getSmallBufferPoolSize() {
        return this.smallBufferPoolSize;
    }

    @Override
    public void setSmallBufferPoolSize(int val) {
        this.smallBufferPoolSize = val;
    }

    @Override
    public int getMediumBufferPoolSize() {
        return this.mediumBufferPoolSize;
    }

    @Override
    public void setMediumBufferPoolSize(int val) {
        this.mediumBufferPoolSize = val;
    }

    @Override
    public int getLargeBufferPoolSize() {
        return this.largeBufferPoolSize;
    }

    @Override
    public void setLargeBufferPoolSize(int val) {
        this.largeBufferPoolSize = val;
    }

    @Override
    public String toString() {
        ToString ts = new ToString(this);
        ts.append("smallBufferSize", this.getSmallBufferSize());
        ts.append("mediumBufferSize", this.getMediumBufferSize());
        ts.append("largeBufferSize", this.getLargeBufferSize());
        ts.append("smallBufferPoolSize", this.getSmallBufferPoolSize());
        ts.append("mediumBufferPoolSize", this.getMediumBufferPoolSize());
        ts.append("largeBufferPoolSize", this.getLargeBufferPoolSize());
        return ts.toString();
    }
}

