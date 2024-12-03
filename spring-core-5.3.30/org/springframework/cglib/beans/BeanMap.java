/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.beans;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.springframework.asm.ClassVisitor;
import org.springframework.cglib.beans.BeanMapEmitter;
import org.springframework.cglib.core.AbstractClassGenerator;
import org.springframework.cglib.core.KeyFactory;
import org.springframework.cglib.core.ReflectUtils;

public abstract class BeanMap
implements Map {
    public static final int REQUIRE_GETTER = 1;
    public static final int REQUIRE_SETTER = 2;
    protected Object bean;

    public static BeanMap create(Object bean) {
        Generator gen = new Generator();
        gen.setBean(bean);
        return gen.create();
    }

    public abstract BeanMap newInstance(Object var1);

    public abstract Class getPropertyType(String var1);

    protected BeanMap() {
    }

    protected BeanMap(Object bean) {
        this.setBean(bean);
    }

    public Object get(Object key) {
        return this.get(this.bean, key);
    }

    public Object put(Object key, Object value) {
        return this.put(this.bean, key, value);
    }

    public abstract Object get(Object var1, Object var2);

    public abstract Object put(Object var1, Object var2, Object var3);

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return this.bean;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.keySet().contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Iterator it = this.keySet().iterator();
        while (it.hasNext()) {
            Object v = this.get(it.next());
            if ((value != null || v != null) && (value == null || !value.equals(v))) continue;
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map t) {
        for (Object key : t.keySet()) {
            this.put(key, t.get(key));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Map)) {
            return false;
        }
        Map other = (Map)o;
        if (this.size() != other.size()) {
            return false;
        }
        for (Object key : this.keySet()) {
            if (!other.containsKey(key)) {
                return false;
            }
            Object v1 = this.get(key);
            Object v2 = other.get(key);
            if (v1 != null ? v1.equals(v2) : v2 == null) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int code = 0;
        for (Object key : this.keySet()) {
            Object value = this.get(key);
            code += (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }
        return code;
    }

    public Set entrySet() {
        HashMap copy = new HashMap();
        for (Object key : this.keySet()) {
            copy.put(key, this.get(key));
        }
        return Collections.unmodifiableMap(copy).entrySet();
    }

    public Collection values() {
        Set keys = this.keySet();
        ArrayList<Object> values = new ArrayList<Object>(keys.size());
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            values.add(this.get(it.next()));
        }
        return Collections.unmodifiableCollection(values);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        Iterator it = this.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            sb.append(key);
            sb.append('=');
            sb.append(this.get(key));
            if (!it.hasNext()) continue;
            sb.append(", ");
        }
        sb.append('}');
        return sb.toString();
    }

    public static class Generator
    extends AbstractClassGenerator {
        private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanMap.class.getName());
        private static final BeanMapKey KEY_FACTORY = (BeanMapKey)((Object)KeyFactory.create(BeanMapKey.class, KeyFactory.CLASS_BY_NAME));
        private Object bean;
        private Class beanClass;
        private int require;

        public Generator() {
            super(SOURCE);
        }

        public void setBean(Object bean) {
            this.bean = bean;
            if (bean != null) {
                this.beanClass = bean.getClass();
                this.setContextClass(this.beanClass);
            }
        }

        public void setBeanClass(Class beanClass) {
            this.beanClass = beanClass;
        }

        public void setRequire(int require) {
            this.require = require;
        }

        @Override
        protected ClassLoader getDefaultClassLoader() {
            return this.beanClass.getClassLoader();
        }

        @Override
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(this.beanClass);
        }

        public BeanMap create() {
            if (this.beanClass == null) {
                throw new IllegalArgumentException("Class of bean unknown");
            }
            this.setNamePrefix(this.beanClass.getName());
            return (BeanMap)super.create(KEY_FACTORY.newInstance(this.beanClass, this.require));
        }

        @Override
        public void generateClass(ClassVisitor v) throws Exception {
            new BeanMapEmitter(v, this.getClassName(), this.beanClass, this.require);
        }

        @Override
        protected Object firstInstance(Class type) {
            return ((BeanMap)ReflectUtils.newInstance(type)).newInstance(this.bean);
        }

        @Override
        protected Object nextInstance(Object instance) {
            return ((BeanMap)instance).newInstance(this.bean);
        }

        static interface BeanMapKey {
            public Object newInstance(Class var1, int var2);
        }
    }
}

