/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpSessionAttributeListener
 *  javax.servlet.http.HttpSessionBindingEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.http;

import com.atlassian.confluence.util.logging.LoggingContext;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class ConfluenceAttributeListener
implements HttpSessionAttributeListener {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAttributeListener.class);
    private static final boolean LISTENER_ENABLED = Boolean.getBoolean("confluence.attribute.listener.enabled");
    private Set<String> nameCache = Collections.newSetFromMap(CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(60L, TimeUnit.MINUTES).build().asMap());
    private final Set<String> classNameCache = Collections.newSetFromMap(CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(60L, TimeUnit.MINUTES).build().asMap());

    public void attributeAdded(HttpSessionBindingEvent event) {
        if (!LISTENER_ENABLED) {
            return;
        }
        if (this.nameCache.add(event.getName())) {
            LoggingContext.executeWithContext((String)"attribute_name", (Object)event.getName(), () -> log.info("New attribute name."));
        }
        this.checkAttributeClass(event);
    }

    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
        this.checkAttributeClass(event);
    }

    private void checkAttributeClass(HttpSessionBindingEvent event) {
        String clazzName;
        if (LISTENER_ENABLED && event.getValue() != null && this.classNameCache.add(clazzName = event.getValue().getClass().getName())) {
            LoggingContext.executeWithContext((Map)ImmutableMap.of((Object)"attribute_name", (Object)event.getName(), (Object)"attribute_class", (Object)clazzName), () -> log.info("New attribute class."));
        }
    }
}

