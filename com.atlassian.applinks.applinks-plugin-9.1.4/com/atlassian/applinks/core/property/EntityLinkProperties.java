/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.PropertySet
 */
package com.atlassian.applinks.core.property;

import com.atlassian.applinks.api.PropertySet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EntityLinkProperties
implements PropertySet {
    private final PropertySet wrappedPropertySet;
    private final Lock write = new ReentrantLock();

    public EntityLinkProperties(PropertySet wrappedPropertySet) {
        this.wrappedPropertySet = wrappedPropertySet;
    }

    public void setProperties(EntityLinkProperties props) {
        Object keys = props.wrappedPropertySet.getProperty(Property.KEYS.key());
        if (keys != null) {
            this.wrappedPropertySet.putProperty(Property.KEYS.key(), keys);
            for (String key : (List)keys) {
                this.wrappedPropertySet.putProperty(key, props.wrappedPropertySet.getProperty(key));
            }
        }
    }

    public Object getProperty(String key) {
        return this.wrappedPropertySet.getProperty(this.checkNotReserved(key));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object putProperty(String key, Object value) {
        Object oldValue = this.wrappedPropertySet.putProperty(this.checkNotReserved(key), value);
        try {
            this.write.lock();
            List<String> keys = this.getPropertyKeys();
            keys.add(key);
            this.setPropertyKeys(keys);
        }
        finally {
            this.write.unlock();
        }
        return oldValue;
    }

    private String checkNotReserved(String key) {
        if (Property.KEYS.key().equals(key)) {
            throw new IllegalArgumentException("The property '" + Property.KEYS.key() + "' is reserved. Please use a different key.");
        }
        return key;
    }

    public void removeAll() {
        try {
            this.write.lock();
            for (String key : this.getPropertyKeys()) {
                this.wrappedPropertySet.removeProperty(key);
            }
            this.wrappedPropertySet.removeProperty(Property.KEYS.key());
        }
        finally {
            this.write.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object removeProperty(String key) {
        Object removedValue = this.wrappedPropertySet.removeProperty(this.checkNotReserved(key));
        if (removedValue != null) {
            try {
                this.write.lock();
                List<String> keys = this.getPropertyKeys();
                keys.remove(key);
                this.setPropertyKeys(keys);
            }
            finally {
                this.write.unlock();
            }
        }
        return removedValue;
    }

    private List<String> getPropertyKeys() {
        ArrayList list = (ArrayList)this.wrappedPropertySet.getProperty(Property.KEYS.key());
        if (list == null) {
            list = new ArrayList();
        }
        return list;
    }

    private void setPropertyKeys(List<String> keys) {
        this.wrappedPropertySet.putProperty(Property.KEYS.key(), keys);
    }

    private static enum Property {
        KEYS("properties");

        private final String key;

        private Property(String key) {
            this.key = key;
        }

        String key() {
            return this.key;
        }
    }
}

