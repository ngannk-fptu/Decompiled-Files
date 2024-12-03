/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.WrapDynaBean;

public class WrapDynaClass
implements DynaClass {
    private String beanClassName = null;
    private Reference<Class<?>> beanClassRef = null;
    private final PropertyUtilsBean propertyUtilsBean;
    @Deprecated
    protected Class<?> beanClass = null;
    protected PropertyDescriptor[] descriptors = null;
    protected HashMap<String, PropertyDescriptor> descriptorsMap = new HashMap();
    protected DynaProperty[] properties = null;
    protected HashMap<String, DynaProperty> propertiesMap = new HashMap();
    private static final ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>> CLASSLOADER_CACHE = new ContextClassLoaderLocal<Map<CacheKey, WrapDynaClass>>(){

        @Override
        protected Map<CacheKey, WrapDynaClass> initialValue() {
            return new WeakHashMap<CacheKey, WrapDynaClass>();
        }
    };
    @Deprecated
    protected static HashMap<Object, Object> dynaClasses = new HashMap<Object, Object>(){

        @Override
        public void clear() {
            WrapDynaClass.getDynaClassesMap().clear();
        }

        @Override
        public boolean containsKey(Object key) {
            return WrapDynaClass.getDynaClassesMap().containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return WrapDynaClass.getDynaClassesMap().containsValue(value);
        }

        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            return WrapDynaClass.getDynaClassesMap().entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return WrapDynaClass.getDynaClassesMap().equals(o);
        }

        @Override
        public Object get(Object key) {
            return WrapDynaClass.getDynaClassesMap().get(key);
        }

        @Override
        public int hashCode() {
            return WrapDynaClass.getDynaClassesMap().hashCode();
        }

        @Override
        public boolean isEmpty() {
            return WrapDynaClass.getDynaClassesMap().isEmpty();
        }

        @Override
        public Set<Object> keySet() {
            HashSet<Object> result = new HashSet<Object>();
            for (CacheKey k : WrapDynaClass.getClassesCache().keySet()) {
                result.add(k.beanClass);
            }
            return result;
        }

        @Override
        public Object put(Object key, Object value) {
            return WrapDynaClass.getClassesCache().put(new CacheKey((Class)key, PropertyUtilsBean.getInstance()), (WrapDynaClass)value);
        }

        @Override
        public void putAll(Map<? extends Object, ? extends Object> m) {
            for (Map.Entry<? extends Object, ? extends Object> e : m.entrySet()) {
                this.put(e.getKey(), e.getValue());
            }
        }

        @Override
        public Object remove(Object key) {
            return WrapDynaClass.getDynaClassesMap().remove(key);
        }

        @Override
        public int size() {
            return WrapDynaClass.getDynaClassesMap().size();
        }

        @Override
        public Collection<Object> values() {
            return WrapDynaClass.getDynaClassesMap().values();
        }
    };

    private WrapDynaClass(Class<?> beanClass, PropertyUtilsBean propUtils) {
        this.beanClassRef = new SoftReference(beanClass);
        this.beanClassName = beanClass.getName();
        this.propertyUtilsBean = propUtils;
        this.introspect();
    }

    private static Map<Object, Object> getDynaClassesMap() {
        Map<Object, Object> cache = CLASSLOADER_CACHE.get();
        return cache;
    }

    private static Map<CacheKey, WrapDynaClass> getClassesCache() {
        return CLASSLOADER_CACHE.get();
    }

    protected Class<?> getBeanClass() {
        return this.beanClassRef.get();
    }

    @Override
    public String getName() {
        return this.beanClassName;
    }

    @Override
    public DynaProperty getDynaProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException("No property name specified");
        }
        return this.propertiesMap.get(name);
    }

    @Override
    public DynaProperty[] getDynaProperties() {
        return this.properties;
    }

    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        return new WrapDynaBean(this.getBeanClass().newInstance());
    }

    public PropertyDescriptor getPropertyDescriptor(String name) {
        return this.descriptorsMap.get(name);
    }

    public static void clear() {
        WrapDynaClass.getClassesCache().clear();
    }

    public static WrapDynaClass createDynaClass(Class<?> beanClass) {
        return WrapDynaClass.createDynaClass(beanClass, null);
    }

    public static WrapDynaClass createDynaClass(Class<?> beanClass, PropertyUtilsBean pu) {
        PropertyUtilsBean propUtils = pu != null ? pu : PropertyUtilsBean.getInstance();
        CacheKey key = new CacheKey(beanClass, propUtils);
        WrapDynaClass dynaClass = WrapDynaClass.getClassesCache().get(key);
        if (dynaClass == null) {
            dynaClass = new WrapDynaClass(beanClass, propUtils);
            WrapDynaClass.getClassesCache().put(key, dynaClass);
        }
        return dynaClass;
    }

    protected PropertyUtilsBean getPropertyUtilsBean() {
        return this.propertyUtilsBean;
    }

    protected void introspect() {
        Object mappeds;
        Class<?> beanClass = this.getBeanClass();
        PropertyDescriptor[] regulars = this.getPropertyUtilsBean().getPropertyDescriptors(beanClass);
        if (regulars == null) {
            regulars = new PropertyDescriptor[]{};
        }
        if ((mappeds = PropertyUtils.getMappedPropertyDescriptors(beanClass)) == null) {
            mappeds = new HashMap();
        }
        this.properties = new DynaProperty[regulars.length + mappeds.size()];
        for (int i = 0; i < regulars.length; ++i) {
            this.descriptorsMap.put(regulars[i].getName(), regulars[i]);
            this.properties[i] = new DynaProperty(regulars[i].getName(), regulars[i].getPropertyType());
            this.propertiesMap.put(this.properties[i].getName(), this.properties[i]);
        }
        int j = regulars.length;
        for (String name : mappeds.keySet()) {
            PropertyDescriptor descriptor = (PropertyDescriptor)mappeds.get(name);
            this.properties[j] = new DynaProperty(descriptor.getName(), Map.class);
            this.propertiesMap.put(this.properties[j].getName(), this.properties[j]);
            ++j;
        }
    }

    private static class CacheKey {
        private final Class<?> beanClass;
        private final PropertyUtilsBean propUtils;

        public CacheKey(Class<?> beanCls, PropertyUtilsBean pu) {
            this.beanClass = beanCls;
            this.propUtils = pu;
        }

        public int hashCode() {
            int factor = 31;
            int result = 17;
            result = 31 * this.beanClass.hashCode() + result;
            result = 31 * this.propUtils.hashCode() + result;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CacheKey)) {
                return false;
            }
            CacheKey c = (CacheKey)obj;
            return this.beanClass.equals(c.beanClass) && this.propUtils.equals(c.propUtils);
        }
    }
}

