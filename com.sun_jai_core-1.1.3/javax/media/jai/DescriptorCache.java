/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationGraph;
import javax.media.jai.PartialOrderNode;
import javax.media.jai.ProductOperationGraph;
import javax.media.jai.PropertyEnvironment;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertyGeneratorFromSource;
import javax.media.jai.PropertySource;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.RegistryMode;
import javax.media.jai.util.CaselessStringKey;

class DescriptorCache {
    final String modeName;
    final RegistryMode mode;
    final boolean arePreferencesSupported;
    final boolean arePropertiesSupported;
    private Hashtable descriptorNames;
    private Hashtable products;
    private Hashtable productPrefs;
    private Hashtable properties;
    private Hashtable suppressed;
    private Hashtable sourceForProp;
    private Hashtable propNames;

    DescriptorCache(String modeName) {
        this.modeName = modeName;
        this.mode = RegistryMode.getMode(modeName);
        this.arePreferencesSupported = this.mode.arePreferencesSupported();
        this.arePropertiesSupported = this.mode.arePropertiesSupported();
        this.descriptorNames = new Hashtable();
        this.products = new Hashtable();
        if (this.arePreferencesSupported) {
            this.productPrefs = new Hashtable();
        }
        this.properties = new Hashtable();
        this.suppressed = new Hashtable();
        this.sourceForProp = new Hashtable();
        this.propNames = new Hashtable();
    }

    boolean addDescriptor(RegistryElementDescriptor rdesc) {
        if (rdesc == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        String descriptorName = rdesc.getName();
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (this.descriptorNames.containsKey(key)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache0", new Object[]{descriptorName, this.modeName}));
        }
        this.descriptorNames.put(key, rdesc);
        if (this.arePreferencesSupported) {
            this.products.put(key, new ProductOperationGraph());
        }
        if (!rdesc.arePropertiesSupported()) {
            return true;
        }
        PropertyGenerator[] props = rdesc.getPropertyGenerators(this.modeName);
        if (props != null) {
            for (int i = 0; i < props.length; ++i) {
                Vector v = (Vector)this.properties.get(key);
                if (v == null) {
                    v = new Vector();
                    v.addElement(props[i]);
                    this.properties.put(key, v);
                } else {
                    v.addElement(props[i]);
                }
                v = (Vector)this.suppressed.get(key);
                Hashtable h = (Hashtable)this.sourceForProp.get(key);
                String[] names = props[i].getPropertyNames();
                for (int j = 0; j < names.length; ++j) {
                    CaselessStringKey name = new CaselessStringKey(names[j]);
                    if (v != null) {
                        v.remove(name);
                    }
                    if (h == null) continue;
                    h.remove(name);
                }
            }
        }
        return true;
    }

    boolean removeDescriptor(String descriptorName) {
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (!this.descriptorNames.containsKey(key)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[]{descriptorName, this.modeName}));
        }
        RegistryElementDescriptor rdesc = (RegistryElementDescriptor)this.descriptorNames.get(key);
        PropertyGenerator[] props = null;
        if (rdesc.arePropertiesSupported()) {
            props = rdesc.getPropertyGenerators(this.modeName);
        }
        if (props != null) {
            for (int i = 0; i < props.length; ++i) {
                if (props[i] == null) {
                    throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache2", new Object[]{new Integer(i), descriptorName, this.modeName}));
                }
                Vector v = (Vector)this.properties.get(key);
                if (v == null) continue;
                v.removeElement(props[i]);
            }
        }
        this.descriptorNames.remove(key);
        if (this.arePreferencesSupported) {
            this.products.remove(key);
        }
        return true;
    }

    boolean removeDescriptor(RegistryElementDescriptor rdesc) {
        if (rdesc == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return this.removeDescriptor(rdesc.getName());
    }

    RegistryElementDescriptor getDescriptor(String descriptorName) {
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        return (RegistryElementDescriptor)this.descriptorNames.get(key);
    }

    List getDescriptors() {
        ArrayList list = new ArrayList();
        Enumeration en = this.descriptorNames.elements();
        while (en.hasMoreElements()) {
            list.add(en.nextElement());
        }
        return list;
    }

    String[] getDescriptorNames() {
        Enumeration e = this.descriptorNames.keys();
        int size = this.descriptorNames.size();
        String[] names = new String[size];
        for (int i = 0; i < size; ++i) {
            CaselessStringKey key = (CaselessStringKey)e.nextElement();
            names[i] = key.getName();
        }
        return names;
    }

    OperationGraph addProduct(String descriptorName, String productName) {
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (productName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ProductOperationGraph pog = (ProductOperationGraph)this.products.get(key);
        if (pog == null) {
            return null;
        }
        PartialOrderNode pon = pog.lookupOp(productName);
        if (pon == null) {
            pog.addProduct(productName);
            pon = pog.lookupOp(productName);
        }
        return (OperationGraph)pon.getData();
    }

    boolean removeProduct(String descriptorName, String productName) {
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (productName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ProductOperationGraph pog = (ProductOperationGraph)this.products.get(key);
        if (pog == null) {
            return false;
        }
        PartialOrderNode pon = pog.lookupOp(productName);
        if (pon == null) {
            return false;
        }
        pog.removeOp(productName);
        return true;
    }

    OperationGraph lookupProduct(String descriptorName, String productName) {
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (productName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ProductOperationGraph pog = (ProductOperationGraph)this.products.get(key);
        if (pog == null) {
            return null;
        }
        PartialOrderNode pon = pog.lookupOp(productName);
        if (pon == null) {
            return null;
        }
        return (OperationGraph)pon.getData();
    }

    boolean setProductPreference(String descriptorName, String preferredProductName, String otherProductName) {
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[]{this.modeName}));
        }
        if (descriptorName == null || preferredProductName == null || otherProductName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (preferredProductName.equalsIgnoreCase(otherProductName)) {
            return false;
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (!this.descriptorNames.containsKey(key)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[]{descriptorName, this.modeName}));
        }
        ProductOperationGraph og = (ProductOperationGraph)this.products.get(key);
        if (og == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache3", new Object[]{descriptorName, this.modeName}));
        }
        if (og.lookupOp(preferredProductName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[]{descriptorName, this.modeName, preferredProductName}));
        }
        if (og.lookupOp(otherProductName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[]{descriptorName, this.modeName, otherProductName}));
        }
        og.setPreference(preferredProductName, otherProductName);
        String[] prefs = new String[]{preferredProductName, otherProductName};
        if (!this.productPrefs.containsKey(key)) {
            Vector<String[]> v = new Vector<String[]>();
            v.addElement(prefs);
            this.productPrefs.put(key, v);
        } else {
            Vector v = (Vector)this.productPrefs.get(key);
            v.addElement(prefs);
        }
        return true;
    }

    boolean unsetProductPreference(String descriptorName, String preferredProductName, String otherProductName) {
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[]{this.modeName}));
        }
        if (descriptorName == null || preferredProductName == null || otherProductName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (preferredProductName.equalsIgnoreCase(otherProductName)) {
            return false;
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (!this.descriptorNames.containsKey(key)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[]{descriptorName, this.modeName}));
        }
        ProductOperationGraph og = (ProductOperationGraph)this.products.get(key);
        if (og == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache3", new Object[]{descriptorName, this.modeName}));
        }
        if (og.lookupOp(preferredProductName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[]{descriptorName, this.modeName, preferredProductName}));
        }
        if (og.lookupOp(otherProductName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[]{descriptorName, this.modeName, otherProductName}));
        }
        og.unsetPreference(preferredProductName, otherProductName);
        if (!this.productPrefs.containsKey(key)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache5", new Object[]{descriptorName, this.modeName}));
        }
        Vector v = (Vector)this.productPrefs.get(key);
        Iterator it = v.iterator();
        while (it.hasNext()) {
            String[] prefs = (String[])it.next();
            if (!prefs[0].equalsIgnoreCase(preferredProductName) || !prefs[1].equalsIgnoreCase(otherProductName)) continue;
            it.remove();
            break;
        }
        return true;
    }

    boolean clearProductPreferences(String descriptorName) {
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (!this.descriptorNames.containsKey(key)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[]{descriptorName, this.modeName}));
        }
        ProductOperationGraph og = (ProductOperationGraph)this.products.get(key);
        if (og == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache3", new Object[]{descriptorName, this.modeName}));
        }
        if (!this.productPrefs.containsKey(key)) {
            return true;
        }
        Vector v = (Vector)this.productPrefs.get(key);
        Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            String[] prefs = (String[])e.nextElement();
            String pref = prefs[0];
            String other = prefs[1];
            if (og.lookupOp(pref) == null) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[]{descriptorName, this.modeName, pref}));
            }
            if (og.lookupOp(other) == null) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache4", new Object[]{descriptorName, this.modeName, other}));
            }
            og.unsetPreference(pref, other);
        }
        this.productPrefs.remove(key);
        return true;
    }

    String[][] getProductPreferences(String descriptorName) {
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache6", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (!this.productPrefs.containsKey(key)) {
            return null;
        }
        Vector v = (Vector)this.productPrefs.get(key);
        int s = v.size();
        if (s == 0) {
            return null;
        }
        String[][] productPreferences = new String[s][2];
        int count = 0;
        Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            String[] o = (String[])e.nextElement();
            productPreferences[count][0] = o[0];
            productPreferences[count++][1] = o[1];
        }
        return productPreferences;
    }

    Vector getOrderedProductList(String descriptorName) {
        if (!this.arePreferencesSupported) {
            return null;
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (!this.descriptorNames.containsKey(key)) {
            return null;
        }
        ProductOperationGraph productGraph = (ProductOperationGraph)this.products.get(key);
        if (productGraph == null) {
            return null;
        }
        Vector v1 = productGraph.getOrderedOperationList();
        if (v1 == null) {
            return null;
        }
        int size = v1.size();
        if (size == 0) {
            return null;
        }
        Vector<String> v2 = new Vector<String>();
        for (int i = 0; i < size; ++i) {
            v2.addElement(((PartialOrderNode)v1.elementAt(i)).getName());
        }
        return v2;
    }

    private boolean arePropertiesSupported(String descriptorName) {
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        RegistryElementDescriptor rdesc = (RegistryElementDescriptor)this.descriptorNames.get(key);
        if (rdesc == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache1", new Object[]{descriptorName, this.modeName}));
        }
        return this.arePropertiesSupported;
    }

    void clearPropertyState() {
        if (!this.arePropertiesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        this.properties = new Hashtable();
        this.suppressed = new Hashtable();
    }

    void addPropertyGenerator(String descriptorName, PropertyGenerator generator) {
        if (descriptorName == null || generator == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!this.arePropertiesSupported(descriptorName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        Vector v = (Vector)this.properties.get(key);
        if (v == null) {
            v = new Vector();
            this.properties.put(key, v);
        }
        v.addElement(generator);
        v = (Vector)this.suppressed.get(key);
        Hashtable h = (Hashtable)this.sourceForProp.get(key);
        String[] names = generator.getPropertyNames();
        for (int j = 0; j < names.length; ++j) {
            CaselessStringKey name = new CaselessStringKey(names[j]);
            if (v != null) {
                v.remove(name);
            }
            if (h == null) continue;
            h.remove(name);
        }
    }

    private void hashNames(String descriptorName) {
        Hashtable htable;
        int i;
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        Vector c = (Vector)this.properties.get(key);
        Vector s = (Vector)this.suppressed.get(key);
        Hashtable<CaselessStringKey, PropertyGenerator> h = new Hashtable<CaselessStringKey, PropertyGenerator>();
        this.propNames.put(key, h);
        if (c != null) {
            Iterator it = c.iterator();
            while (it.hasNext()) {
                PropertyGenerator pg = (PropertyGenerator)it.next();
                String[] names = pg.getPropertyNames();
                for (i = 0; i < names.length; ++i) {
                    CaselessStringKey name = new CaselessStringKey(names[i]);
                    if (s != null && s.contains(name)) continue;
                    h.put(name, pg);
                }
            }
        }
        if ((htable = (Hashtable)this.sourceForProp.get(key)) != null) {
            Enumeration e = htable.keys();
            while (e.hasMoreElements()) {
                CaselessStringKey name = (CaselessStringKey)e.nextElement();
                i = (Integer)htable.get(name);
                PropertyGeneratorFromSource generator = new PropertyGeneratorFromSource(i, name.getName());
                h.put(name, generator);
            }
        }
    }

    void removePropertyGenerator(String descriptorName, PropertyGenerator generator) {
        if (descriptorName == null || generator == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!this.arePropertiesSupported(descriptorName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        Vector v = (Vector)this.properties.get(key);
        if (v != null) {
            v.removeElement(generator);
        }
    }

    void suppressProperty(String descriptorName, String propertyName) {
        if (descriptorName == null || propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!this.arePropertiesSupported(descriptorName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        CaselessStringKey propertyKey = new CaselessStringKey(propertyName);
        Vector<CaselessStringKey> v = (Vector<CaselessStringKey>)this.suppressed.get(key);
        if (v == null) {
            v = new Vector<CaselessStringKey>();
            this.suppressed.put(key, v);
        }
        v.addElement(propertyKey);
        Hashtable h = (Hashtable)this.sourceForProp.get(key);
        if (h != null) {
            h.remove(propertyKey);
        }
    }

    void suppressAllProperties(String descriptorName) {
        if (!this.arePropertiesSupported(descriptorName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        Vector v = (Vector)this.properties.get(key);
        if (v != null) {
            Iterator it = v.iterator();
            while (it.hasNext()) {
                PropertyGenerator pg = (PropertyGenerator)it.next();
                String[] propertyNames = pg.getPropertyNames();
                for (int i = 0; i < propertyNames.length; ++i) {
                    this.suppressProperty(descriptorName, propertyNames[i]);
                }
            }
        }
    }

    void copyPropertyFromSource(String descriptorName, String propertyName, int sourceIndex) {
        if (descriptorName == null || propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!this.arePropertiesSupported(descriptorName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        CaselessStringKey propertyKey = new CaselessStringKey(propertyName);
        Hashtable<CaselessStringKey, Integer> h = (Hashtable<CaselessStringKey, Integer>)this.sourceForProp.get(key);
        if (h == null) {
            h = new Hashtable<CaselessStringKey, Integer>();
            this.sourceForProp.put(key, h);
        }
        h.put(propertyKey, new Integer(sourceIndex));
        Vector v = (Vector)this.suppressed.get(key);
        if (v != null) {
            v.remove(propertyKey);
        }
    }

    String[] getGeneratedPropertyNames(String descriptorName) {
        if (!this.arePropertiesSupported(descriptorName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        this.hashNames(descriptorName);
        Hashtable h = (Hashtable)this.propNames.get(key);
        if (h != null && h.size() > 0) {
            String[] names = new String[h.size()];
            int count = 0;
            Enumeration e = h.keys();
            while (e.hasMoreElements()) {
                CaselessStringKey str = (CaselessStringKey)e.nextElement();
                names[count++] = str.getName();
            }
            return count > 0 ? names : null;
        }
        return null;
    }

    PropertySource getPropertySource(String descriptorName, Object op, Vector sources) {
        if (descriptorName == null || op == null || sources == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!this.arePropertiesSupported(descriptorName)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("DescriptorCache7", new Object[]{this.modeName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        Vector pg = (Vector)this.properties.get(key);
        Vector sp = (Vector)this.suppressed.get(key);
        Hashtable sfp = (Hashtable)this.sourceForProp.get(key);
        return new PropertyEnvironment(sources, pg, sp, sfp, op);
    }
}

