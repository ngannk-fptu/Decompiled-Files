/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.DynaBean
 *  org.apache.commons.beanutils.DynaClass
 *  org.apache.commons.beanutils.DynaProperty
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.configuration2.beanutils.BeanHelper;
import org.apache.commons.configuration2.builder.combined.MultiWrapDynaClass;

class MultiWrapDynaBean
implements DynaBean {
    private final DynaClass dynaClass;
    private final Map<String, DynaBean> propsToBeans = new HashMap<String, DynaBean>();

    public MultiWrapDynaBean(Collection<?> beans) {
        ArrayList beanClasses = new ArrayList(beans.size());
        beans.forEach(bean -> {
            DynaBean dynaBean = MultiWrapDynaBean.createDynaBean(bean);
            DynaClass beanClass = dynaBean.getDynaClass();
            for (DynaProperty prop : beanClass.getDynaProperties()) {
                this.propsToBeans.putIfAbsent(prop.getName(), dynaBean);
            }
            beanClasses.add(beanClass);
        });
        this.dynaClass = new MultiWrapDynaClass(beanClasses);
    }

    public boolean contains(String name, String key) {
        throw new UnsupportedOperationException("contains() operation not supported!");
    }

    public Object get(String name) {
        return this.fetchBean(name).get(name);
    }

    public Object get(String name, int index) {
        return this.fetchBean(name).get(name, index);
    }

    public Object get(String name, String key) {
        return this.fetchBean(name).get(name, key);
    }

    public DynaClass getDynaClass() {
        return this.dynaClass;
    }

    public void remove(String name, String key) {
        throw new UnsupportedOperationException("remove() operation not supported!");
    }

    public void set(String name, Object value) {
        this.fetchBean(name).set(name, value);
    }

    public void set(String name, int index, Object value) {
        this.fetchBean(name).set(name, index, value);
    }

    public void set(String name, String key, Object value) {
        this.fetchBean(name).set(name, key, value);
    }

    private DynaBean fetchBean(String property) {
        DynaBean dynaBean = this.propsToBeans.get(property);
        if (dynaBean == null) {
            dynaBean = this.propsToBeans.values().iterator().next();
        }
        return dynaBean;
    }

    private static DynaBean createDynaBean(Object bean) {
        if (bean instanceof DynaBean) {
            return (DynaBean)bean;
        }
        return BeanHelper.createWrapDynaBean(bean);
    }
}

