/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Map;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.PropertySource;
import javax.media.jai.PropertySourceChangeEvent;
import javax.media.jai.PropertySourceImpl;
import javax.media.jai.WritablePropertySource;
import javax.media.jai.util.CaselessStringKey;

public class WritablePropertySourceImpl
extends PropertySourceImpl
implements WritablePropertySource {
    protected PropertyChangeSupportJAI manager = null;

    public WritablePropertySourceImpl() {
    }

    public WritablePropertySourceImpl(Map propertyMap, PropertySource source, PropertyChangeSupportJAI manager) {
        super(propertyMap, source);
        this.manager = manager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getProperty(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Map map = this.properties;
        synchronized (map) {
            boolean isMapped = this.properties.containsKey(new CaselessStringKey(propertyName));
            Object value = super.getProperty(propertyName);
            if (this.manager != null && !isMapped && value != Image.UndefinedProperty) {
                Object eventSource = this.manager.getPropertyChangeEventSource();
                PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, propertyName, Image.UndefinedProperty, value);
                this.manager.firePropertyChange(evt);
            }
            return value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setProperty(String propertyName, Object propertyValue) {
        if (propertyName == null || propertyValue == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Map map = this.properties;
        synchronized (map) {
            CaselessStringKey key = new CaselessStringKey(propertyName);
            Object oldValue = this.properties.put(key, propertyValue);
            if (oldValue == null) {
                oldValue = Image.UndefinedProperty;
            }
            this.cachedPropertyNames.remove(key);
            if (this.manager != null && !oldValue.equals(propertyValue)) {
                Object eventSource = this.manager.getPropertyChangeEventSource();
                PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, propertyName, oldValue, propertyValue);
                this.manager.firePropertyChange(evt);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeProperty(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Map map = this.properties;
        synchronized (map) {
            CaselessStringKey key = new CaselessStringKey(propertyName);
            Object oldValue = this.properties.remove(key);
            this.propertySources.remove(key);
            this.cachedPropertyNames.remove(key);
            if (this.manager != null && oldValue != null) {
                Object eventSource = this.manager.getPropertyChangeEventSource();
                PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, propertyName, oldValue, Image.UndefinedProperty);
                this.manager.firePropertyChange(evt);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addProperties(Map propertyMap) {
        if (propertyMap != null) {
            Map map = this.properties;
            synchronized (map) {
                Iterator keys = propertyMap.keySet().iterator();
                while (keys.hasNext()) {
                    Object key = keys.next();
                    if (key instanceof String) {
                        this.setProperty((String)key, propertyMap.get(key));
                        continue;
                    }
                    if (!(key instanceof CaselessStringKey)) continue;
                    this.setProperty(((CaselessStringKey)key).getName(), propertyMap.get(key));
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addProperties(PropertySource propertySource) {
        if (propertySource != null) {
            Map map = this.properties;
            synchronized (map) {
                String[] names = propertySource.getPropertyNames();
                if (names != null) {
                    int length = names.length;
                    for (int i = 0; i < length; ++i) {
                        this.propertySources.put(new CaselessStringKey(names[i]), propertySource);
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearProperties() {
        Map map = this.properties;
        synchronized (map) {
            String[] names = this.getPropertyNames();
            if (names != null) {
                int length = names.length;
                for (int i = 0; i < length; ++i) {
                    this.removeProperty(names[i]);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearPropertyMap() {
        Map map = this.properties;
        synchronized (map) {
            Iterator keys = this.properties.keySet().iterator();
            while (keys.hasNext()) {
                CaselessStringKey key = (CaselessStringKey)keys.next();
                Object oldValue = this.properties.get(key);
                keys.remove();
                if (this.manager == null) continue;
                Object eventSource = this.manager.getPropertyChangeEventSource();
                PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, key.getName(), oldValue, Image.UndefinedProperty);
                this.manager.firePropertyChange(evt);
            }
            this.cachedPropertyNames.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearPropertySourceMap() {
        Map map = this.properties;
        synchronized (map) {
            this.propertySources.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearCachedProperties() {
        Map map = this.properties;
        synchronized (map) {
            Iterator names = this.cachedPropertyNames.iterator();
            while (names.hasNext()) {
                CaselessStringKey name = (CaselessStringKey)names.next();
                Object oldValue = this.properties.remove(name);
                names.remove();
                if (this.manager == null) continue;
                Object eventSource = this.manager.getPropertyChangeEventSource();
                PropertySourceChangeEvent evt = new PropertySourceChangeEvent(eventSource, name.getName(), oldValue, Image.UndefinedProperty);
                this.manager.firePropertyChange(evt);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePropertySource(PropertySource propertySource) {
        Map map = this.properties;
        synchronized (map) {
            Iterator keys = this.propertySources.keySet().iterator();
            while (keys.hasNext()) {
                Object ps = this.propertySources.get(keys.next());
                if (!ps.equals(propertySource)) continue;
                keys.remove();
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.getEventManager().addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.getEventManager().addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.getEventManager().removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.getEventManager().removePropertyChangeListener(propertyName, listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PropertyChangeSupportJAI getEventManager() {
        if (this.manager == null) {
            WritablePropertySourceImpl writablePropertySourceImpl = this;
            synchronized (writablePropertySourceImpl) {
                this.manager = new PropertyChangeSupportJAI(this);
            }
        }
        return this.manager;
    }
}

