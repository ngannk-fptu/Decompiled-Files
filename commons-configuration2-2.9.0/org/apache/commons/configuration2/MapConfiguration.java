/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;

public class MapConfiguration
extends AbstractConfiguration
implements Cloneable {
    protected Map<String, Object> map;
    private boolean trimmingDisabled;

    public MapConfiguration(Map<String, ?> map) {
        this.map = map;
    }

    public MapConfiguration(Properties props) {
        this.map = MapConfiguration.convertPropertiesToMap(props);
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public boolean isTrimmingDisabled() {
        return this.trimmingDisabled;
    }

    public void setTrimmingDisabled(boolean trimmingDisabled) {
        this.trimmingDisabled = trimmingDisabled;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        Object value = this.map.get(key);
        if (value instanceof String) {
            Collection<String> list = this.getListDelimiterHandler().split((String)value, !this.isTrimmingDisabled());
            return list.size() > 1 ? list : list.iterator().next();
        }
        return value;
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        Object previousValue = this.getProperty(key);
        if (previousValue == null) {
            this.map.put(key, value);
        } else if (previousValue instanceof List) {
            ((List)previousValue).add(value);
        } else {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(previousValue);
            list.add(value);
            this.map.put(key, list);
        }
    }

    @Override
    protected boolean isEmptyInternal() {
        return this.map.isEmpty();
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.map.containsKey(key);
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.map.remove(key);
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return this.map.keySet().iterator();
    }

    @Override
    protected int sizeInternal() {
        return this.map.size();
    }

    @Override
    public Object clone() {
        try {
            Map clonedMap;
            MapConfiguration copy = (MapConfiguration)super.clone();
            copy.map = clonedMap = (Map)ConfigurationUtils.clone(this.map);
            copy.cloneInterpolator(this);
            return copy;
        }
        catch (CloneNotSupportedException cex) {
            throw new ConfigurationRuntimeException(cex);
        }
    }

    private static Map<String, Object> convertPropertiesToMap(Properties props) {
        Properties map = props;
        return map;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [map=" + this.map + ", trimmingDisabled=" + this.trimmingDisabled + "]";
    }
}

