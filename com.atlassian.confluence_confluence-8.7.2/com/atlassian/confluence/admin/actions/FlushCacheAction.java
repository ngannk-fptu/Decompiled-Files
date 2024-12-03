/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class FlushCacheAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(FlushCacheAction.class);
    private boolean showDistributions;
    private String cache;
    private CacheManager cacheManager;

    public void setCache(String cacheName) {
        this.cache = cacheName;
    }

    public String execute() {
        if (StringUtils.isNotEmpty((CharSequence)this.cache)) {
            try {
                this.cacheManager.getCache(this.cache).removeAll();
            }
            catch (Exception e) {
                log.error("Unable to flush cache", (Throwable)e);
            }
        } else {
            this.cacheManager.flushCaches();
        }
        return "success";
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public boolean isShowDistributions() {
        return this.showDistributions;
    }

    public void setShowDistributions(boolean showDistributions) {
        this.showDistributions = showDistributions;
    }
}

