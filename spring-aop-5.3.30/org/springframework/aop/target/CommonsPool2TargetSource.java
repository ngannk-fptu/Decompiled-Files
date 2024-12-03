/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.ObjectPool
 *  org.apache.commons.pool2.PooledObject
 *  org.apache.commons.pool2.PooledObjectFactory
 *  org.apache.commons.pool2.impl.DefaultPooledObject
 *  org.apache.commons.pool2.impl.GenericObjectPool
 *  org.apache.commons.pool2.impl.GenericObjectPoolConfig
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.aop.target;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.aop.target.AbstractPoolingTargetSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class CommonsPool2TargetSource
extends AbstractPoolingTargetSource
implements PooledObjectFactory<Object> {
    private int maxIdle = 8;
    private int minIdle = 0;
    private long maxWait = -1L;
    private long timeBetweenEvictionRunsMillis = -1L;
    private long minEvictableIdleTimeMillis = 1800000L;
    private boolean blockWhenExhausted = true;
    @Nullable
    private ObjectPool pool;

    public CommonsPool2TargetSource() {
        this.setMaxSize(8);
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxIdle() {
        return this.maxIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMinIdle() {
        return this.minIdle;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public long getMaxWait() {
        return this.maxWait;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public boolean isBlockWhenExhausted() {
        return this.blockWhenExhausted;
    }

    @Override
    protected final void createPool() {
        this.logger.debug((Object)"Creating Commons object pool");
        this.pool = this.createObjectPool();
    }

    protected ObjectPool createObjectPool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(this.getMaxSize());
        config.setMaxIdle(this.getMaxIdle());
        config.setMinIdle(this.getMinIdle());
        config.setMaxWaitMillis(this.getMaxWait());
        config.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
        config.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
        config.setBlockWhenExhausted(this.isBlockWhenExhausted());
        return new GenericObjectPool((PooledObjectFactory)this, config);
    }

    @Override
    public Object getTarget() throws Exception {
        Assert.state((this.pool != null ? 1 : 0) != 0, (String)"No Commons ObjectPool available");
        return this.pool.borrowObject();
    }

    @Override
    public void releaseTarget(Object target) throws Exception {
        if (this.pool != null) {
            this.pool.returnObject(target);
        }
    }

    @Override
    public int getActiveCount() throws UnsupportedOperationException {
        return this.pool != null ? this.pool.getNumActive() : 0;
    }

    @Override
    public int getIdleCount() throws UnsupportedOperationException {
        return this.pool != null ? this.pool.getNumIdle() : 0;
    }

    public void destroy() throws Exception {
        if (this.pool != null) {
            this.logger.debug((Object)"Closing Commons ObjectPool");
            this.pool.close();
        }
    }

    public PooledObject<Object> makeObject() throws Exception {
        return new DefaultPooledObject(this.newPrototypeInstance());
    }

    public void destroyObject(PooledObject<Object> p) throws Exception {
        this.destroyPrototypeInstance(p.getObject());
    }

    public boolean validateObject(PooledObject<Object> p) {
        return true;
    }

    public void activateObject(PooledObject<Object> p) throws Exception {
    }

    public void passivateObject(PooledObject<Object> p) throws Exception {
    }
}

