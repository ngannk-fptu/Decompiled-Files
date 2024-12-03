/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.core.Ordered
 *  org.springframework.lang.Nullable
 *  org.springframework.web.context.support.GenericWebApplicationContext
 */
package org.springframework.web.servlet.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;

@Deprecated
public class ResourceBundleViewResolver
extends AbstractCachingViewResolver
implements Ordered,
InitializingBean,
DisposableBean {
    public static final String DEFAULT_BASENAME = "views";
    private String[] basenames = new String[]{"views"};
    private ClassLoader bundleClassLoader = Thread.currentThread().getContextClassLoader();
    @Nullable
    private String defaultParentView;
    @Nullable
    private Locale[] localesToInitialize;
    private int order = Integer.MAX_VALUE;
    private final Map<Locale, BeanFactory> localeCache = new HashMap<Locale, BeanFactory>();
    private final Map<List<ResourceBundle>, ConfigurableApplicationContext> bundleCache = new HashMap<List<ResourceBundle>, ConfigurableApplicationContext>();

    public void setBasename(String basename) {
        this.setBasenames(basename);
    }

    public void setBasenames(String ... basenames) {
        this.basenames = basenames;
    }

    public void setBundleClassLoader(ClassLoader classLoader) {
        this.bundleClassLoader = classLoader;
    }

    protected ClassLoader getBundleClassLoader() {
        return this.bundleClassLoader;
    }

    public void setDefaultParentView(String defaultParentView) {
        this.defaultParentView = defaultParentView;
    }

    public void setLocalesToInitialize(Locale ... localesToInitialize) {
        this.localesToInitialize = localesToInitialize;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public void afterPropertiesSet() throws BeansException {
        if (this.localesToInitialize != null) {
            for (Locale locale : this.localesToInitialize) {
                this.initFactory(locale);
            }
        }
    }

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        BeanFactory factory = this.initFactory(locale);
        try {
            return (View)factory.getBean(viewName, View.class);
        }
        catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    protected synchronized BeanFactory initFactory(Locale locale) throws BeansException {
        BeanFactory cachedFactory;
        BeanFactory cachedFactory2;
        if (this.isCache() && (cachedFactory2 = this.localeCache.get(locale)) != null) {
            return cachedFactory2;
        }
        ArrayList<ResourceBundle> bundles = new ArrayList<ResourceBundle>(this.basenames.length);
        for (String basename : this.basenames) {
            bundles.add(this.getBundle(basename, locale));
        }
        if (this.isCache() && (cachedFactory = (BeanFactory)this.bundleCache.get(bundles)) != null) {
            this.localeCache.put(locale, cachedFactory);
            return cachedFactory;
        }
        GenericWebApplicationContext factory = new GenericWebApplicationContext();
        factory.setParent(this.getApplicationContext());
        factory.setServletContext(this.getServletContext());
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader((BeanDefinitionRegistry)factory);
        reader.setDefaultParentBean(this.defaultParentView);
        for (ResourceBundle bundle : bundles) {
            reader.registerBeanDefinitions(bundle);
        }
        factory.refresh();
        if (this.isCache()) {
            this.localeCache.put(locale, (BeanFactory)factory);
            this.bundleCache.put(bundles, (ConfigurableApplicationContext)factory);
        }
        return factory;
    }

    protected ResourceBundle getBundle(String basename, Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle(basename, locale, this.getBundleClassLoader());
    }

    public void destroy() throws BeansException {
        for (ConfigurableApplicationContext factory : this.bundleCache.values()) {
            factory.close();
        }
        this.localeCache.clear();
        this.bundleCache.clear();
    }
}

