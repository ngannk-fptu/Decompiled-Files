/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.soy.renderer.SoyException
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.io.Closeables
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  com.google.inject.Injector
 *  com.google.template.soy.SoyFileSet
 *  com.google.template.soy.SoyFileSet$Builder
 *  com.google.template.soy.jssrc.SoyJsSrcOptions
 *  com.google.template.soy.shared.SoyAstCache
 *  com.google.template.soy.tofu.SoyTofu
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.soy.impl;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.soy.impl.DevMode;
import com.atlassian.soy.impl.SoyDependencyInjectorFactory;
import com.atlassian.soy.impl.SoyManager;
import com.atlassian.soy.impl.data.JavaBeanAccessorResolver;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.spi.TemplateSetFactory;
import com.atlassian.soy.spi.modules.GuiceModuleSupplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Injector;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.shared.SoyAstCache;
import com.google.template.soy.tofu.SoyTofu;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSoyManager
implements SoyManager {
    private static final SoyTofu DIDNOTCOMPILE = (SoyTofu)Proxy.newProxyInstance(DefaultSoyManager.class.getClassLoader(), new Class[]{SoyTofu.class}, (InvocationHandler)new NullTofuProxy());
    private static final Logger log = LoggerFactory.getLogger(DefaultSoyManager.class);
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Compiled SOY templates, same for all tenants.")
    private final ResettableLazyReference<SoyAstCache> soyAstCache;
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Compiled SOY templates, same for all tenants.")
    private final LoadingCache<String, SoyTofu> soyTofuCache;
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Last modified time for templates, same for all tenants.")
    private final LoadingCache<String, Long> lastModifiedCache;
    private final JavaBeanAccessorResolver javaBeanAccessorResolver;
    private final SoyDependencyInjectorFactory soyDependencyInjectorFactory;
    private final TemplateSetFactory templateSetFactory;

    public DefaultSoyManager(GuiceModuleSupplier moduleSupplier, JavaBeanAccessorResolver javaBeanAccessorResolver, TemplateSetFactory templateSetFactory) {
        this.javaBeanAccessorResolver = javaBeanAccessorResolver;
        this.templateSetFactory = templateSetFactory;
        this.soyAstCache = new ResettableLazyReference<SoyAstCache>(){

            protected SoyAstCache create() throws Exception {
                return new SoyAstCache();
            }
        };
        this.soyTofuCache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, SoyTofu>(){

            public SoyTofu load(@Nonnull String key) throws SoyException {
                SoyTofu soyTofu = DefaultSoyManager.this.strainTofu(key);
                return soyTofu == null ? DIDNOTCOMPILE : soyTofu;
            }
        });
        this.lastModifiedCache = CacheBuilder.newBuilder().build((CacheLoader)new CacheLoader<String, Long>(){

            public Long load(@Nonnull String key) {
                return DefaultSoyManager.this.getLastModifiedForModule(key);
            }
        });
        this.soyDependencyInjectorFactory = new SoyDependencyInjectorFactory(moduleSupplier);
    }

    @Override
    public String compile(CharSequence content, String filePath) {
        SoyJsSrcOptions options;
        SoyFileSet soyFiles = this.makeSoyFileSetBuilder().setSupportContentSecurityPolicy(true).add(content, filePath).build();
        List output = soyFiles.compileToJsSrc(options = DefaultSoyManager.newOptions(), null);
        if (output.size() != 1) {
            throw new IllegalStateException("Did not manage to compile soy template at:" + filePath + ", size=" + output.size());
        }
        return (String)output.get(0);
    }

    @Override
    public void render(Appendable appendable, String completeModuleKey, String templateName, Map<String, Object> data, Map<String, Object> injectedData) throws SoyException {
        if (DevMode.isDevMode()) {
            log.debug("Clearing caches in dev mode");
            this.clearCaches(completeModuleKey);
        }
        try {
            SoyTofu tofu = (SoyTofu)this.soyTofuCache.getUnchecked((Object)completeModuleKey);
            if (tofu == DIDNOTCOMPILE) {
                throw new SoyException("Unable to compile Soy template in plugin module: " + completeModuleKey);
            }
            if (DevMode.isDevMode()) {
                this.lastModifiedCache.getUnchecked((Object)completeModuleKey);
            }
            tofu.newRenderer(templateName).setData(data).setIjData(injectedData).render(appendable);
        }
        catch (UncheckedExecutionException e) {
            throw new SoyException("Unable to compile Soy templates at: " + completeModuleKey, e.getCause());
        }
    }

    @Override
    public void clearCaches(String completeModuleKey) {
        this.soyAstCache.reset();
        this.soyDependencyInjectorFactory.clear();
        this.templateSetFactory.clear();
        this.javaBeanAccessorResolver.clearCaches();
        if (completeModuleKey == null) {
            this.soyTofuCache.invalidateAll();
        } else if (this.isModified(completeModuleKey)) {
            this.soyTofuCache.invalidate((Object)completeModuleKey);
            this.lastModifiedCache.invalidate((Object)completeModuleKey);
        }
    }

    private long getLastModifiedForModule(String completeModuleKey) {
        long lastModified = 0L;
        for (URL url : this.templateSetFactory.get(completeModuleKey)) {
            lastModified = Math.max(lastModified, DefaultSoyManager.getLastModified(url));
        }
        return lastModified;
    }

    private boolean isModified(String completeModuleKey) {
        try {
            Long previousModifiedDate = (Long)this.lastModifiedCache.getUnchecked((Object)completeModuleKey);
            long currentModifiedDate = this.getLastModifiedForModule(completeModuleKey);
            return previousModifiedDate < currentModifiedDate || currentModifiedDate == -1L;
        }
        catch (UncheckedExecutionException e) {
            log.debug("Unable to check resolve the module key '{}'. Treating as modified", (Object)completeModuleKey, (Object)e);
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static long getLastModified(URL url) {
        long l;
        URLConnection urlConnection = url.openConnection();
        try {
            l = urlConnection.getLastModified();
        }
        catch (Throwable throwable) {
            try {
                Closeables.closeQuietly((InputStream)urlConnection.getInputStream());
                throw throwable;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Closeables.closeQuietly((InputStream)urlConnection.getInputStream());
        return l;
    }

    private SoyTofu strainTofu(String completeModuleKey) throws SoyException {
        SoyFileSet.Builder builder = this.makeSoyFileSetBuilder().setSupportContentSecurityPolicy(true).setSoyAstCache((SoyAstCache)this.soyAstCache.get());
        this.templateSetFactory.get(completeModuleKey).forEach(arg_0 -> ((SoyFileSet.Builder)builder).add(arg_0));
        return builder.build().compileToTofu();
    }

    private SoyFileSet.Builder makeSoyFileSetBuilder() {
        Injector injector = this.soyDependencyInjectorFactory.get();
        return (SoyFileSet.Builder)injector.getInstance(SoyFileSet.Builder.class);
    }

    private static SoyJsSrcOptions newOptions() {
        SoyJsSrcOptions options = new SoyJsSrcOptions();
        options.setShouldGenerateJsdoc(false);
        return options;
    }

    private static class NullTofuProxy
    implements InvocationHandler {
        private NullTofuProxy() {
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke((Object)this, args);
            }
            throw new UnsupportedOperationException();
        }
    }
}

