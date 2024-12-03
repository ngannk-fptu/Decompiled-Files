/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.apache.lucene.util.InfoStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import org.apache.lucene.util.InfoStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class Slf4JInfoStream
extends InfoStream
implements InitializingBean,
DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(Slf4JInfoStream.class);
    private final LoadingCache<String, Logger> loggerRegistry;
    private InfoStream defaultInfoStream;

    public Slf4JInfoStream(final Map<String, String> componentToLoggerMapping) {
        this.loggerRegistry = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, Logger>(){

            public Logger load(String component) throws Exception {
                String loggerName = (String)componentToLoggerMapping.get(component);
                if (loggerName != null) {
                    Logger componentLogger = LoggerFactory.getLogger((String)loggerName);
                    if (logger.isDebugEnabled() && !componentLogger.isDebugEnabled()) {
                        logger.debug("Logger [{}] for Lucene component [{}] is disabled.", (Object)loggerName, (Object)component);
                    }
                    return componentLogger;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Using logger [{}] for unknown Lucene component [{}].", (Object)logger.getName(), (Object)component);
                }
                return logger;
            }
        });
    }

    public void message(String component, String message) {
        Logger logger = (Logger)this.loggerRegistry.getUnchecked((Object)component);
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public boolean isEnabled(String component) {
        Logger logger = (Logger)this.loggerRegistry.getUnchecked((Object)component);
        return logger.isDebugEnabled();
    }

    public void close() {
    }

    public void destroy() throws Exception {
        InfoStream.setDefault((InfoStream)this.defaultInfoStream);
    }

    public void afterPropertiesSet() throws Exception {
        this.defaultInfoStream = InfoStream.getDefault();
        InfoStream.setDefault((InfoStream)this);
    }
}

