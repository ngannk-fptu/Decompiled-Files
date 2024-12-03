/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.util.concurrent.ResettableLazyReference
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.shortcuts.internal;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcut;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutManager;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutModuleDescriptor;
import com.atlassian.plugins.shortcuts.internal.Hasher;
import com.atlassian.plugins.shortcuts.internal.ShortcutsResettableLazyReference;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.util.concurrent.ResettableLazyReference;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultKeyboardShortcutManager
implements KeyboardShortcutManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultKeyboardShortcutManager.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ResettableLazyReference<List<KeyboardShortcut>> ref;
    private String applicationContextPath = "";
    private final HttpContext httpContext;
    private final ResettableLazyReference<String> hashRef = new ResettableLazyReference<String>(){

        protected String create() throws Exception {
            List<KeyboardShortcut> shortcuts = DefaultKeyboardShortcutManager.this.getAllShortcuts();
            return Hasher.getHash(shortcuts);
        }
    };

    public DefaultKeyboardShortcutManager(PluginAccessor pluginAccessor, PluginEventManager pluginEventManager, HttpContext httpContext) {
        this.ref = new ShortcutsResettableLazyReference(pluginAccessor, httpContext);
        this.httpContext = httpContext;
        pluginEventManager.register((Object)this);
    }

    @PluginEventListener
    public void handleEvent(Object event) {
        ModuleDescriptor moduleDescriptor;
        if (event instanceof PluginModuleDisabledEvent) {
            moduleDescriptor = ((PluginModuleDisabledEvent)event).getModule();
        } else if (event instanceof PluginModuleEnabledEvent) {
            moduleDescriptor = ((PluginModuleEnabledEvent)event).getModule();
        } else {
            return;
        }
        if (!(moduleDescriptor instanceof KeyboardShortcutModuleDescriptor)) {
            return;
        }
        log.info("KeyboardShortcutModuleDescriptor plugin module event detected - resetting references");
        this.lock.writeLock().lock();
        try {
            this.ref.reset();
            this.hashRef.reset();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public String getShortcutsHash() {
        this.lock.readLock().lock();
        try {
            String string = (String)this.hashRef.get();
            return string;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public List<KeyboardShortcut> getAllShortcuts() {
        this.lock.readLock().lock();
        HttpServletRequest request = this.httpContext.getRequest();
        if (request != null && !request.getContextPath().equals(this.applicationContextPath)) {
            this.ref.reset();
            this.hashRef.reset();
            this.applicationContextPath = request.getContextPath();
        }
        try {
            List list = (List)this.ref.get();
            return list;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
}

