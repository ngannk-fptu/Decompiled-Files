/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.service.importer.DefaultOsgiServiceDependency
 *  org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency
 *  org.eclipse.gemini.blueprint.service.importer.support.Availability
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean
 *  org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean
 *  org.eclipse.gemini.blueprint.util.OsgiFilterUtils
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.BundleException
 *  org.osgi.framework.Filter
 *  org.osgi.framework.InvalidSyntaxException
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.PropertyValue
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.SmartFactoryBean
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.TypedStringValue
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.dependencies.startup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.OsgiServiceDependencyFactory;
import org.eclipse.gemini.blueprint.service.importer.DefaultOsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceDependency;
import org.eclipse.gemini.blueprint.service.importer.support.Availability;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean;
import org.eclipse.gemini.blueprint.util.OsgiFilterUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class MandatoryImporterDependencyFactory
implements OsgiServiceDependencyFactory {
    private static final Log log = LogFactory.getLog(MandatoryImporterDependencyFactory.class);
    private static final String AVAILABILITY_PROP = "availability";
    private static final String INTERFACES_PROP = "interfaces";
    private static final String SERVICE_BEAN_NAME_PROP = "serviceBeanName";
    private static final String FILTER_PROP = "filter";

    @Override
    public Collection<OsgiServiceDependency> getServiceDependencies(BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory) throws BeansException, InvalidSyntaxException, BundleException {
        boolean trace = log.isTraceEnabled();
        Object[] singleBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)beanFactory, OsgiServiceProxyFactoryBean.class, (boolean)true, (boolean)false);
        if (trace) {
            log.trace((Object)("Discovered single proxy importers " + Arrays.toString(singleBeans)));
        }
        Object[] collectionBeans = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)beanFactory, OsgiServiceCollectionProxyFactoryBean.class, (boolean)true, (boolean)false);
        if (trace) {
            log.trace((Object)("Discovered collection proxy importers " + Arrays.toString(collectionBeans)));
        }
        String[] beans = StringUtils.concatenateStringArrays((String[])singleBeans, (String[])collectionBeans);
        ArrayList<OsgiServiceDependency> beansCollections = new ArrayList<OsgiServiceDependency>(beans.length);
        for (int i = 0; i < beans.length; ++i) {
            String name;
            if (!this.isLazy(beanFactory, beans[i])) {
                DefaultOsgiServiceDependency dependency;
                OsgiServiceProxyFactoryBean importer;
                String beanName = beans[i].startsWith("&") ? beans[i] : "&" + beans[i];
                SmartFactoryBean reference = (SmartFactoryBean)beanFactory.getBean(beanName, SmartFactoryBean.class);
                if (reference instanceof OsgiServiceProxyFactoryBean) {
                    importer = (OsgiServiceProxyFactoryBean)reference;
                    dependency = new DefaultOsgiServiceDependency(beanName, importer.getUnifiedFilter(), Availability.MANDATORY.equals((Object)importer.getAvailability()));
                } else {
                    importer = (OsgiServiceCollectionProxyFactoryBean)reference;
                    dependency = new DefaultOsgiServiceDependency(beanName, importer.getUnifiedFilter(), Availability.MANDATORY.equals((Object)importer.getAvailability()));
                }
                if (trace) {
                    log.trace((Object)("Eager importer " + beanName + " implies dependecy " + dependency));
                }
                beansCollections.add((OsgiServiceDependency)dependency);
                continue;
            }
            String string = name = beans[i].startsWith("&") ? beans[i].substring(1) : beans[i];
            if (beanFactory.containsBeanDefinition(name)) {
                BeanDefinition def = beanFactory.getBeanDefinition(name);
                MutablePropertyValues values = def.getPropertyValues();
                PropertyValue value = values.getPropertyValue(AVAILABILITY_PROP);
                if (value == null || !Availability.MANDATORY.equals(value.getValue())) continue;
                String[] intfs = this.getInterfaces(values.getPropertyValue(INTERFACES_PROP));
                String beanName = this.getString(values.getPropertyValue(SERVICE_BEAN_NAME_PROP));
                String filterProp = this.getString(values.getPropertyValue(FILTER_PROP));
                Filter filter = this.createFilter(intfs, beanName, filterProp);
                DefaultOsgiServiceDependency dependency = new DefaultOsgiServiceDependency(name, filter, true);
                if (trace) {
                    log.trace((Object)("Lazy importer " + beanName + " implies dependecy " + dependency));
                }
                beansCollections.add((OsgiServiceDependency)dependency);
                continue;
            }
            if (!trace) continue;
            log.trace((Object)("Bean " + name + " is marked as lazy but does not provide a bean definition; ignoring..."));
        }
        return beansCollections;
    }

    private Filter createFilter(String[] intfs, String serviceBeanName, String filter) {
        String nameFilter;
        String filterWithClasses;
        String string = filterWithClasses = !ObjectUtils.isEmpty((Object[])intfs) ? OsgiFilterUtils.unifyFilter((String[])intfs, (String)filter) : filter;
        if (StringUtils.hasText((String)serviceBeanName)) {
            StringBuilder nsFilter = new StringBuilder("(|(");
            nsFilter.append("org.eclipse.gemini.blueprint.bean.name");
            nsFilter.append("=");
            nsFilter.append(serviceBeanName);
            nsFilter.append(")(");
            nsFilter.append("org.springframework.osgi.bean.name");
            nsFilter.append("=");
            nsFilter.append(serviceBeanName);
            nsFilter.append(")(");
            nsFilter.append("osgi.service.blueprint.compname");
            nsFilter.append("=");
            nsFilter.append(serviceBeanName);
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
        return OsgiFilterUtils.createFilter((String)filterWithServiceBeanName);
    }

    private String getString(PropertyValue pv) {
        if (pv == null) {
            return "";
        }
        Object value = pv.getValue();
        if (value == null) {
            return "";
        }
        if (value instanceof TypedStringValue) {
            return ((TypedStringValue)value).getValue();
        }
        return value.toString();
    }

    private String[] getInterfaces(PropertyValue pv) {
        if (pv == null) {
            return new String[0];
        }
        Object value = pv.getValue();
        if (value instanceof Collection) {
            Collection collection = (Collection)value;
            String[] strs = new String[collection.size()];
            int index = 0;
            for (Object obj : collection) {
                strs[index] = value instanceof TypedStringValue ? ((TypedStringValue)value).getValue() : value.toString();
                ++index;
            }
            return strs;
        }
        return new String[]{value.toString()};
    }

    private boolean isLazy(ConfigurableListableBeanFactory beanFactory, String beanName) {
        String name;
        String string = name = beanName.startsWith("&") ? beanName.substring(1) : beanName;
        if (beanFactory.containsBeanDefinition(name)) {
            return beanFactory.getBeanDefinition(name).isLazyInit();
        }
        return false;
    }
}

