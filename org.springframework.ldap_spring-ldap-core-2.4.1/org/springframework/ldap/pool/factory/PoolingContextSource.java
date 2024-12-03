/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedObjectPool
 *  org.apache.commons.pool.KeyedPoolableObjectFactory
 *  org.apache.commons.pool.impl.GenericKeyedObjectPool
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package org.springframework.ldap.pool.factory;

import java.util.Collection;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.support.DelegatingBaseLdapPathContextSourceSupport;
import org.springframework.ldap.pool.DelegatingDirContext;
import org.springframework.ldap.pool.DelegatingLdapContext;
import org.springframework.ldap.pool.DirContextType;
import org.springframework.ldap.pool.factory.DirContextPoolableObjectFactory;
import org.springframework.ldap.pool.validation.DirContextValidator;

public class PoolingContextSource
extends DelegatingBaseLdapPathContextSourceSupport
implements ContextSource,
DisposableBean {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final GenericKeyedObjectPool keyedObjectPool;
    private final DirContextPoolableObjectFactory dirContextPoolableObjectFactory = new DirContextPoolableObjectFactory();

    public PoolingContextSource() {
        this.keyedObjectPool = new GenericKeyedObjectPool();
        this.keyedObjectPool.setFactory((KeyedPoolableObjectFactory)this.dirContextPoolableObjectFactory);
    }

    public int getMaxActive() {
        return this.keyedObjectPool.getMaxActive();
    }

    public int getMaxIdle() {
        return this.keyedObjectPool.getMaxIdle();
    }

    public int getMaxTotal() {
        return this.keyedObjectPool.getMaxTotal();
    }

    public long getMaxWait() {
        return this.keyedObjectPool.getMaxWait();
    }

    public long getMinEvictableIdleTimeMillis() {
        return this.keyedObjectPool.getMinEvictableIdleTimeMillis();
    }

    public int getMinIdle() {
        return this.keyedObjectPool.getMinIdle();
    }

    public int getNumActive() {
        return this.keyedObjectPool.getNumActive();
    }

    public int getNumIdle() {
        return this.keyedObjectPool.getNumIdle();
    }

    public int getNumTestsPerEvictionRun() {
        return this.keyedObjectPool.getNumTestsPerEvictionRun();
    }

    public boolean getTestOnBorrow() {
        return this.keyedObjectPool.getTestOnBorrow();
    }

    public boolean getTestOnReturn() {
        return this.keyedObjectPool.getTestOnReturn();
    }

    public boolean getTestWhileIdle() {
        return this.keyedObjectPool.getTestWhileIdle();
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.keyedObjectPool.getTimeBetweenEvictionRunsMillis();
    }

    public byte getWhenExhaustedAction() {
        return this.keyedObjectPool.getWhenExhaustedAction();
    }

    public void setMaxActive(int maxActive) {
        this.keyedObjectPool.setMaxActive(maxActive);
    }

    public void setMaxIdle(int maxIdle) {
        this.keyedObjectPool.setMaxIdle(maxIdle);
    }

    public void setMaxTotal(int maxTotal) {
        this.keyedObjectPool.setMaxTotal(maxTotal);
    }

    public void setMaxWait(long maxWait) {
        this.keyedObjectPool.setMaxWait(maxWait);
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.keyedObjectPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public void setMinIdle(int poolSize) {
        this.keyedObjectPool.setMinIdle(poolSize);
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.keyedObjectPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.keyedObjectPool.setTestOnBorrow(testOnBorrow);
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.keyedObjectPool.setTestOnReturn(testOnReturn);
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.keyedObjectPool.setTestWhileIdle(testWhileIdle);
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.keyedObjectPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public void setWhenExhaustedAction(byte whenExhaustedAction) {
        this.keyedObjectPool.setWhenExhaustedAction(whenExhaustedAction);
    }

    public ContextSource getContextSource() {
        return this.dirContextPoolableObjectFactory.getContextSource();
    }

    public DirContextValidator getDirContextValidator() {
        return this.dirContextPoolableObjectFactory.getDirContextValidator();
    }

    public void setContextSource(ContextSource contextSource) {
        this.dirContextPoolableObjectFactory.setContextSource(contextSource);
    }

    public void setDirContextValidator(DirContextValidator dirContextValidator) {
        this.dirContextPoolableObjectFactory.setDirContextValidator(dirContextValidator);
    }

    public void setNonTransientExceptions(Collection<Class<? extends Throwable>> nonTransientExceptions) {
        this.dirContextPoolableObjectFactory.setNonTransientExceptions(nonTransientExceptions);
    }

    public void destroy() throws Exception {
        try {
            this.keyedObjectPool.close();
        }
        catch (Exception e) {
            this.logger.warn("An exception occured while closing the underlying pool.", (Throwable)e);
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
            return new DelegatingLdapContext((KeyedObjectPool)this.keyedObjectPool, (LdapContext)dirContext, dirContextType);
        }
        return new DelegatingDirContext((KeyedObjectPool)this.keyedObjectPool, dirContext, dirContextType);
    }

    @Override
    public DirContext getContext(String principal, String credentials) {
        throw new UnsupportedOperationException("Not supported for this implementation");
    }
}

