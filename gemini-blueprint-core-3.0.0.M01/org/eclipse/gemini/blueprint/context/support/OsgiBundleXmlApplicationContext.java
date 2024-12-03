/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver
 *  org.springframework.beans.factory.xml.DelegatingEntityResolver
 *  org.springframework.beans.factory.xml.DocumentLoader
 *  org.springframework.beans.factory.xml.NamespaceHandlerResolver
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.context.ApplicationContext
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.util.Assert
 */
package org.eclipse.gemini.blueprint.context.support;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.gemini.blueprint.context.support.AbstractDelegatedExecutionApplicationContext;
import org.eclipse.gemini.blueprint.context.support.BlueprintDocumentLoader;
import org.eclipse.gemini.blueprint.context.support.ChainedEntityResolver;
import org.eclipse.gemini.blueprint.context.support.DelegatedNamespaceHandlerResolver;
import org.eclipse.gemini.blueprint.context.support.TrackingUtil;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.eclipse.gemini.blueprint.util.internal.BundleUtils;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.xml.sax.EntityResolver;

public class OsgiBundleXmlApplicationContext
extends AbstractDelegatedExecutionApplicationContext
implements DisposableBean {
    public static final String DEFAULT_CONFIG_LOCATION = "osgibundle:/META-INF/spring/*.xml";

    public OsgiBundleXmlApplicationContext() {
        this((String[])null);
    }

    public OsgiBundleXmlApplicationContext(ApplicationContext parent) {
        this(null, parent);
    }

    public OsgiBundleXmlApplicationContext(String[] configLocations) {
        this(configLocations, null);
    }

    public OsgiBundleXmlApplicationContext(String[] configLocations, ApplicationContext parent) {
        super(parent);
        this.setConfigLocations(configLocations);
    }

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)beanFactory);
        beanDefinitionReader.setResourceLoader((ResourceLoader)this);
        beanDefinitionReader.setDocumentLoader((DocumentLoader)new BlueprintDocumentLoader());
        final Object[] resolvers = new Object[2];
        final BundleContext ctx = this.getBundleContext();
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    String filter = BundleUtils.createNamespaceFilter(ctx);
                    resolvers[0] = OsgiBundleXmlApplicationContext.this.createNamespaceHandlerResolver(ctx, filter, OsgiBundleXmlApplicationContext.this.getClassLoader());
                    resolvers[1] = OsgiBundleXmlApplicationContext.this.createEntityResolver(ctx, filter, OsgiBundleXmlApplicationContext.this.getClassLoader());
                    return null;
                }
            });
        } else {
            String filter = BundleUtils.createNamespaceFilter(ctx);
            resolvers[0] = this.createNamespaceHandlerResolver(ctx, filter, this.getClassLoader());
            resolvers[1] = this.createEntityResolver(ctx, filter, this.getClassLoader());
        }
        beanDefinitionReader.setNamespaceHandlerResolver((NamespaceHandlerResolver)resolvers[0]);
        beanDefinitionReader.setEntityResolver((EntityResolver)resolvers[1]);
        this.initBeanDefinitionReader(beanDefinitionReader);
        this.loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        String[] configLocations = this.expandLocations(this.getConfigLocations());
        if (configLocations != null) {
            for (int i = 0; i < configLocations.length; ++i) {
                reader.loadBeanDefinitions(configLocations[i]);
            }
        }
    }

    private String[] expandLocations(String[] configLocations) {
        String[] expanded = null;
        if (configLocations != null) {
            expanded = new String[configLocations.length];
            for (int i = 0; i < configLocations.length; ++i) {
                String location = configLocations[i];
                if (location.endsWith("/")) {
                    location = location + "*.xml";
                }
                expanded[i] = location;
            }
        }
        return expanded;
    }

    @Override
    protected String[] getDefaultConfigLocations() {
        return new String[]{DEFAULT_CONFIG_LOCATION};
    }

    private NamespaceHandlerResolver createNamespaceHandlerResolver(BundleContext bundleContext, String filter, ClassLoader bundleClassLoader) {
        Assert.notNull((Object)bundleContext, (String)"bundleContext is required");
        DefaultNamespaceHandlerResolver localNamespaceResolver = new DefaultNamespaceHandlerResolver(bundleClassLoader);
        NamespaceHandlerResolver osgiServiceNamespaceResolver = this.lookupNamespaceHandlerResolver(bundleContext, filter, localNamespaceResolver);
        DelegatedNamespaceHandlerResolver delegate = new DelegatedNamespaceHandlerResolver();
        delegate.addNamespaceHandler((NamespaceHandlerResolver)localNamespaceResolver, "LocalNamespaceResolver for bundle " + OsgiStringUtils.nullSafeNameAndSymName(bundleContext.getBundle()));
        delegate.addNamespaceHandler(osgiServiceNamespaceResolver, "OSGi Service resolver");
        return delegate;
    }

    private EntityResolver createEntityResolver(BundleContext bundleContext, String filter, ClassLoader bundleClassLoader) {
        Assert.notNull((Object)bundleContext, (String)"bundleContext is required");
        DelegatingEntityResolver localEntityResolver = new DelegatingEntityResolver(bundleClassLoader);
        EntityResolver osgiServiceEntityResolver = this.lookupEntityResolver(bundleContext, filter, localEntityResolver);
        ChainedEntityResolver delegate = new ChainedEntityResolver();
        delegate.addEntityResolver((EntityResolver)localEntityResolver, "LocalEntityResolver for bundle " + OsgiStringUtils.nullSafeNameAndSymName(bundleContext.getBundle()));
        delegate.addEntityResolver(osgiServiceEntityResolver, "OSGi Service resolver");
        return delegate;
    }

    private NamespaceHandlerResolver lookupNamespaceHandlerResolver(BundleContext bundleContext, String filter, Object fallbackObject) {
        return (NamespaceHandlerResolver)TrackingUtil.getService(new Class[]{NamespaceHandlerResolver.class}, filter, NamespaceHandlerResolver.class.getClassLoader(), bundleContext, fallbackObject);
    }

    private EntityResolver lookupEntityResolver(BundleContext bundleContext, String filter, Object fallbackObject) {
        return (EntityResolver)TrackingUtil.getService(new Class[]{EntityResolver.class}, filter, EntityResolver.class.getClassLoader(), bundleContext, fallbackObject);
    }

    @Override
    public String[] getConfigLocations() {
        return super.getConfigLocations();
    }
}

