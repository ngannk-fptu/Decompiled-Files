/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.MetaProperty;
import java.util.Map;

public class MetaExpandoProperty
extends MetaProperty {
    Object value = null;

    public MetaExpandoProperty(Map.Entry entry) {
        super((String)entry.getKey(), Object.class);
        this.value = entry.getValue();
    }

    @Override
    public Object getProperty(Object object) {
        return this.value;
    }

    @Override
    public void setProperty(Object object, Object newValue) {
        this.value = newValue;
    }
}

