/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.manager.selector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.transaction.TransactionManager;
import net.sf.ehcache.transaction.manager.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FactorySelector
extends Selector {
    private static final Logger LOG = LoggerFactory.getLogger(FactorySelector.class);
    private final String factoryClassName;
    private final String factoryMethodName;

    public FactorySelector(String vendor, String factoryClassName, String factoryMethodName) {
        super(vendor);
        this.factoryClassName = factoryClassName;
        this.factoryMethodName = factoryMethodName;
    }

    @Override
    protected TransactionManager doLookup() {
        TransactionManager transactionManager = null;
        try {
            Class<?> factoryClass = Class.forName(this.factoryClassName);
            Class<?>[] signature = null;
            Object[] args = null;
            Method method = factoryClass.getMethod(this.factoryMethodName, signature);
            transactionManager = (TransactionManager)method.invoke(null, args);
        }
        catch (ClassNotFoundException e) {
            LOG.debug("FactorySelector failed lookup: {}", (Object)e);
        }
        catch (NoSuchMethodException e) {
            LOG.debug("FactorySelector failed lookup: {}", (Object)e);
        }
        catch (InvocationTargetException e) {
            LOG.debug("FactorySelector failed lookup: {}", (Object)e);
        }
        catch (IllegalAccessException e) {
            LOG.debug("FactorySelector failed lookup: {}", (Object)e);
        }
        return transactionManager;
    }
}

