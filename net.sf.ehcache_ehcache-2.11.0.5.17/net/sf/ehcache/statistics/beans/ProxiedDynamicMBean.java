/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import net.sf.ehcache.statistics.beans.AttributeProxy;

public class ProxiedDynamicMBean
implements DynamicMBean {
    private final TreeMap<String, AttributeProxy> map = new TreeMap();
    private ArrayList<MBeanAttributeInfo> attributeInfoList;
    private MBeanInfo mbi;
    private final String beanName;
    private final String beanDescription;

    public ProxiedDynamicMBean(String beanName, String beanDescription, Collection<AttributeProxy> attributeStandins) {
        this.beanName = beanName;
        this.beanDescription = beanDescription;
        this.initialize(attributeStandins);
    }

    public void initialize(Collection<AttributeProxy> attributeStandins) {
        for (AttributeProxy attributeProxy : attributeStandins) {
            this.map.put(attributeProxy.getName(), attributeProxy);
        }
        this.attributeInfoList = new ArrayList(this.map.size());
        for (Map.Entry entry : this.map.entrySet()) {
            AttributeProxy standin = (AttributeProxy)entry.getValue();
            MBeanAttributeInfo tmpInfo = new MBeanAttributeInfo(standin.getName(), standin.getTypeClass().getName(), standin.getDescription(), standin.isRead(), standin.isWrite(), false);
            this.attributeInfoList.add(tmpInfo);
        }
        this.mbi = new MBeanInfo(this.getClass().getName(), this.beanDescription, this.attributeInfoList.toArray(new MBeanAttributeInfo[0]), null, null, null);
    }

    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public Object getAttribute(String name) throws AttributeNotFoundException {
        AttributeProxy attr = this.map.get(name);
        if (attr != null && attr.isRead()) {
            return attr.get(name);
        }
        return "";
    }

    @Override
    public void setAttribute(Attribute attribute) throws InvalidAttributeValueException, MBeanException, AttributeNotFoundException {
        String name = attribute.getName();
        AttributeProxy attr = this.map.get(name);
        if (attr != null && attr.isWrite()) {
            attr.set(name, attribute.getValue());
        }
    }

    @Override
    public AttributeList getAttributes(String[] names) {
        AttributeList list = new AttributeList();
        for (String name : names) {
            AttributeProxy attr = this.map.get(name);
            if (attr == null || !attr.isRead()) continue;
            Object value = attr.get(name);
            list.add(new Attribute(name, value));
        }
        return list;
    }

    @Override
    public AttributeList setAttributes(AttributeList list) {
        Attribute[] attrs = list.toArray(new Attribute[0]);
        AttributeList retlist = new AttributeList();
        for (Attribute attr : attrs) {
            String name = attr.getName();
            AttributeProxy a = this.map.get(name);
            if (a == null || !a.isWrite()) continue;
            a.set(name, attr.getValue());
            retlist.add(attr);
        }
        return retlist;
    }

    @Override
    public Object invoke(String name, Object[] args, String[] sig) throws MBeanException, ReflectionException {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mbi;
    }
}

