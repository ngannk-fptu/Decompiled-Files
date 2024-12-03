/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.servlet.io;

import java.io.IOException;
import org.bedework.util.jmx.ConfBase;
import org.bedework.util.servlet.io.BufferPool;
import org.bedework.util.servlet.io.PooledBuffersMBean;
import org.bedework.util.servlet.io.PooledBuffersProperties;
import org.bedework.util.servlet.io.PooledBuffersPropertiesImpl;

public class PooledBuffers
extends ConfBase<PooledBuffersPropertiesImpl>
implements PooledBuffersMBean {
    public static final String confuriPname = "org.bedework.io.confuri";
    private static BufferPool smallBufferPool;
    private static BufferPool mediumBufferPool;
    private static BufferPool largeBufferPool;
    private static PooledBuffersProperties staticConf;
    private static final String nm = "pooledBuffers";

    public PooledBuffers() {
        super(PooledBuffers.getServiceName(nm));
        this.setConfigName(nm);
        this.setConfigPname(confuriPname);
        this.loadConfig();
        smallBufferPool = new BufferPool(((PooledBuffersPropertiesImpl)this.getConfig()).getSmallBufferSize(), ((PooledBuffersPropertiesImpl)this.getConfig()).getSmallBufferPoolSize());
        mediumBufferPool = new BufferPool(((PooledBuffersPropertiesImpl)this.getConfig()).getMediumBufferSize(), ((PooledBuffersPropertiesImpl)this.getConfig()).getMediumBufferPoolSize());
        largeBufferPool = new BufferPool(((PooledBuffersPropertiesImpl)this.getConfig()).getLargeBufferSize(), ((PooledBuffersPropertiesImpl)this.getConfig()).getLargeBufferPoolSize());
    }

    public static String getServiceName(String name) {
        return "org.bedework.io:service=" + name;
    }

    @Override
    public String loadConfig() {
        String res = this.loadConfig(PooledBuffersPropertiesImpl.class);
        staticConf = (PooledBuffersProperties)this.getConfig();
        return res;
    }

    static void release(BufferPool.Buffer buff) throws IOException {
        if (buff.buf.length == staticConf.getSmallBufferSize()) {
            smallBufferPool.put(buff);
        } else if (buff.buf.length == staticConf.getMediumBufferSize()) {
            mediumBufferPool.put(buff);
        } else if (buff.buf.length == staticConf.getLargeBufferSize()) {
            largeBufferPool.put(buff);
        }
    }

    static BufferPool.Buffer getSmallBuffer() {
        return smallBufferPool.get();
    }

    static BufferPool.Buffer getMediumBuffer() {
        return mediumBufferPool.get();
    }

    static BufferPool.Buffer getLargeBuffer() {
        return largeBufferPool.get();
    }

    @Override
    public int getSmallBufferSize() {
        return ((PooledBuffersPropertiesImpl)this.getConfig()).getSmallBufferSize();
    }

    @Override
    public void setSmallBufferSize(int val) {
        ((PooledBuffersPropertiesImpl)this.getConfig()).setSmallBufferSize(val);
        smallBufferPool.setBufferSize(val);
    }

    @Override
    public int getMediumBufferSize() {
        return ((PooledBuffersPropertiesImpl)this.getConfig()).getMediumBufferSize();
    }

    @Override
    public void setMediumBufferSize(int val) {
        ((PooledBuffersPropertiesImpl)this.getConfig()).setMediumBufferSize(val);
        mediumBufferPool.setBufferSize(val);
    }

    @Override
    public int getLargeBufferSize() {
        return ((PooledBuffersPropertiesImpl)this.getConfig()).getLargeBufferSize();
    }

    @Override
    public void setLargeBufferSize(int val) {
        ((PooledBuffersPropertiesImpl)this.getConfig()).setLargeBufferSize(val);
        largeBufferPool.setBufferSize(val);
    }

    @Override
    public int getSmallBufferPoolSize() {
        return ((PooledBuffersPropertiesImpl)this.getConfig()).getSmallBufferPoolSize();
    }

    @Override
    public void setSmallBufferPoolSize(int val) {
        ((PooledBuffersPropertiesImpl)this.getConfig()).setSmallBufferPoolSize(val);
        smallBufferPool.setPoolMaxSize(val);
    }

    @Override
    public int getMediumBufferPoolSize() {
        return ((PooledBuffersPropertiesImpl)this.getConfig()).getMediumBufferPoolSize();
    }

    @Override
    public void setMediumBufferPoolSize(int val) {
        ((PooledBuffersPropertiesImpl)this.getConfig()).setMediumBufferPoolSize(val);
        mediumBufferPool.setPoolMaxSize(val);
    }

    @Override
    public int getLargeBufferPoolSize() {
        return ((PooledBuffersPropertiesImpl)this.getConfig()).getLargeBufferPoolSize();
    }

    @Override
    public void setLargeBufferPoolSize(int val) {
        ((PooledBuffersPropertiesImpl)this.getConfig()).setLargeBufferPoolSize(val);
        largeBufferPool.setPoolMaxSize(val);
    }

    @Override
    public String getSmallBufferPoolStats() {
        return smallBufferPool.getStats();
    }

    @Override
    public String getMediumBufferPoolStats() {
        return mediumBufferPool.getStats();
    }

    @Override
    public String getLargeBufferPoolStats() {
        return largeBufferPool.getStats();
    }
}

