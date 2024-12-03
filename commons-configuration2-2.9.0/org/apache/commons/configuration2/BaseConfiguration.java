/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;

public class BaseConfiguration
extends AbstractConfiguration
implements Cloneable {
    private Map<String, Object> store = new LinkedHashMap<String, Object>();

    @Override
    protected void addPropertyDirect(String key, Object value) {
        Object previousValue = this.getPropertyInternal(key);
        if (previousValue == null) {
            this.store.put(key, value);
        } else if (previousValue instanceof List) {
            List valueList = (List)previousValue;
            valueList.add(value);
        } else {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(previousValue);
            list.add(value);
            this.store.put(key, list);
        }
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.store.get(key);
    }

    @Override
    protected boolean isEmptyInternal() {
        return this.store.isEmpty();
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.store.containsKey(key);
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.store.remove(key);
    }

    @Override
    protected void clearInternal() {
        this.store.clear();
    }

    @Override
    protected int sizeInternal() {
        return this.store.size();
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return this.store.keySet().iterator();
    }

    @Override
    public Object clone() {
        try {
            BaseConfiguration copy = (BaseConfiguration)super.clone();
            this.cloneStore(copy);
            copy.cloneInterpolator(this);
            return copy;
        }
        catch (CloneNotSupportedException cex) {
            throw new ConfigurationRuntimeException(cex);
        }
    }

    private void cloneStore(BaseConfiguration copy) throws CloneNotSupportedException {
        Map clonedStore;
        copy.store = clonedStore = (Map)ConfigurationUtils.clone(this.store);
        this.store.forEach((k, v) -> {
            if (v instanceof Collection) {
                Collection strList = (Collection)v;
                copy.store.put((String)k, new ArrayList(strList));
            }
        });
    }
}

