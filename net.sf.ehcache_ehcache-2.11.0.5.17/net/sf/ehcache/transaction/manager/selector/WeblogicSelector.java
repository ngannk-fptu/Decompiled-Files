/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.manager.selector;

import java.lang.reflect.Method;
import javax.transaction.xa.XAResource;
import net.sf.ehcache.transaction.manager.selector.FactorySelector;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeblogicSelector
extends FactorySelector {
    private static final Logger LOG = LoggerFactory.getLogger(WeblogicSelector.class);

    public WeblogicSelector() {
        super("Weblogic", "weblogic.transaction.TxHelper", "getTransactionManager");
    }

    @Override
    public void registerResource(EhcacheXAResource ehcacheXAResource, boolean forRecovery) {
        if (!forRecovery) {
            return;
        }
        String uniqueName = ehcacheXAResource.getCacheName();
        try {
            Class<?> tmImplClass = Class.forName("weblogic.transaction.TransactionManager");
            Class[] signature = new Class[]{String.class, XAResource.class};
            Object[] args = new Object[]{uniqueName, ehcacheXAResource};
            Method method = tmImplClass.getMethod("registerResource", signature);
            method.invoke((Object)this.getTransactionManager(), args);
        }
        catch (Exception e) {
            LOG.error("unable to register resource of cache " + uniqueName + " with Weblogic", (Throwable)e);
        }
    }

    @Override
    public void unregisterResource(EhcacheXAResource ehcacheXAResource, boolean forRecovery) {
        if (!forRecovery) {
            return;
        }
        String uniqueName = ehcacheXAResource.getCacheName();
        try {
            Class<?> tmImplClass = Class.forName("weblogic.transaction.TransactionManager");
            Class[] signature = new Class[]{String.class, Boolean.TYPE};
            Object[] args = new Object[]{uniqueName, Boolean.TRUE};
            Method method = tmImplClass.getMethod("unregisterResource", signature);
            method.invoke((Object)this.getTransactionManager(), args);
        }
        catch (Exception e) {
            LOG.error("unable to unregister resource of cache " + uniqueName + " with Weblogic", (Throwable)e);
        }
    }
}

