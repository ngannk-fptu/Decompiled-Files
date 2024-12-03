/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import java.io.Serializable;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.StandardMBean;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;

public final class Store
implements Serializable,
DynamicMBean {
    private static final long serialVersionUID = 3477287016924524437L;
    private final ObjectName objectName;
    private final DynamicMBean storeBean;

    private Store(Ehcache ehcache, Object storeBean) throws NotCompliantMBeanException {
        this.objectName = Store.createObjectName(ehcache.getCacheManager().getName(), ehcache.getName());
        this.storeBean = storeBean instanceof DynamicMBean ? (DynamicMBean)storeBean : new StandardMBean(storeBean, null);
    }

    static Store getBean(Ehcache cache) throws NotCompliantMBeanException {
        Object bean;
        if (cache instanceof Cache && (bean = ((Cache)cache).getStoreMBean()) != null) {
            return new Store(cache, bean);
        }
        return null;
    }

    static ObjectName createObjectName(String cacheManagerName, String cacheName) {
        try {
            return new ObjectName("net.sf.ehcache:type=Store,CacheManager=" + cacheManagerName + ",name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheName));
        }
        catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
    }

    public ObjectName getObjectName() {
        return this.objectName;
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return this.storeBean.getAttribute(attribute);
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        this.storeBean.setAttribute(attribute);
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return this.storeBean.getAttributes(attributes);
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return this.storeBean.setAttributes(attributes);
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return this.storeBean.invoke(actionName, params, signature);
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.storeBean.getMBeanInfo();
    }
}

