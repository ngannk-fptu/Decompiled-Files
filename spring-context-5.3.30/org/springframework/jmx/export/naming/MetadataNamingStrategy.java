/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jmx.export.naming;

import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.metadata.ManagedResource;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class MetadataNamingStrategy
implements ObjectNamingStrategy,
InitializingBean {
    @Nullable
    private JmxAttributeSource attributeSource;
    @Nullable
    private String defaultDomain;

    public MetadataNamingStrategy() {
    }

    public MetadataNamingStrategy(JmxAttributeSource attributeSource) {
        Assert.notNull((Object)attributeSource, (String)"JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    public void setAttributeSource(JmxAttributeSource attributeSource) {
        Assert.notNull((Object)attributeSource, (String)"JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    public void setDefaultDomain(String defaultDomain) {
        this.defaultDomain = defaultDomain;
    }

    public void afterPropertiesSet() {
        if (this.attributeSource == null) {
            throw new IllegalArgumentException("Property 'attributeSource' is required");
        }
    }

    @Override
    public ObjectName getObjectName(Object managedBean, @Nullable String beanKey) throws MalformedObjectNameException {
        Assert.state((this.attributeSource != null ? 1 : 0) != 0, (String)"No JmxAttributeSource set");
        Class managedClass = AopUtils.getTargetClass((Object)managedBean);
        ManagedResource mr = this.attributeSource.getManagedResource(managedClass);
        if (mr != null && StringUtils.hasText((String)mr.getObjectName())) {
            return ObjectNameManager.getInstance(mr.getObjectName());
        }
        Assert.state((beanKey != null ? 1 : 0) != 0, (String)"No ManagedResource attribute and no bean key specified");
        try {
            return ObjectNameManager.getInstance(beanKey);
        }
        catch (MalformedObjectNameException ex) {
            String domain = this.defaultDomain;
            if (domain == null) {
                domain = ClassUtils.getPackageName((Class)managedClass);
            }
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("type", ClassUtils.getShortName((Class)managedClass));
            properties.put("name", beanKey);
            return ObjectNameManager.getInstance(domain, properties);
        }
    }
}

