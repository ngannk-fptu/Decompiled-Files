/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.transaction.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.transaction.TransactionManager;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.transaction.manager.TransactionManagerLookup;
import net.sf.ehcache.transaction.manager.selector.AtomikosSelector;
import net.sf.ehcache.transaction.manager.selector.BitronixSelector;
import net.sf.ehcache.transaction.manager.selector.GenericJndiSelector;
import net.sf.ehcache.transaction.manager.selector.GlassfishSelector;
import net.sf.ehcache.transaction.manager.selector.JndiSelector;
import net.sf.ehcache.transaction.manager.selector.NullSelector;
import net.sf.ehcache.transaction.manager.selector.Selector;
import net.sf.ehcache.transaction.manager.selector.WeblogicSelector;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTransactionManagerLookup
implements TransactionManagerLookup {
    private static final Logger LOG = LoggerFactory.getLogger((String)DefaultTransactionManagerLookup.class.getName());
    private final Lock lock = new ReentrantLock();
    private final List<EhcacheXAResource> uninitializedEhcacheXAResources = new ArrayList<EhcacheXAResource>();
    private volatile boolean initialized = false;
    private volatile Selector selector;
    private final JndiSelector defaultJndiSelector = new GenericJndiSelector();
    private final Selector[] transactionManagerSelectors = new Selector[]{this.defaultJndiSelector, new GlassfishSelector(), new WeblogicSelector(), new BitronixSelector(), new AtomikosSelector()};

    @Override
    public void init() {
        if (!this.initialized) {
            this.lock.lock();
            try {
                Iterator<EhcacheXAResource> iterator = this.uninitializedEhcacheXAResources.iterator();
                while (iterator.hasNext()) {
                    if (this.getTransactionManager() == null) {
                        throw new CacheException("No Transaction Manager could be located, cannot initialize DefaultTransactionManagerLookup. Caches which registered an XAResource: " + this.getUninitializedXAResourceCacheNames());
                    }
                    EhcacheXAResource resource = iterator.next();
                    this.selector.registerResource(resource, true);
                    iterator.remove();
                }
            }
            finally {
                this.lock.unlock();
            }
            this.initialized = true;
        }
    }

    private Set<String> getUninitializedXAResourceCacheNames() {
        HashSet<String> names = new HashSet<String>();
        for (EhcacheXAResource xar : this.uninitializedEhcacheXAResources) {
            names.add(xar.getCacheName());
        }
        return names;
    }

    @Override
    public TransactionManager getTransactionManager() {
        if (this.selector == null) {
            this.lock.lock();
            try {
                if (this.selector == null) {
                    this.lookupTransactionManager();
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        return this.selector.getTransactionManager();
    }

    private void lookupTransactionManager() {
        for (Selector s : this.transactionManagerSelectors) {
            TransactionManager transactionManager = s.getTransactionManager();
            if (transactionManager == null) continue;
            this.selector = s;
            LOG.debug("Found TransactionManager for {}", (Object)s.getVendor());
            return;
        }
        this.selector = new NullSelector();
        LOG.debug("Found no TransactionManager");
    }

    @Override
    public void register(EhcacheXAResource resource, boolean forRecovery) {
        if (this.initialized) {
            this.selector.registerResource(resource, forRecovery);
        } else {
            this.lock.lock();
            try {
                this.uninitializedEhcacheXAResources.add(resource);
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unregister(EhcacheXAResource resource, boolean forRecovery) {
        if (this.initialized) {
            this.selector.unregisterResource(resource, forRecovery);
        } else {
            this.lock.lock();
            try {
                Iterator<EhcacheXAResource> iterator = this.uninitializedEhcacheXAResources.iterator();
                while (iterator.hasNext()) {
                    EhcacheXAResource uninitializedEhcacheXAResource = iterator.next();
                    if (uninitializedEhcacheXAResource != resource) continue;
                    iterator.remove();
                    break;
                }
            }
            finally {
                this.lock.unlock();
            }
        }
    }

    @Override
    public void setProperties(Properties properties) {
        String jndiName;
        if (properties != null && (jndiName = properties.getProperty("jndiName")) != null) {
            this.defaultJndiSelector.setJndiName(jndiName);
        }
    }
}

