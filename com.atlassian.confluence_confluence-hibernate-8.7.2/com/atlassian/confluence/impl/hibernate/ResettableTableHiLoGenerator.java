/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.hibernate.HibernateException
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.id.IdentifierGeneratorHelper
 *  org.hibernate.id.enhanced.SequenceStyleGenerator
 *  org.hibernate.internal.util.config.ConfigurationHelper
 *  org.hibernate.service.ServiceRegistry
 *  org.hibernate.type.Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.hibernate;

import com.atlassian.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResettableTableHiLoGenerator
extends SequenceStyleGenerator {
    public static final String MAX_LO = "max_lo";
    private static final Logger log = LoggerFactory.getLogger(ResettableTableHiLoGenerator.class);
    private long hi;
    private int lo;
    private int maxLo;
    private Class<?> returnClass;
    private final Lock generateResetLock = new ReentrantLock();

    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        Properties props = new Properties();
        props.setProperty("force_table_use", "true");
        props.setProperty("sequence_name", "hibernate_unique_key");
        props.setProperty("value_column", "next_hi");
        props.putAll((Map<?, ?>)params);
        this.configureSuper(type, props, serviceRegistry);
        this.maxLo = ConfigurationHelper.getInt((String)MAX_LO, (Map)props, (int)Short.MAX_VALUE);
        this.lo = this.maxLo + 1;
        this.returnClass = type.getReturnedClass();
    }

    @VisibleForTesting
    void configureSuper(Type type, Properties props, ServiceRegistry serviceRegistry) {
        super.configure(type, props, serviceRegistry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        this.generateResetLock.lock();
        try {
            if (this.lo > this.maxLo) {
                long hival = this.generateSuper(session, obj);
                this.lo = 1;
                this.hi = hival * (long)(this.maxLo + 1);
                log.debug("new hi value: " + hival);
            }
            Number number = IdentifierGeneratorHelper.getIntegralDataTypeHolder(this.returnClass).initialize(this.hi + (long)this.lo++).makeValue();
            return number;
        }
        finally {
            this.generateResetLock.unlock();
        }
    }

    @VisibleForTesting
    long generateSuper(SharedSessionContractImplementor session, Object obj) {
        return ((Number)super.generate(session, obj)).longValue();
    }

    public int getMaxLo() {
        return this.maxLo;
    }

    @VisibleForTesting
    int getLo() {
        return this.lo;
    }

    @VisibleForTesting
    long getHi() {
        return this.hi;
    }

    public void reset() {
        this.generateResetLock.lock();
        try {
            this.lo = this.maxLo + 1;
        }
        finally {
            this.generateResetLock.unlock();
        }
    }
}

