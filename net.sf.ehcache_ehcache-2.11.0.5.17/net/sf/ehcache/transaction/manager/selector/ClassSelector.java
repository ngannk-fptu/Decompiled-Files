/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.manager.selector;

import javax.transaction.TransactionManager;
import net.sf.ehcache.transaction.manager.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClassSelector
extends Selector {
    private static final Logger LOG = LoggerFactory.getLogger(ClassSelector.class);
    private final String classname;

    public ClassSelector(String vendor, String classname) {
        super(vendor);
        this.classname = classname;
    }

    @Override
    protected TransactionManager doLookup() {
        TransactionManager transactionManager = null;
        try {
            Class<?> txManagerClass = Class.forName(this.classname);
            transactionManager = (TransactionManager)txManagerClass.newInstance();
        }
        catch (ClassNotFoundException e) {
            LOG.debug("FactorySelector failed lookup", (Throwable)e);
        }
        catch (InstantiationException e) {
            LOG.debug("FactorySelector failed lookup", (Throwable)e);
        }
        catch (IllegalAccessException e) {
            LOG.debug("FactorySelector failed lookup", (Throwable)e);
        }
        return transactionManager;
    }
}

