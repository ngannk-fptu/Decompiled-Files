/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.config.Configuration
 *  net.sf.ehcache.config.ConfigurationFactory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class EhCacheManagerFactoryBean
implements FactoryBean<CacheManager>,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private Resource configLocation;
    @Nullable
    private String cacheManagerName;
    private boolean acceptExisting = false;
    private boolean shared = false;
    @Nullable
    private CacheManager cacheManager;
    private boolean locallyManaged = true;

    public void setConfigLocation(Resource configLocation) {
        this.configLocation = configLocation;
    }

    public void setCacheManagerName(String cacheManagerName) {
        this.cacheManagerName = cacheManagerName;
    }

    public void setAcceptExisting(boolean acceptExisting) {
        this.acceptExisting = acceptExisting;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterPropertiesSet() throws CacheException {
        Configuration configuration;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Initializing EhCache CacheManager" + (this.cacheManagerName != null ? " '" + this.cacheManagerName + "'" : "")));
        }
        Configuration configuration2 = configuration = this.configLocation != null ? EhCacheManagerUtils.parseConfiguration(this.configLocation) : ConfigurationFactory.parseConfiguration();
        if (this.cacheManagerName != null) {
            configuration.setName(this.cacheManagerName);
        }
        if (this.shared) {
            this.cacheManager = CacheManager.create((Configuration)configuration);
        } else if (this.acceptExisting) {
            Class<CacheManager> clazz = CacheManager.class;
            synchronized (CacheManager.class) {
                this.cacheManager = CacheManager.getCacheManager((String)this.cacheManagerName);
                if (this.cacheManager == null) {
                    this.cacheManager = new CacheManager(configuration);
                } else {
                    this.locallyManaged = false;
                }
                // ** MonitorExit[var2_2] (shouldn't be in output)
            }
        } else {
            this.cacheManager = new CacheManager(configuration);
        }
    }

    @Nullable
    public CacheManager getObject() {
        return this.cacheManager;
    }

    public Class<? extends CacheManager> getObjectType() {
        return this.cacheManager != null ? this.cacheManager.getClass() : CacheManager.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        if (this.cacheManager != null && this.locallyManaged) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Shutting down EhCache CacheManager" + (this.cacheManagerName != null ? " '" + this.cacheManagerName + "'" : "")));
            }
            this.cacheManager.shutdown();
        }
    }
}

