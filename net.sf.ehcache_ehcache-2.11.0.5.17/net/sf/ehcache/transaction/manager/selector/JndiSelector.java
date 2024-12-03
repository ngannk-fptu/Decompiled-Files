/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.manager.selector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import net.sf.ehcache.transaction.manager.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JndiSelector
extends Selector {
    private static final Logger LOG = LoggerFactory.getLogger(JndiSelector.class);
    private volatile String jndiName;

    public JndiSelector(String vendor, String jndiName) {
        super(vendor);
        this.jndiName = jndiName;
    }

    public String getJndiName() {
        return this.jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected TransactionManager doLookup() {
        TransactionManager transactionManager;
        InitialContext initialContext;
        try {
            initialContext = new InitialContext();
        }
        catch (NamingException ne) {
            LOG.debug("cannot create initial context", (Throwable)ne);
            return null;
        }
        try {
            Object jndiObject = initialContext.lookup(this.getJndiName());
            if (!(jndiObject instanceof TransactionManager)) return null;
            transactionManager = (TransactionManager)jndiObject;
        }
        catch (NamingException e) {
            LOG.debug("Couldn't locate TransactionManager for {} under {}", (Object)this.getVendor(), (Object)this.getJndiName());
            return null;
        }
        catch (Throwable throwable) {
            throw throwable;
        }
        try {
            initialContext.close();
            return transactionManager;
        }
        catch (NamingException ne) {
            LOG.warn("error closing initial context", (Throwable)ne);
        }
        return transactionManager;
    }
}

