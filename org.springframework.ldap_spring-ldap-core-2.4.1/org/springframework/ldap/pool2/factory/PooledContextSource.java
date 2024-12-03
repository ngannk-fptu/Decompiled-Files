/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.KeyedObjectPool
 *  org.apache.commons.pool2.KeyedPooledObjectFactory
 *  org.apache.commons.pool2.impl.GenericKeyedObjectPool
 *  org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package org.springframework.ldap.pool2.factory;

import java.util.Collection;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.DelegatingBaseLdapPathContextSourceSupport;
import org.springframework.ldap.pool2.DelegatingDirContext;
import org.springframework.ldap.pool2.DelegatingLdapContext;
import org.springframework.ldap.pool2.DirContextType;
import org.springframework.ldap.pool2.factory.DirContextPooledObjectFactory;
import org.springframework.ldap.pool2.factory.PoolConfig;
import org.springframework.ldap.pool2.validation.DirContextValidator;

public class PooledContextSource
extends DelegatingBaseLdapPathContextSourceSupport
implements ContextSource,
DisposableBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final GenericKeyedObjectPool<Object, Object> keyedObjectPool;
    private final DirContextPooledObjectFactory dirContextPooledObjectFactory = new DirContextPooledObjectFactory();
    private PoolConfig poolConfig;

    public PooledContextSource(PoolConfig poolConfig) {
        if (poolConfig != null) {
            this.poolConfig = poolConfig;
            GenericKeyedObjectPoolConfig objectPoolConfig = this.getConfig(poolConfig);
            this.keyedObjectPool = new GenericKeyedObjectPool((KeyedPooledObjectFactory)this.dirContextPooledObjectFactory, objectPoolConfig);
        } else {
            this.keyedObjectPool = new GenericKeyedObjectPool((KeyedPooledObjectFactory)this.dirContextPooledObjectFactory);
        }
    }

    public PoolConfig getPoolConfig() {
        return this.poolConfig;
    }

    public int getNumIdle() {
        return this.keyedObjectPool.getNumIdle();
    }

    public int getNumIdleRead() {
        return this.keyedObjectPool.getNumIdle((Object)DirContextType.READ_ONLY);
    }

    public int getNumIdleWrite() {
        return this.keyedObjectPool.getNumIdle((Object)DirContextType.READ_WRITE);
    }

    public int getNumActive() {
        return this.keyedObjectPool.getNumActive();
    }

    public int getNumActiveRead() {
        return this.keyedObjectPool.getNumActive((Object)DirContextType.READ_ONLY);
    }

    public int getNumActiveWrite() {
        return this.keyedObjectPool.getNumActive((Object)DirContextType.READ_WRITE);
    }

    public int getNumWaiters() {
        return this.keyedObjectPool.getNumWaiters();
    }

    public ContextSource getContextSource() {
        return this.dirContextPooledObjectFactory.getContextSource();
    }

    public DirContextValidator getDirContextValidator() {
        return this.dirContextPooledObjectFactory.getDirContextValidator();
    }

    public void setContextSource(ContextSource contextSource) {
        this.dirContextPooledObjectFactory.setContextSource(contextSource);
    }

    public void setDirContextValidator(DirContextValidator dirContextValidator) {
        this.dirContextPooledObjectFactory.setDirContextValidator(dirContextValidator);
    }

    public void setNonTransientExceptions(Collection<Class<? extends Throwable>> nonTransientExceptions) {
        this.dirContextPooledObjectFactory.setNonTransientExceptions(nonTransientExceptions);
    }

    public void destroy() throws Exception {
        try {
            this.keyedObjectPool.close();
        }
        catch (Exception e) {
            this.logger.warn("An exception occurred while closing the underlying pool.", (Throwable)e);
        }
    }

    @Override
    protected ContextSource getTarget() {
        return this.getContextSource();
    }

    @Override
    public DirContext getReadOnlyContext() {
        return this.getContext(DirContextType.READ_ONLY);
    }

    @Override
    public DirContext getReadWriteContext() {
        return this.getContext(DirContextType.READ_WRITE);
    }

    protected DirContext getContext(DirContextType dirContextType) {
        DirContext dirContext;
        try {
            dirContext = (DirContext)this.keyedObjectPool.borrowObject((Object)dirContextType);
        }
        catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed to borrow DirContext from pool.", (Throwable)e);
        }
        if (dirContext instanceof LdapContext) {
            return new DelegatingLdapContext((KeyedObjectPool<Object, Object>)this.keyedObjectPool, (LdapContext)dirContext, dirContextType);
        }
        return new DelegatingDirContext((KeyedObjectPool<Object, Object>)this.keyedObjectPool, dirContext, dirContextType);
    }

    @Override
    public DirContext getContext(String principal, String credentials) {
        throw new UnsupportedOperationException("Not supported for this implementation");
    }

    private GenericKeyedObjectPoolConfig getConfig(PoolConfig poolConfig) {
        GenericKeyedObjectPoolConfig objectPoolConfig = new GenericKeyedObjectPoolConfig();
        objectPoolConfig.setMaxTotalPerKey(poolConfig.getMaxTotalPerKey());
        objectPoolConfig.setMaxTotal(poolConfig.getMaxTotal());
        objectPoolConfig.setMaxIdlePerKey(poolConfig.getMaxIdlePerKey());
        objectPoolConfig.setMinIdlePerKey(poolConfig.getMinIdlePerKey());
        objectPoolConfig.setTestWhileIdle(poolConfig.isTestWhileIdle());
        objectPoolConfig.setTestOnReturn(poolConfig.isTestOnReturn());
        objectPoolConfig.setTestOnCreate(poolConfig.isTestOnCreate());
        objectPoolConfig.setTestOnBorrow(poolConfig.isTestOnBorrow());
        objectPoolConfig.setTimeBetweenEvictionRunsMillis(poolConfig.getTimeBetweenEvictionRunsMillis());
        objectPoolConfig.setEvictionPolicyClassName(poolConfig.getEvictionPolicyClassName());
        objectPoolConfig.setMinEvictableIdleTimeMillis(poolConfig.getMinEvictableIdleTimeMillis());
        objectPoolConfig.setNumTestsPerEvictionRun(poolConfig.getNumTestsPerEvictionRun());
        objectPoolConfig.setSoftMinEvictableIdleTimeMillis(poolConfig.getSoftMinEvictableIdleTimeMillis());
        objectPoolConfig.setJmxEnabled(poolConfig.isJmxEnabled());
        objectPoolConfig.setJmxNameBase(poolConfig.getJmxNameBase());
        objectPoolConfig.setJmxNamePrefix(poolConfig.getJmxNamePrefix());
        objectPoolConfig.setMaxWaitMillis(poolConfig.getMaxWaitMillis());
        objectPoolConfig.setFairness(poolConfig.isFairness());
        objectPoolConfig.setBlockWhenExhausted(poolConfig.isBlockWhenExhausted());
        objectPoolConfig.setLifo(poolConfig.isLifo());
        return objectPoolConfig;
    }
}

