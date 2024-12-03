/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.constructs.web;

import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownListener
implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(ShutdownListener.class);

    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        List<CacheManager> knownCacheManagers = CacheManager.ALL_CACHE_MANAGERS;
        if (LOG.isDebugEnabled()) {
            LOG.debug("Shutting down " + knownCacheManagers.size() + " CacheManagers.");
        }
        while (!knownCacheManagers.isEmpty()) {
            CacheManager.ALL_CACHE_MANAGERS.get(0).shutdown();
        }
    }
}

