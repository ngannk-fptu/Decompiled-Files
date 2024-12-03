/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertySource;
import javax.media.jai.util.CaselessStringKey;

public class PropertySourceImpl
implements PropertySource,
Serializable {
    protected transient Map properties = new Hashtable();
    protected transient Map propertySources = new Hashtable();
    protected Set cachedPropertyNames = Collections.synchronizedSet(new HashSet());

    protected PropertySourceImpl() {
    }

    public PropertySourceImpl(Map propertyMap, PropertySource propertySource) {
        this();
        String[] names;
        if (propertyMap == null && propertySource == null) {
            boolean throwException = false;
            try {
                Class<?> rootClass = Class.forName("javax.media.jai.PropertySourceImpl");
                throwException = this.getClass().equals(rootClass);
            }
            catch (Exception e) {
                // empty catch block
            }
            if (throwException) {
                throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
            }
        }
        if (propertyMap != null) {
            Iterator keys = propertyMap.keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                if (key instanceof String) {
                    this.properties.put(new CaselessStringKey((String)key), propertyMap.get(key));
                    continue;
                }
                if (!(key instanceof CaselessStringKey)) continue;
                this.properties.put((CaselessStringKey)key, propertyMap.get(key));
            }
        }
        if (propertySource != null && (names = propertySource.getPropertyNames()) != null) {
            int length = names.length;
            for (int i = 0; i < length; ++i) {
                this.propertySources.put(new CaselessStringKey(names[i]), propertySource);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String[] getPropertyNames() {
        Map map = this.properties;
        synchronized (map) {
            if (this.properties.size() + this.propertySources.size() == 0) {
                return null;
            }
            Set propertyNames = Collections.synchronizedSet(new HashSet(this.properties.keySet()));
            propertyNames.addAll(this.propertySources.keySet());
            int length = propertyNames.size();
            String[] names = new String[length];
            Iterator elements = propertyNames.iterator();
            int index = 0;
            while (elements.hasNext() && index < length) {
                names[index++] = ((CaselessStringKey)elements.next()).getName();
            }
            return names;
        }
    }

    public String[] getPropertyNames(String prefix) {
        return PropertyUtil.getPropertyNames(this.getPropertyNames(), prefix);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Class getPropertyClass(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Map map = this.properties;
        synchronized (map) {
            Class<?> propertyClass = null;
            Object value = this.properties.get(new CaselessStringKey(propertyName));
            if (value != null) {
                propertyClass = value.getClass();
            }
            return propertyClass;
        }
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
            CaselessStringKey key = new CaselessStringKey(propertyName);
            Object value = this.properties.get(key);
            if (value == null) {
                PropertySource propertySource = (PropertySource)this.propertySources.get(key);
                if (propertySource != null) {
                    value = propertySource.getProperty(propertyName);
                    if (value != Image.UndefinedProperty) {
                        this.properties.put(key, value);
                        this.cachedPropertyNames.add(key);
                    }
                } else {
                    value = Image.UndefinedProperty;
                }
            }
            return value;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map getProperties() {
        if (this.properties.size() + this.propertySources.size() == 0) {
            return null;
        }
        Map map = this.properties;
        synchronized (map) {
            Hashtable<String, Object> props = null;
            String[] propertyNames = this.getPropertyNames();
            if (propertyNames != null) {
                int length = propertyNames.length;
                props = new Hashtable<String, Object>(this.properties.size());
                for (int i = 0; i < length; ++i) {
                    String name = propertyNames[i];
                    Object value = this.getProperty(name);
                    props.put(name, value);
                }
            }
            return props;
        }
    }

    private static void writeMap(ObjectOutputStream out, Map map) throws IOException {
        Hashtable table = new Hashtable();
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = map.get(key);
            if (!(value instanceof Serializable)) continue;
            table.put(key, value);
        }
        out.writeObject(table);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        Map map = this.properties;
        synchronized (map) {
            PropertySourceImpl.writeMap(out, this.properties);
            PropertySourceImpl.writeMap(out, this.propertySources);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.properties = (Map)in.readObject();
        this.propertySources = (Map)in.readObject();
        Iterator names = this.cachedPropertyNames.iterator();
        Set propertyNames = this.properties.keySet();
        while (names.hasNext()) {
            if (propertyNames.contains(names.next())) continue;
            names.remove();
        }
    }
}

