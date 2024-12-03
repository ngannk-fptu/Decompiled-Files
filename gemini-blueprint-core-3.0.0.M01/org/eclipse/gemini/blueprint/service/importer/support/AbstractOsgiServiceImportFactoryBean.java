/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.Filter
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.eclipse.gemini.blueprint.service.importer.support.ImportContextClassLoaderEnum;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractOsgiServiceImportFactoryBean
implements FactoryBean<Object>,
InitializingBean,
DisposableBean,
BundleContextAware,
BeanClassLoaderAware,
BeanNameAware {
    private static final Log log = LogFactory.getLog(AbstractOsgiServiceImportFactoryBean.class);
    private ClassLoader classLoader;
    private BundleContext bundleContext;
    private ImportContextClassLoaderEnum contextClassLoader = ImportContextClassLoaderEnum.CLIENT;
    private Class<?>[] interfaces;
    private String filter;
    private Filter unifiedFilter;
    private OsgiServiceLifecycleListener[] listeners;
    private String serviceBeanName;
    private Availability availability = Availability.MANDATORY;
    private String beanName = "";

    public void afterPropertiesSet() {
        Assert.notNull((Object)this.bundleContext, (String)"Required 'bundleContext' property was not set.");
        Assert.notNull((Object)this.classLoader, (String)"Required 'classLoader' property was not set.");
        Assert.isTrue((!ObjectUtils.isEmpty((Object[])this.interfaces) || StringUtils.hasText((String)this.filter) || StringUtils.hasText((String)this.serviceBeanName) ? 1 : 0) != 0, (String)"At least the interface or filter or service name needs to be defined to import an OSGi service");
        if (ObjectUtils.isEmpty((Object[])this.interfaces)) {
            log.warn((Object)("OSGi importer [" + this.beanName + "] definition contains no interfaces: all invocations will be executed on the proxy and not on the backing service"));
        }
        Assert.isTrue((!ClassUtils.containsUnrelatedClasses(this.interfaces) ? 1 : 0) != 0, (String)"More then one concrete class specified; cannot create proxy.");
        this.listeners = this.listeners == null ? new OsgiServiceLifecycleListener[]{} : this.listeners;
        this.interfaces = this.interfaces == null ? new Class[]{} : this.interfaces;
        this.filter = StringUtils.hasText((String)this.filter) ? this.filter : "";
        this.getUnifiedFilter();
    }

    public Filter getUnifiedFilter() {
        String nameFilter;
        if (this.unifiedFilter != null) {
            return this.unifiedFilter;
        }
        String filterWithClasses = !ObjectUtils.isEmpty((Object[])this.interfaces) ? OsgiFilterUtils.unifyFilter(this.interfaces, this.filter) : this.filter;
        boolean trace = log.isTraceEnabled();
        if (trace) {
            log.trace((Object)("Unified classes=" + ObjectUtils.nullSafeToString((Object[])this.interfaces) + " and filter=[" + this.filter + "]  in=[" + filterWithClasses + "]"));
        }
        if (StringUtils.hasText((String)this.serviceBeanName)) {
            StringBuilder nsFilter = new StringBuilder("(|(");
            nsFilter.append("org.eclipse.gemini.blueprint.bean.name");
            nsFilter.append("=");
            nsFilter.append(this.serviceBeanName);
            nsFilter.append(")(");
            nsFilter.append("org.springframework.osgi.bean.name");
            nsFilter.append("=");
            nsFilter.append(this.serviceBeanName);
            nsFilter.append(")(");
            nsFilter.append("osgi.service.blueprint.compname");
            nsFilter.append("=");
            nsFilter.append(this.serviceBeanName);
            nsFilter.append("))");
            nameFilter = nsFilter.toString();
        } else {
            nameFilter = null;
        }
        String filterWithServiceBeanName = filterWithClasses;
        if (nameFilter != null) {
            StringBuilder finalFilter = new StringBuilder();
            finalFilter.append("(&");
            finalFilter.append(filterWithClasses);
            finalFilter.append(nameFilter);
            finalFilter.append(")");
            filterWithServiceBeanName = finalFilter.toString();
        }
        if (trace) {
            log.trace((Object)("Unified serviceBeanName [" + ObjectUtils.nullSafeToString((Object)this.serviceBeanName) + "] and filter=[" + filterWithClasses + "]  in=[" + filterWithServiceBeanName + "]"));
        }
        this.unifiedFilter = OsgiFilterUtils.createFilter(filterWithServiceBeanName);
        return this.unifiedFilter;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    public void setImportContextClassLoader(ImportContextClassLoaderEnum contextClassLoader) {
        Assert.notNull((Object)((Object)contextClassLoader));
        this.contextClassLoader = contextClassLoader;
    }

    @Override
    public void setBundleContext(BundleContext context) {
        this.bundleContext = context;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setListeners(OsgiServiceLifecycleListener[] listeners) {
        this.listeners = listeners;
    }

    public void setServiceBeanName(String serviceBeanName) {
        this.serviceBeanName = serviceBeanName;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return this.classLoader;
    }

    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    public Class<?>[] getInterfaces() {
        return this.interfaces;
    }

    public String getFilter() {
        return this.filter;
    }

    public OsgiServiceLifecycleListener[] getListeners() {
        return this.listeners;
    }

    public ImportContextClassLoaderEnum getImportContextClassLoader() {
        return this.contextClassLoader;
    }

    public Availability getAvailability() {
        return this.availability;
    }

    public void setAvailability(Availability availability) {
        Assert.notNull((Object)((Object)availability));
        this.availability = availability;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }
}

