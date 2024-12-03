/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.assembler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.assembler.AbstractReflectiveMBeanInfoAssembler;
import org.springframework.jmx.export.assembler.AutodetectCapableMBeanInfoAssembler;
import org.springframework.jmx.export.metadata.InvalidMetadataException;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.metadata.JmxMetadataUtils;
import org.springframework.jmx.export.metadata.ManagedAttribute;
import org.springframework.jmx.export.metadata.ManagedMetric;
import org.springframework.jmx.export.metadata.ManagedNotification;
import org.springframework.jmx.export.metadata.ManagedOperation;
import org.springframework.jmx.export.metadata.ManagedOperationParameter;
import org.springframework.jmx.export.metadata.ManagedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class MetadataMBeanInfoAssembler
extends AbstractReflectiveMBeanInfoAssembler
implements AutodetectCapableMBeanInfoAssembler,
InitializingBean {
    @Nullable
    private JmxAttributeSource attributeSource;

    public MetadataMBeanInfoAssembler() {
    }

    public MetadataMBeanInfoAssembler(JmxAttributeSource attributeSource) {
        Assert.notNull((Object)attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    public void setAttributeSource(JmxAttributeSource attributeSource) {
        Assert.notNull((Object)attributeSource, "JmxAttributeSource must not be null");
        this.attributeSource = attributeSource;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.attributeSource == null) {
            throw new IllegalArgumentException("Property 'attributeSource' is required");
        }
    }

    private JmxAttributeSource obtainAttributeSource() {
        Assert.state(this.attributeSource != null, "No JmxAttributeSource set");
        return this.attributeSource;
    }

    @Override
    protected void checkManagedBean(Object managedBean) throws IllegalArgumentException {
        if (AopUtils.isJdkDynamicProxy(managedBean)) {
            throw new IllegalArgumentException("MetadataMBeanInfoAssembler does not support JDK dynamic proxies - export the target beans directly or use CGLIB proxies instead");
        }
    }

    @Override
    public boolean includeBean(Class<?> beanClass, String beanName) {
        return this.obtainAttributeSource().getManagedResource(this.getClassToExpose(beanClass)) != null;
    }

    @Override
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return this.hasManagedAttribute(method) || this.hasManagedMetric(method);
    }

    @Override
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return this.hasManagedAttribute(method);
    }

    @Override
    protected boolean includeOperation(Method method, String beanKey) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
        return pd != null && this.hasManagedAttribute(method) || this.hasManagedOperation(method);
    }

    private boolean hasManagedAttribute(Method method) {
        return this.obtainAttributeSource().getManagedAttribute(method) != null;
    }

    private boolean hasManagedMetric(Method method) {
        return this.obtainAttributeSource().getManagedMetric(method) != null;
    }

    private boolean hasManagedOperation(Method method) {
        return this.obtainAttributeSource().getManagedOperation(method) != null;
    }

    @Override
    protected String getDescription(Object managedBean, String beanKey) {
        ManagedResource mr = this.obtainAttributeSource().getManagedResource(this.getClassToExpose(managedBean));
        return mr != null ? mr.getDescription() : "";
    }

    @Override
    protected String getAttributeDescription(PropertyDescriptor propertyDescriptor, String beanKey) {
        ManagedMetric metric;
        ManagedAttribute setter;
        Method readMethod = propertyDescriptor.getReadMethod();
        Method writeMethod = propertyDescriptor.getWriteMethod();
        ManagedAttribute getter = readMethod != null ? this.obtainAttributeSource().getManagedAttribute(readMethod) : null;
        ManagedAttribute managedAttribute = setter = writeMethod != null ? this.obtainAttributeSource().getManagedAttribute(writeMethod) : null;
        if (getter != null && StringUtils.hasText(getter.getDescription())) {
            return getter.getDescription();
        }
        if (setter != null && StringUtils.hasText(setter.getDescription())) {
            return setter.getDescription();
        }
        ManagedMetric managedMetric = metric = readMethod != null ? this.obtainAttributeSource().getManagedMetric(readMethod) : null;
        if (metric != null && StringUtils.hasText(metric.getDescription())) {
            return metric.getDescription();
        }
        return propertyDescriptor.getDisplayName();
    }

    @Override
    protected String getOperationDescription(Method method, String beanKey) {
        PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
        if (pd != null) {
            ManagedAttribute ma = this.obtainAttributeSource().getManagedAttribute(method);
            if (ma != null && StringUtils.hasText(ma.getDescription())) {
                return ma.getDescription();
            }
            ManagedMetric metric = this.obtainAttributeSource().getManagedMetric(method);
            if (metric != null && StringUtils.hasText(metric.getDescription())) {
                return metric.getDescription();
            }
            return method.getName();
        }
        ManagedOperation mo = this.obtainAttributeSource().getManagedOperation(method);
        if (mo != null && StringUtils.hasText(mo.getDescription())) {
            return mo.getDescription();
        }
        return method.getName();
    }

    @Override
    protected MBeanParameterInfo[] getOperationParameters(Method method, String beanKey) {
        Object[] params = this.obtainAttributeSource().getManagedOperationParameters(method);
        if (ObjectUtils.isEmpty(params)) {
            return super.getOperationParameters(method, beanKey);
        }
        MBeanParameterInfo[] parameterInfo = new MBeanParameterInfo[params.length];
        Class<?>[] methodParameters = method.getParameterTypes();
        for (int i2 = 0; i2 < params.length; ++i2) {
            Object param = params[i2];
            parameterInfo[i2] = new MBeanParameterInfo(((ManagedOperationParameter)param).getName(), methodParameters[i2].getName(), ((ManagedOperationParameter)param).getDescription());
        }
        return parameterInfo;
    }

    @Override
    protected ModelMBeanNotificationInfo[] getNotificationInfo(Object managedBean, String beanKey) {
        ManagedNotification[] notificationAttributes = this.obtainAttributeSource().getManagedNotifications(this.getClassToExpose(managedBean));
        ModelMBeanNotificationInfo[] notificationInfos = new ModelMBeanNotificationInfo[notificationAttributes.length];
        for (int i2 = 0; i2 < notificationAttributes.length; ++i2) {
            ManagedNotification attribute = notificationAttributes[i2];
            notificationInfos[i2] = JmxMetadataUtils.convertToModelMBeanNotificationInfo(attribute);
        }
        return notificationInfos;
    }

    @Override
    protected void populateMBeanDescriptor(Descriptor desc, Object managedBean, String beanKey) {
        ManagedResource mr = this.obtainAttributeSource().getManagedResource(this.getClassToExpose(managedBean));
        if (mr == null) {
            throw new InvalidMetadataException("No ManagedResource attribute found for class: " + this.getClassToExpose(managedBean));
        }
        this.applyCurrencyTimeLimit(desc, mr.getCurrencyTimeLimit());
        if (mr.isLog()) {
            desc.setField("log", "true");
        }
        if (StringUtils.hasLength(mr.getLogFile())) {
            desc.setField("logFile", mr.getLogFile());
        }
        if (StringUtils.hasLength(mr.getPersistPolicy())) {
            desc.setField("persistPolicy", mr.getPersistPolicy());
        }
        if (mr.getPersistPeriod() >= 0) {
            desc.setField("persistPeriod", Integer.toString(mr.getPersistPeriod()));
        }
        if (StringUtils.hasLength(mr.getPersistName())) {
            desc.setField("persistName", mr.getPersistName());
        }
        if (StringUtils.hasLength(mr.getPersistLocation())) {
            desc.setField("persistLocation", mr.getPersistLocation());
        }
    }

    @Override
    protected void populateAttributeDescriptor(Descriptor desc, @Nullable Method getter, @Nullable Method setter, String beanKey) {
        ManagedMetric metric;
        if (getter != null && (metric = this.obtainAttributeSource().getManagedMetric(getter)) != null) {
            this.populateMetricDescriptor(desc, metric);
            return;
        }
        ManagedAttribute gma = getter != null ? this.obtainAttributeSource().getManagedAttribute(getter) : null;
        ManagedAttribute sma = setter != null ? this.obtainAttributeSource().getManagedAttribute(setter) : null;
        this.populateAttributeDescriptor(desc, gma != null ? gma : ManagedAttribute.EMPTY, sma != null ? sma : ManagedAttribute.EMPTY);
    }

    private void populateAttributeDescriptor(Descriptor desc, ManagedAttribute gma, ManagedAttribute sma) {
        int persistPeriod;
        this.applyCurrencyTimeLimit(desc, this.resolveIntDescriptor(gma.getCurrencyTimeLimit(), sma.getCurrencyTimeLimit()));
        Object defaultValue = this.resolveObjectDescriptor(gma.getDefaultValue(), sma.getDefaultValue());
        desc.setField("default", defaultValue);
        String persistPolicy = this.resolveStringDescriptor(gma.getPersistPolicy(), sma.getPersistPolicy());
        if (StringUtils.hasLength(persistPolicy)) {
            desc.setField("persistPolicy", persistPolicy);
        }
        if ((persistPeriod = this.resolveIntDescriptor(gma.getPersistPeriod(), sma.getPersistPeriod())) >= 0) {
            desc.setField("persistPeriod", Integer.toString(persistPeriod));
        }
    }

    private void populateMetricDescriptor(Descriptor desc, ManagedMetric metric) {
        this.applyCurrencyTimeLimit(desc, metric.getCurrencyTimeLimit());
        if (StringUtils.hasLength(metric.getPersistPolicy())) {
            desc.setField("persistPolicy", metric.getPersistPolicy());
        }
        if (metric.getPersistPeriod() >= 0) {
            desc.setField("persistPeriod", Integer.toString(metric.getPersistPeriod()));
        }
        if (StringUtils.hasLength(metric.getDisplayName())) {
            desc.setField("displayName", metric.getDisplayName());
        }
        if (StringUtils.hasLength(metric.getUnit())) {
            desc.setField("units", metric.getUnit());
        }
        if (StringUtils.hasLength(metric.getCategory())) {
            desc.setField("metricCategory", metric.getCategory());
        }
        desc.setField("metricType", metric.getMetricType().toString());
    }

    @Override
    protected void populateOperationDescriptor(Descriptor desc, Method method, String beanKey) {
        ManagedOperation mo = this.obtainAttributeSource().getManagedOperation(method);
        if (mo != null) {
            this.applyCurrencyTimeLimit(desc, mo.getCurrencyTimeLimit());
        }
    }

    private int resolveIntDescriptor(int getter, int setter) {
        return getter >= setter ? getter : setter;
    }

    @Nullable
    private Object resolveObjectDescriptor(@Nullable Object getter, @Nullable Object setter) {
        return getter != null ? getter : setter;
    }

    @Nullable
    private String resolveStringDescriptor(@Nullable String getter, @Nullable String setter) {
        return StringUtils.hasLength(getter) ? getter : setter;
    }
}

