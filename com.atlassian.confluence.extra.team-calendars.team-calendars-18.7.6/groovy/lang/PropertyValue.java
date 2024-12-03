/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaProperty;

public class PropertyValue {
    private Object bean;
    private MetaProperty mp;

    public PropertyValue(Object bean, MetaProperty mp) {
        this.bean = bean;
        this.mp = mp;
    }

    public String getName() {
        return this.mp.getName();
    }

    public Class getType() {
        return this.mp.getType();
    }

    public Object getValue() {
        return this.mp.getProperty(this.bean);
    }

    public void setValue(Object value) {
        this.mp.setProperty(this.bean, value);
    }
}

