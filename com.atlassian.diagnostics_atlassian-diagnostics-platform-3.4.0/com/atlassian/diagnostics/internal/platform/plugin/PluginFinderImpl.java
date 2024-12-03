/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.platform.plugin;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.diagnostics.internal.platform.plugin.BundleFinder;
import com.atlassian.diagnostics.internal.platform.plugin.ClassNameToPluginKeyStore;
import com.atlassian.diagnostics.internal.platform.plugin.PluginFinder;
import com.atlassian.diagnostics.internal.platform.plugin.PluginSystemMonitoringConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginFinderImpl
implements PluginFinder {
    private static final Logger log = LoggerFactory.getLogger(PluginFinderImpl.class);
    @VisibleForTesting
    static final String JAVA_CLASS_PREFIX = "java";
    private final ClassContextSecurityManager securityManger;
    private final BundleFinder bundleFinder;
    private final ClassNameToPluginKeyStore classNameToPluginKeyStore;
    private final Cache<Class<?>, String> classPluginSourceCache;
    private final int classContextTraversalLimit;
    private final int stackTraceTraversalLimit;

    public PluginFinderImpl(BundleFinder bundleFinder, ClassNameToPluginKeyStore classNameToPluginKeyStore, PluginSystemMonitoringConfig pluginSystemMonitoringConfig) {
        this(PluginFinderImpl.createClassContextSecurityManager(), bundleFinder, classNameToPluginKeyStore, CacheBuilder.newBuilder().maximumSize(10000L).weakValues().expireAfterAccess(Duration.ofHours(1L)).build(), pluginSystemMonitoringConfig.classContextTraversalLimit(), pluginSystemMonitoringConfig.stackTraceTraversalLimit());
    }

    @VisibleForTesting
    PluginFinderImpl(ClassContextSecurityManager securityManger, BundleFinder bundleFinder, ClassNameToPluginKeyStore classNameToPluginKeyStore, Cache<Class<?>, String> classPluginSourceCache, int classContextTraversalLimit, int stackTraceTraversalLimit) {
        this.securityManger = securityManger;
        this.bundleFinder = bundleFinder;
        this.classNameToPluginKeyStore = classNameToPluginKeyStore;
        this.classPluginSourceCache = classPluginSourceCache;
        this.classContextTraversalLimit = classContextTraversalLimit;
        this.stackTraceTraversalLimit = stackTraceTraversalLimit;
    }

    private static ClassContextSecurityManager createClassContextSecurityManager() {
        try {
            return new ClassContextSecurityManager();
        }
        catch (Exception exception) {
            log.debug("Failed to create security manager", (Throwable)exception);
            return null;
        }
    }

    @Override
    public Collection<String> getPluginNamesInCurrentCallStack() {
        try {
            if (this.securityManger != null) {
                return this.getPluginsFromClasses(this.securityManger.getClassContext());
            }
        }
        catch (Exception exception) {
            log.debug("Failed to get plugins list from call stack", (Throwable)exception);
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getPluginNamesFromStackTrace(@Nonnull StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace).map(StackTraceElement::getClassName).map(PluginFinderImpl::getClassNameWithoutLambda).map(this.classNameToPluginKeyStore::getPluginKey).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    @Override
    @Nullable
    public String getInvokingPluginKeyFromStackTrace(StackTraceElement[] stackTrace) {
        if (stackTrace == null) {
            return null;
        }
        int traversalDepth = Math.max(0, stackTrace.length - this.stackTraceTraversalLimit);
        for (int traceIndex = stackTrace.length - 1; traceIndex >= traversalDepth; --traceIndex) {
            if (stackTrace[traceIndex].getClassName().startsWith(JAVA_CLASS_PREFIX)) continue;
            return this.classNameToPluginKeyStore.getPluginKey(PluginFinderImpl.getClassNameWithoutLambda(stackTrace[traceIndex].getClassName())).orElse(null);
        }
        return null;
    }

    private static String getClassNameWithoutLambda(String clasName) {
        int positionOfLambda = clasName.indexOf("$");
        return positionOfLambda < 0 ? clasName : clasName.substring(0, positionOfLambda);
    }

    @Override
    @Nullable
    public String getInvokingPluginKeyFromClassContext(Class<?>[] classContext) {
        if (classContext == null) {
            return null;
        }
        int traversalDepth = Math.max(0, classContext.length - this.classContextTraversalLimit);
        for (int traceIndex = classContext.length - 1; traceIndex >= traversalDepth; --traceIndex) {
            Optional<String> pluginKey = this.bundleFinder.getBundleNameForClass(classContext[traceIndex]);
            if (!pluginKey.isPresent()) continue;
            return pluginKey.get();
        }
        return null;
    }

    private Collection<String> getPluginsFromClasses(Class<?>[] classes) {
        HashSet<String> plugins = new HashSet<String>();
        for (Class<?> clazz : classes) {
            String cachedPluginSource = (String)this.classPluginSourceCache.getIfPresent(clazz);
            if (cachedPluginSource == null) {
                this.resolvePlugin(clazz).ifPresent(pluginName -> this.add((Set<String>)plugins, (String)pluginName));
                continue;
            }
            if (!StringUtils.isNotEmpty((CharSequence)cachedPluginSource)) continue;
            this.add(plugins, cachedPluginSource);
        }
        return plugins;
    }

    private Optional<String> resolvePlugin(Class<?> clazz) {
        Optional<String> pluginName = this.bundleFinder.getBundleNameForClass(clazz);
        this.classPluginSourceCache.put(clazz, (Object)pluginName.orElse(""));
        return pluginName;
    }

    private void add(Set<String> plugins, String pluginName) {
        plugins.add(pluginName);
    }

    static class ClassContextSecurityManager
    extends SecurityManager {
        private static final Class<?>[] EMPTY_ARRAY = new Class[0];

        ClassContextSecurityManager() {
        }

        @Override
        protected Class<?>[] getClassContext() {
            Class<?>[] classContext = super.getClassContext();
            return classContext == null ? EMPTY_ARRAY : classContext;
        }
    }
}

