/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.CaselessStringKeyHashtable;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;
import javax.media.jai.util.CaselessStringKey;

class PropertyEnvironment
implements PropertySource {
    Vector pg;
    Vector sources;
    private static final Object PRESENT = new Object();
    CaselessStringKeyHashtable suppressed;
    CaselessStringKeyHashtable sourceForProp;
    private Object op;
    private CaselessStringKeyHashtable propNames;
    private PropertySource defaultPropertySource = null;
    private boolean areDefaultsMapped = true;

    public PropertyEnvironment(Vector sources, Vector generators, Vector suppressed, Hashtable sourceForProp, Object op) {
        this.sources = sources;
        this.pg = generators == null ? null : (Vector)generators.clone();
        this.suppressed = new CaselessStringKeyHashtable();
        if (suppressed != null) {
            Enumeration e = suppressed.elements();
            while (e.hasMoreElements()) {
                this.suppressed.put(e.nextElement(), PRESENT);
            }
        }
        this.sourceForProp = sourceForProp == null ? null : new CaselessStringKeyHashtable((Map)sourceForProp);
        this.op = op;
        this.hashNames();
    }

    public String[] getPropertyNames() {
        this.mapDefaults();
        int count = 0;
        String[] names = new String[this.propNames.size()];
        Enumeration e = this.propNames.keys();
        while (e.hasMoreElements()) {
            names[count++] = ((CaselessStringKey)e.nextElement()).getName();
        }
        return names;
    }

    public String[] getPropertyNames(String prefix) {
        String[] propertyNames = this.getPropertyNames();
        return PropertyUtil.getPropertyNames(propertyNames, prefix);
    }

    public Class getPropertyClass(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }

    public Object getProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.mapDefaults();
        Object o = this.propNames.get(name);
        Object property = null;
        if (o == null) {
            return Image.UndefinedProperty;
        }
        if (o instanceof PropertyGenerator) {
            property = ((PropertyGenerator)o).getProperty(name, this.op);
        } else if (o instanceof Integer) {
            int srcIndex = (Integer)o;
            PropertySource src = (PropertySource)this.sources.elementAt(srcIndex);
            property = src.getProperty(name);
        } else if (o instanceof PropertySource) {
            property = ((PropertySource)o).getProperty(name);
        }
        return property;
    }

    public void copyPropertyFromSource(String propertyName, int sourceIndex) {
        PropertySource propertySource = (PropertySource)this.sources.elementAt(sourceIndex);
        this.propNames.put(propertyName, (Object)propertySource);
        this.suppressed.remove(propertyName);
    }

    public void suppressProperty(String propertyName) {
        this.suppressed.put(propertyName, PRESENT);
        this.hashNames();
    }

    public void addPropertyGenerator(PropertyGenerator generator) {
        if (this.pg == null) {
            this.pg = new Vector();
        }
        this.pg.addElement(generator);
        this.removeSuppressedProps(generator);
        this.hashNames();
    }

    public void setDefaultPropertySource(PropertySource ps) {
        if (ps == this.defaultPropertySource) {
            return;
        }
        if (this.defaultPropertySource != null) {
            this.hashNames();
        }
        this.areDefaultsMapped = false;
        this.defaultPropertySource = ps;
    }

    private void mapDefaults() {
        if (!this.areDefaultsMapped) {
            String[] names;
            this.areDefaultsMapped = true;
            if (this.defaultPropertySource != null && (names = this.defaultPropertySource.getPropertyNames()) != null) {
                int length = names.length;
                for (int i = 0; i < length; ++i) {
                    Object o;
                    if (this.suppressed.containsKey(names[i]) || (o = this.propNames.get(names[i])) != null && !(o instanceof Integer)) continue;
                    this.propNames.put(names[i], (Object)this.defaultPropertySource);
                }
            }
        }
    }

    private void removeSuppressedProps(PropertyGenerator generator) {
        String[] names = generator.getPropertyNames();
        for (int i = 0; i < names.length; ++i) {
            this.suppressed.remove(names[i]);
        }
    }

    private void hashNames() {
        this.propNames = new CaselessStringKeyHashtable();
        if (this.sources != null) {
            for (int i = this.sources.size() - 1; i >= 0; --i) {
                PropertySource source;
                String[] propertyNames;
                Object o = this.sources.elementAt(i);
                if (!(o instanceof PropertySource) || (propertyNames = (source = (PropertySource)o).getPropertyNames()) == null) continue;
                for (int j = 0; j < propertyNames.length; ++j) {
                    String name = propertyNames[j];
                    if (this.suppressed.containsKey(name)) continue;
                    this.propNames.put(name, (Object)new Integer(i));
                }
            }
        }
        if (this.pg != null) {
            Iterator it = this.pg.iterator();
            while (it.hasNext()) {
                String[] propertyNames;
                PropertyGenerator generator = (PropertyGenerator)it.next();
                if (!generator.canGenerateProperties(this.op) || (propertyNames = generator.getPropertyNames()) == null) continue;
                for (int i = 0; i < propertyNames.length; ++i) {
                    String name = propertyNames[i];
                    if (this.suppressed.containsKey(name)) continue;
                    this.propNames.put(name, (Object)generator);
                }
            }
        }
        if (this.sourceForProp != null) {
            Enumeration e = this.sourceForProp.keys();
            while (e.hasMoreElements()) {
                CaselessStringKey name = (CaselessStringKey)e.nextElement();
                if (this.suppressed.containsKey(name)) continue;
                Integer i = (Integer)this.sourceForProp.get(name);
                PropertySource propertySource = (PropertySource)this.sources.elementAt(i);
                this.propNames.put(name, (Object)propertySource);
            }
        }
        this.areDefaultsMapped = false;
    }
}

