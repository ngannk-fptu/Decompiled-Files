/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.support.AopUtils
 */
package org.springframework.jmx.export.assembler;

import javax.management.Descriptor;
import javax.management.JMException;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.springframework.aop.support.AopUtils;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.support.JmxUtils;

public abstract class AbstractMBeanInfoAssembler
implements MBeanInfoAssembler {
    @Override
    public ModelMBeanInfo getMBeanInfo(Object managedBean, String beanKey) throws JMException {
        this.checkManagedBean(managedBean);
        ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(this.getClassName(managedBean, beanKey), this.getDescription(managedBean, beanKey), this.getAttributeInfo(managedBean, beanKey), this.getConstructorInfo(managedBean, beanKey), this.getOperationInfo(managedBean, beanKey), this.getNotificationInfo(managedBean, beanKey));
        Descriptor desc = info.getMBeanDescriptor();
        this.populateMBeanDescriptor(desc, managedBean, beanKey);
        info.setMBeanDescriptor(desc);
        return info;
    }

    protected void checkManagedBean(Object managedBean) throws IllegalArgumentException {
    }

    protected Class<?> getTargetClass(Object managedBean) {
        return AopUtils.getTargetClass((Object)managedBean);
    }

    protected Class<?> getClassToExpose(Object managedBean) {
        return JmxUtils.getClassToExpose(managedBean);
    }

    protected Class<?> getClassToExpose(Class<?> beanClass) {
        return JmxUtils.getClassToExpose(beanClass);
    }

    protected String getClassName(Object managedBean, String beanKey) throws JMException {
        return this.getTargetClass(managedBean).getName();
    }

    protected String getDescription(Object managedBean, String beanKey) throws JMException {
        String targetClassName = this.getTargetClass(managedBean).getName();
        if (AopUtils.isAopProxy((Object)managedBean)) {
            return "Proxy for " + targetClassName;
        }
        return targetClassName;
    }

    protected void populateMBeanDescriptor(Descriptor descriptor, Object managedBean, String beanKey) throws JMException {
    }

    protected ModelMBeanConstructorInfo[] getConstructorInfo(Object managedBean, String beanKey) throws JMException {
        return new ModelMBeanConstructorInfo[0];
    }

    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) throws JMException {
        return new ModelMBeanNotificationInfo[0];
    }

    protected abstract ModelMBeanAttributeInfo[] getAttributeInfo(Object var1, String var2) throws JMException;

    protected abstract ModelMBeanOperationInfo[] getOperationInfo(Object var1, String var2) throws JMException;
}

