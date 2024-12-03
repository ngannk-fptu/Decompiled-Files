/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.assembler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.management.Descriptor;
import javax.management.JMException;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.jmx.export.assembler.AbstractMBeanInfoAssembler;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.lang.Nullable;

public abstract class AbstractReflectiveMBeanInfoAssembler
extends AbstractMBeanInfoAssembler {
    protected static final String FIELD_GET_METHOD = "getMethod";
    protected static final String FIELD_SET_METHOD = "setMethod";
    protected static final String FIELD_ROLE = "role";
    protected static final String ROLE_GETTER = "getter";
    protected static final String ROLE_SETTER = "setter";
    protected static final String ROLE_OPERATION = "operation";
    protected static final String FIELD_VISIBILITY = "visibility";
    protected static final int ATTRIBUTE_OPERATION_VISIBILITY = 4;
    protected static final String FIELD_CLASS = "class";
    protected static final String FIELD_LOG = "log";
    protected static final String FIELD_LOG_FILE = "logFile";
    protected static final String FIELD_CURRENCY_TIME_LIMIT = "currencyTimeLimit";
    protected static final String FIELD_DEFAULT = "default";
    protected static final String FIELD_PERSIST_POLICY = "persistPolicy";
    protected static final String FIELD_PERSIST_PERIOD = "persistPeriod";
    protected static final String FIELD_PERSIST_LOCATION = "persistLocation";
    protected static final String FIELD_PERSIST_NAME = "persistName";
    protected static final String FIELD_DISPLAY_NAME = "displayName";
    protected static final String FIELD_UNITS = "units";
    protected static final String FIELD_METRIC_TYPE = "metricType";
    protected static final String FIELD_METRIC_CATEGORY = "metricCategory";
    @Nullable
    private Integer defaultCurrencyTimeLimit;
    private boolean useStrictCasing = true;
    private boolean exposeClassDescriptor = false;
    @Nullable
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public void setDefaultCurrencyTimeLimit(@Nullable Integer defaultCurrencyTimeLimit) {
        this.defaultCurrencyTimeLimit = defaultCurrencyTimeLimit;
    }

    @Nullable
    protected Integer getDefaultCurrencyTimeLimit() {
        return this.defaultCurrencyTimeLimit;
    }

    public void setUseStrictCasing(boolean useStrictCasing) {
        this.useStrictCasing = useStrictCasing;
    }

    protected boolean isUseStrictCasing() {
        return this.useStrictCasing;
    }

    public void setExposeClassDescriptor(boolean exposeClassDescriptor) {
        this.exposeClassDescriptor = exposeClassDescriptor;
    }

    protected boolean isExposeClassDescriptor() {
        return this.exposeClassDescriptor;
    }

    public void setParameterNameDiscoverer(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    @Nullable
    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    @Override
    protected ModelMBeanAttributeInfo[] getAttributeInfo(Object managedBean, String beanKey) throws JMException {
        PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors(this.getClassToExpose(managedBean));
        ArrayList<ModelMBeanAttributeInfo> infos = new ArrayList<ModelMBeanAttributeInfo>();
        for (PropertyDescriptor prop : props) {
            Method setter;
            Method getter = prop.getReadMethod();
            if (getter != null && getter.getDeclaringClass() == Object.class) continue;
            if (getter != null && !this.includeReadAttribute(getter, beanKey)) {
                getter = null;
            }
            if ((setter = prop.getWriteMethod()) != null && !this.includeWriteAttribute(setter, beanKey)) {
                setter = null;
            }
            if (getter == null && setter == null) continue;
            String attrName = JmxUtils.getAttributeName(prop, this.isUseStrictCasing());
            String description = this.getAttributeDescription(prop, beanKey);
            ModelMBeanAttributeInfo info = new ModelMBeanAttributeInfo(attrName, description, getter, setter);
            Descriptor desc = info.getDescriptor();
            if (getter != null) {
                desc.setField(FIELD_GET_METHOD, getter.getName());
            }
            if (setter != null) {
                desc.setField(FIELD_SET_METHOD, setter.getName());
            }
            this.populateAttributeDescriptor(desc, getter, setter, beanKey);
            info.setDescriptor(desc);
            infos.add(info);
        }
        return infos.toArray(new ModelMBeanAttributeInfo[0]);
    }

    @Override
    protected ModelMBeanOperationInfo[] getOperationInfo(Object managedBean, String beanKey) {
        Method[] methods = this.getClassToExpose(managedBean).getMethods();
        ArrayList<ModelMBeanOperationInfo> infos = new ArrayList<ModelMBeanOperationInfo>();
        for (Method method : methods) {
            Descriptor desc;
            if (method.isSynthetic() || Object.class == method.getDeclaringClass()) continue;
            ModelMBeanOperationInfo info = null;
            PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
            if (pd != null && (method.equals(pd.getReadMethod()) && this.includeReadAttribute(method, beanKey) || method.equals(pd.getWriteMethod()) && this.includeWriteAttribute(method, beanKey))) {
                info = this.createModelMBeanOperationInfo(method, pd.getName(), beanKey);
                desc = info.getDescriptor();
                if (method.equals(pd.getReadMethod())) {
                    desc.setField(FIELD_ROLE, ROLE_GETTER);
                } else {
                    desc.setField(FIELD_ROLE, ROLE_SETTER);
                }
                desc.setField(FIELD_VISIBILITY, 4);
                if (this.isExposeClassDescriptor()) {
                    desc.setField(FIELD_CLASS, this.getClassForDescriptor(managedBean).getName());
                }
                info.setDescriptor(desc);
            }
            if (info == null && this.includeOperation(method, beanKey)) {
                info = this.createModelMBeanOperationInfo(method, method.getName(), beanKey);
                desc = info.getDescriptor();
                desc.setField(FIELD_ROLE, ROLE_OPERATION);
                if (this.isExposeClassDescriptor()) {
                    desc.setField(FIELD_CLASS, this.getClassForDescriptor(managedBean).getName());
                }
                this.populateOperationDescriptor(desc, method, beanKey);
                info.setDescriptor(desc);
            }
            if (info == null) continue;
            infos.add(info);
        }
        return infos.toArray(new ModelMBeanOperationInfo[0]);
    }

    protected ModelMBeanOperationInfo createModelMBeanOperationInfo(Method method, String name, String beanKey) {
        MBeanParameterInfo[] params = this.getOperationParameters(method, beanKey);
        if (params.length == 0) {
            return new ModelMBeanOperationInfo(this.getOperationDescription(method, beanKey), method);
        }
        return new ModelMBeanOperationInfo(method.getName(), this.getOperationDescription(method, beanKey), this.getOperationParameters(method, beanKey), method.getReturnType().getName(), 3);
    }

    protected Class<?> getClassForDescriptor(Object managedBean) {
        if (AopUtils.isJdkDynamicProxy(managedBean)) {
            return AopProxyUtils.proxiedUserInterfaces(managedBean)[0];
        }
        return this.getClassToExpose(managedBean);
    }

    protected abstract boolean includeReadAttribute(Method var1, String var2);

    protected abstract boolean includeWriteAttribute(Method var1, String var2);

    protected abstract boolean includeOperation(Method var1, String var2);

    protected String getAttributeDescription(PropertyDescriptor propertyDescriptor, String beanKey) {
        return propertyDescriptor.getDisplayName();
    }

    protected String getOperationDescription(Method method, String beanKey) {
        return method.getName();
    }

    protected MBeanParameterInfo[] getOperationParameters(Method method, String beanKey) {
        String[] paramNames;
        ParameterNameDiscoverer paramNameDiscoverer = this.getParameterNameDiscoverer();
        String[] stringArray = paramNames = paramNameDiscoverer != null ? paramNameDiscoverer.getParameterNames(method) : null;
        if (paramNames == null) {
            return new MBeanParameterInfo[0];
        }
        MBeanParameterInfo[] info = new MBeanParameterInfo[paramNames.length];
        Class<?>[] typeParameters = method.getParameterTypes();
        for (int i = 0; i < info.length; ++i) {
            info[i] = new MBeanParameterInfo(paramNames[i], typeParameters[i].getName(), paramNames[i]);
        }
        return info;
    }

    @Override
    protected void populateMBeanDescriptor(Descriptor descriptor, Object managedBean, String beanKey) {
        this.applyDefaultCurrencyTimeLimit(descriptor);
    }

    protected void populateAttributeDescriptor(Descriptor desc, @Nullable Method getter, @Nullable Method setter, String beanKey) {
        this.applyDefaultCurrencyTimeLimit(desc);
    }

    protected void populateOperationDescriptor(Descriptor desc, Method method, String beanKey) {
        this.applyDefaultCurrencyTimeLimit(desc);
    }

    protected final void applyDefaultCurrencyTimeLimit(Descriptor desc) {
        if (this.getDefaultCurrencyTimeLimit() != null) {
            desc.setField(FIELD_CURRENCY_TIME_LIMIT, this.getDefaultCurrencyTimeLimit().toString());
        }
    }

    protected void applyCurrencyTimeLimit(Descriptor desc, int currencyTimeLimit) {
        if (currencyTimeLimit > 0) {
            desc.setField(FIELD_CURRENCY_TIME_LIMIT, Integer.toString(currencyTimeLimit));
        } else if (currencyTimeLimit == 0) {
            desc.setField(FIELD_CURRENCY_TIME_LIMIT, Integer.toString(Integer.MAX_VALUE));
        } else {
            this.applyDefaultCurrencyTimeLimit(desc);
        }
    }
}

