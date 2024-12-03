/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.BeanIntrospector
 *  org.apache.commons.beanutils.BeanUtilsBean
 *  org.apache.commons.beanutils.ConvertUtilsBean
 *  org.apache.commons.beanutils.DynaBean
 *  org.apache.commons.beanutils.FluentPropertyBeanIntrospector
 *  org.apache.commons.beanutils.PropertyUtilsBean
 *  org.apache.commons.beanutils.WrapDynaBean
 *  org.apache.commons.beanutils.WrapDynaClass
 *  org.apache.commons.lang3.ClassUtils
 */
package org.apache.commons.configuration2.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.beanutils.BeanIntrospector;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.FluentPropertyBeanIntrospector;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.commons.configuration2.beanutils.BeanCreationContext;
import org.apache.commons.configuration2.beanutils.BeanDeclaration;
import org.apache.commons.configuration2.beanutils.BeanFactory;
import org.apache.commons.configuration2.beanutils.DefaultBeanFactory;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.lang3.ClassUtils;

public final class BeanHelper {
    public static final BeanHelper INSTANCE = new BeanHelper();
    private static final BeanUtilsBean BEAN_UTILS_BEAN = BeanHelper.initBeanUtilsBean();
    private final Map<String, BeanFactory> beanFactories = Collections.synchronizedMap(new HashMap());
    private final BeanFactory defaultBeanFactory;

    public BeanHelper() {
        this(null);
    }

    public BeanHelper(BeanFactory defaultBeanFactory) {
        this.defaultBeanFactory = defaultBeanFactory != null ? defaultBeanFactory : DefaultBeanFactory.INSTANCE;
    }

    public void registerBeanFactory(String name, BeanFactory factory) {
        if (name == null) {
            throw new IllegalArgumentException("Name for bean factory must not be null!");
        }
        if (factory == null) {
            throw new IllegalArgumentException("Bean factory must not be null!");
        }
        this.beanFactories.put(name, factory);
    }

    public BeanFactory deregisterBeanFactory(String name) {
        return this.beanFactories.remove(name);
    }

    public Set<String> registeredFactoryNames() {
        return this.beanFactories.keySet();
    }

    public BeanFactory getDefaultBeanFactory() {
        return this.defaultBeanFactory;
    }

    public void initBean(Object bean, BeanDeclaration data) {
        BeanHelper.initBeanProperties(bean, data);
        Map<String, Object> nestedBeans = data.getNestedBeanDeclarations();
        if (nestedBeans != null) {
            if (bean instanceof Collection) {
                Collection coll = (Collection)bean;
                if (nestedBeans.size() == 1) {
                    Map.Entry<String, Object> e = nestedBeans.entrySet().iterator().next();
                    String propName2 = e.getKey();
                    Class<?> defaultClass = BeanHelper.getDefaultClass(bean, propName2);
                    if (e.getValue() instanceof List) {
                        List decls = (List)e.getValue();
                        decls.forEach(decl -> coll.add(this.createBean((BeanDeclaration)decl, defaultClass)));
                    } else {
                        coll.add(this.createBean((BeanDeclaration)e.getValue(), defaultClass));
                    }
                }
            } else {
                nestedBeans.forEach((propName, prop) -> {
                    Class<?> defaultClass = BeanHelper.getDefaultClass(bean, propName);
                    if (prop instanceof Collection) {
                        Collection<Object> beanCollection = BeanHelper.createPropertyCollection(propName, defaultClass);
                        ((Collection)prop).forEach(elemDef -> beanCollection.add(this.createBean((BeanDeclaration)elemDef)));
                        BeanHelper.initProperty(bean, propName, beanCollection);
                    } else {
                        BeanHelper.initProperty(bean, propName, this.createBean((BeanDeclaration)prop, defaultClass));
                    }
                });
            }
        }
    }

    public static void initBeanProperties(Object bean, BeanDeclaration data) {
        Map<String, Object> properties = data.getBeanProperties();
        if (properties != null) {
            properties.forEach((k, v) -> BeanHelper.initProperty(bean, k, v));
        }
    }

    public static DynaBean createWrapDynaBean(Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Bean must not be null!");
        }
        WrapDynaClass dynaClass = WrapDynaClass.createDynaClass(bean.getClass(), (PropertyUtilsBean)BEAN_UTILS_BEAN.getPropertyUtils());
        return new WrapDynaBean(bean, dynaClass);
    }

    public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        BEAN_UTILS_BEAN.getPropertyUtils().copyProperties(dest, orig);
    }

    private static Class<?> getDefaultClass(Object bean, String propName) {
        try {
            PropertyDescriptor desc = BEAN_UTILS_BEAN.getPropertyUtils().getPropertyDescriptor(bean, propName);
            if (desc == null) {
                return null;
            }
            return desc.getPropertyType();
        }
        catch (Exception ex) {
            return null;
        }
    }

    private static void initProperty(Object bean, String propName, Object value) {
        if (!BeanHelper.isPropertyWriteable(bean, propName)) {
            throw new ConfigurationRuntimeException("Property " + propName + " cannot be set on " + bean.getClass().getName());
        }
        try {
            BEAN_UTILS_BEAN.setProperty(bean, propName, value);
        }
        catch (IllegalAccessException | InvocationTargetException itex) {
            throw new ConfigurationRuntimeException(itex);
        }
    }

    private static Collection<Object> createPropertyCollection(String propName, Class<?> propertyClass) {
        AbstractCollection beanCollection;
        if (List.class.isAssignableFrom(propertyClass)) {
            beanCollection = new ArrayList<Object>();
        } else if (Set.class.isAssignableFrom(propertyClass)) {
            beanCollection = new TreeSet();
        } else {
            throw new UnsupportedOperationException("Unable to handle collection of type : " + propertyClass.getName() + " for property " + propName);
        }
        return beanCollection;
    }

    public static void setProperty(Object bean, String propName, Object value) {
        if (BeanHelper.isPropertyWriteable(bean, propName)) {
            BeanHelper.initProperty(bean, propName, value);
        }
    }

    public Object createBean(BeanDeclaration data, Class<?> defaultClass, Object param) {
        if (data == null) {
            throw new IllegalArgumentException("Bean declaration must not be null!");
        }
        BeanFactory factory = this.fetchBeanFactory(data);
        BeanCreationContext bcc = this.createBeanCreationContext(data, defaultClass, param, factory);
        try {
            return factory.createBean(bcc);
        }
        catch (Exception ex) {
            throw new ConfigurationRuntimeException(ex);
        }
    }

    public Object createBean(BeanDeclaration data, Class<?> defaultClass) {
        return this.createBean(data, defaultClass, null);
    }

    public Object createBean(BeanDeclaration data) {
        return this.createBean(data, null);
    }

    static Class<?> loadClass(String name) throws ClassNotFoundException {
        return ClassUtils.getClass((String)name);
    }

    private static boolean isPropertyWriteable(Object bean, String propName) {
        return BEAN_UTILS_BEAN.getPropertyUtils().isWriteable(bean, propName);
    }

    private static Class<?> fetchBeanClass(BeanDeclaration data, Class<?> defaultClass, BeanFactory factory) {
        String clsName = data.getBeanClassName();
        if (clsName != null) {
            try {
                return BeanHelper.loadClass(clsName);
            }
            catch (ClassNotFoundException cex) {
                throw new ConfigurationRuntimeException(cex);
            }
        }
        if (defaultClass != null) {
            return defaultClass;
        }
        Class<?> clazz = factory.getDefaultBeanClass();
        if (clazz == null) {
            throw new ConfigurationRuntimeException("Bean class is not specified!");
        }
        return clazz;
    }

    private BeanFactory fetchBeanFactory(BeanDeclaration data) {
        String factoryName = data.getBeanFactoryName();
        if (factoryName != null) {
            BeanFactory factory = this.beanFactories.get(factoryName);
            if (factory == null) {
                throw new ConfigurationRuntimeException("Unknown bean factory: " + factoryName);
            }
            return factory;
        }
        return this.getDefaultBeanFactory();
    }

    private BeanCreationContext createBeanCreationContext(BeanDeclaration data, Class<?> defaultClass, Object param, BeanFactory factory) {
        Class<?> beanClass = BeanHelper.fetchBeanClass(data, defaultClass, factory);
        return new BeanCreationContextImpl(this, beanClass, data, param);
    }

    private static BeanUtilsBean initBeanUtilsBean() {
        PropertyUtilsBean propUtilsBean = new PropertyUtilsBean();
        propUtilsBean.addBeanIntrospector((BeanIntrospector)new FluentPropertyBeanIntrospector());
        return new BeanUtilsBean(new ConvertUtilsBean(), propUtilsBean);
    }

    private static final class BeanCreationContextImpl
    implements BeanCreationContext {
        private final BeanHelper beanHelper;
        private final Class<?> beanClass;
        private final BeanDeclaration data;
        private final Object param;

        private BeanCreationContextImpl(BeanHelper helper, Class<?> beanClass, BeanDeclaration data, Object param) {
            this.beanHelper = helper;
            this.beanClass = beanClass;
            this.param = param;
            this.data = data;
        }

        @Override
        public void initBean(Object bean, BeanDeclaration data) {
            this.beanHelper.initBean(bean, data);
        }

        @Override
        public Object getParameter() {
            return this.param;
        }

        @Override
        public BeanDeclaration getBeanDeclaration() {
            return this.data;
        }

        @Override
        public Class<?> getBeanClass() {
            return this.beanClass;
        }

        @Override
        public Object createBean(BeanDeclaration data) {
            return this.beanHelper.createBean(data);
        }
    }
}

