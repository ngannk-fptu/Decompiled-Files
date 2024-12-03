/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.oracle.webservices.api.message;

import com.oracle.webservices.api.message.BasePropertySet;
import com.oracle.webservices.api.message.DistributedPropertySet;
import com.oracle.webservices.api.message.MessageContext;
import com.oracle.webservices.api.message.PropertySet;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public abstract class BaseDistributedPropertySet
extends BasePropertySet
implements DistributedPropertySet {
    private final Map<Class<? extends PropertySet>, PropertySet> satellites = new IdentityHashMap<Class<? extends PropertySet>, PropertySet>();
    private final Map<String, Object> viewthis = super.createView();

    @Override
    public void addSatellite(@NotNull PropertySet satellite) {
        this.addSatellite(satellite.getClass(), satellite);
    }

    @Override
    public void addSatellite(@NotNull Class<? extends PropertySet> keyClass, @NotNull PropertySet satellite) {
        this.satellites.put(keyClass, satellite);
    }

    @Override
    public void removeSatellite(PropertySet satellite) {
        this.satellites.remove(satellite.getClass());
    }

    public void copySatelliteInto(@NotNull DistributedPropertySet r) {
        for (Map.Entry<Class<? extends PropertySet>, PropertySet> entry : this.satellites.entrySet()) {
            r.addSatellite(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void copySatelliteInto(MessageContext r) {
        this.copySatelliteInto((DistributedPropertySet)r);
    }

    @Override
    @Nullable
    public <T extends PropertySet> T getSatellite(Class<T> satelliteClass) {
        PropertySet satellite = this.satellites.get(satelliteClass);
        if (satellite != null) {
            return (T)satellite;
        }
        for (PropertySet child : this.satellites.values()) {
            if (satelliteClass.isInstance(child)) {
                return (T)((PropertySet)satelliteClass.cast(child));
            }
            if (!DistributedPropertySet.class.isInstance(child) || (satellite = ((DistributedPropertySet)DistributedPropertySet.class.cast(child)).getSatellite(satelliteClass)) == null) continue;
            return (T)satellite;
        }
        return null;
    }

    @Override
    public Map<Class<? extends PropertySet>, PropertySet> getSatellites() {
        return this.satellites;
    }

    @Override
    public Object get(Object key) {
        for (PropertySet child : this.satellites.values()) {
            if (!child.supports(key)) continue;
            return child.get(key);
        }
        return super.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        for (PropertySet child : this.satellites.values()) {
            if (!child.supports(key)) continue;
            return child.put(key, value);
        }
        return super.put(key, value);
    }

    @Override
    public boolean containsKey(Object key) {
        if (this.viewthis.containsKey(key)) {
            return true;
        }
        for (PropertySet child : this.satellites.values()) {
            if (!child.containsKey(key)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean supports(Object key) {
        for (PropertySet child : this.satellites.values()) {
            if (!child.supports(key)) continue;
            return true;
        }
        return super.supports(key);
    }

    @Override
    public Object remove(Object key) {
        for (PropertySet child : this.satellites.values()) {
            if (!child.supports(key)) continue;
            return child.remove(key);
        }
        return super.remove(key);
    }

    @Override
    protected void createEntrySet(Set<Map.Entry<String, Object>> core) {
        super.createEntrySet(core);
        for (PropertySet child : this.satellites.values()) {
            ((BasePropertySet)child).createEntrySet(core);
        }
    }

    protected Map<String, Object> asMapLocal() {
        return this.viewthis;
    }

    protected boolean supportsLocal(Object key) {
        return super.supports(key);
    }

    @Override
    protected Map<String, Object> createView() {
        return new DistributedMapView();
    }

    class DistributedMapView
    extends AbstractMap<String, Object> {
        DistributedMapView() {
        }

        @Override
        public Object get(Object key) {
            for (PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (!child.supports(key)) continue;
                return child.get(key);
            }
            return BaseDistributedPropertySet.this.viewthis.get(key);
        }

        @Override
        public int size() {
            int size = BaseDistributedPropertySet.this.viewthis.size();
            for (PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                size += child.asMap().size();
            }
            return size;
        }

        @Override
        public boolean containsKey(Object key) {
            if (BaseDistributedPropertySet.this.viewthis.containsKey(key)) {
                return true;
            }
            for (PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (!child.asMap().containsKey(key)) continue;
                return true;
            }
            return false;
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            HashSet<Map.Entry<String, Object>> entries = new HashSet<Map.Entry<String, Object>>();
            for (PropertySet propertySet : BaseDistributedPropertySet.this.satellites.values()) {
                for (Map.Entry<String, Object> entry : propertySet.asMap().entrySet()) {
                    entries.add(new AbstractMap.SimpleImmutableEntry<String, Object>(entry.getKey(), entry.getValue()));
                }
            }
            for (Map.Entry entry : BaseDistributedPropertySet.this.viewthis.entrySet()) {
                entries.add(new AbstractMap.SimpleImmutableEntry((String)entry.getKey(), entry.getValue()));
            }
            return entries;
        }

        @Override
        public Object put(String key, Object value) {
            for (PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (!child.supports(key)) continue;
                return child.put(key, value);
            }
            return BaseDistributedPropertySet.this.viewthis.put(key, value);
        }

        @Override
        public void clear() {
            BaseDistributedPropertySet.this.satellites.clear();
            BaseDistributedPropertySet.this.viewthis.clear();
        }

        @Override
        public Object remove(Object key) {
            for (PropertySet child : BaseDistributedPropertySet.this.satellites.values()) {
                if (!child.supports(key)) continue;
                return child.remove(key);
            }
            return BaseDistributedPropertySet.this.viewthis.remove(key);
        }
    }
}

