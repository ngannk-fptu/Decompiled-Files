/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.RegistryMode;
import javax.media.jai.util.CaselessStringKey;

class FactoryCache {
    final String modeName;
    final RegistryMode mode;
    final Class factoryClass;
    final Method factoryMethod;
    final boolean arePreferencesSupported;
    private Hashtable instances;
    private Hashtable instancesByName;
    private int count = 0;
    private Hashtable prefs;

    FactoryCache(String modeName) {
        this.modeName = modeName;
        this.mode = RegistryMode.getMode(modeName);
        this.factoryClass = this.mode.getFactoryClass();
        this.factoryMethod = this.mode.getFactoryMethod();
        this.arePreferencesSupported = this.mode.arePreferencesSupported();
        this.instances = new Hashtable();
        if (this.arePreferencesSupported) {
            this.instancesByName = new Hashtable();
            this.prefs = new Hashtable();
        }
    }

    Object invoke(Object factoryInstance, Object[] parameterValues) throws InvocationTargetException, IllegalAccessException {
        return this.factoryMethod.invoke(factoryInstance, parameterValues);
    }

    void addFactory(String descriptorName, String productName, Object factoryInstance) {
        this.checkInstance(factoryInstance);
        if (this.arePreferencesSupported) {
            if (productName == null) {
                throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
            }
            Vector<String> v = new Vector<String>();
            v.add(factoryInstance.getClass().getName());
            v.add(productName);
            v.add(descriptorName);
            CaselessStringKey fileName = new CaselessStringKey(this.modeName + this.count);
            this.instancesByName.put(factoryInstance, fileName);
            this.instances.put(fileName, v);
            ++this.count;
        } else {
            this.instances.put(new CaselessStringKey(descriptorName), factoryInstance);
        }
    }

    void removeFactory(String descriptorName, String productName, Object factoryInstance) {
        this.checkInstance(factoryInstance);
        this.checkRegistered(descriptorName, productName, factoryInstance);
        if (this.arePreferencesSupported) {
            CaselessStringKey fileName = (CaselessStringKey)this.instancesByName.get(factoryInstance);
            this.instancesByName.remove(factoryInstance);
            this.instances.remove(fileName);
            --this.count;
        } else {
            this.instances.remove(new CaselessStringKey(descriptorName));
        }
    }

    void setPreference(String descriptorName, String productName, Object preferredOp, Object otherOp) {
        Vector<Object[]> pv;
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[]{this.modeName}));
        }
        if (preferredOp == null || otherOp == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.checkRegistered(descriptorName, productName, preferredOp);
        this.checkRegistered(descriptorName, productName, otherOp);
        if (preferredOp == otherOp) {
            return;
        }
        this.checkInstance(preferredOp);
        this.checkInstance(otherOp);
        CaselessStringKey dn = new CaselessStringKey(descriptorName);
        CaselessStringKey pn = new CaselessStringKey(productName);
        Hashtable<CaselessStringKey, Vector<Object[]>> dht = (Hashtable<CaselessStringKey, Vector<Object[]>>)this.prefs.get(dn);
        if (dht == null) {
            dht = new Hashtable<CaselessStringKey, Vector<Object[]>>();
            this.prefs.put(dn, dht);
        }
        if ((pv = (Vector<Object[]>)dht.get(pn)) == null) {
            pv = new Vector<Object[]>();
            dht.put(pn, pv);
        }
        pv.addElement(new Object[]{preferredOp, otherOp});
    }

    void unsetPreference(String descriptorName, String productName, Object preferredOp, Object otherOp) {
        Vector pv;
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[]{this.modeName}));
        }
        if (preferredOp == null || otherOp == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.checkRegistered(descriptorName, productName, preferredOp);
        this.checkRegistered(descriptorName, productName, otherOp);
        if (preferredOp == otherOp) {
            return;
        }
        this.checkInstance(preferredOp);
        this.checkInstance(otherOp);
        Hashtable dht = (Hashtable)this.prefs.get(new CaselessStringKey(descriptorName));
        boolean found = false;
        if (dht != null && (pv = (Vector)dht.get(new CaselessStringKey(productName))) != null) {
            Iterator it = pv.iterator();
            while (it.hasNext()) {
                Object[] objs = (Object[])it.next();
                if (objs[0] != preferredOp || objs[1] != otherOp) continue;
                it.remove();
                found = true;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache2", new Object[]{preferredOp.getClass().getName(), otherOp.getClass().getName(), this.modeName, descriptorName, productName}));
        }
    }

    Object[][] getPreferences(String descriptorName, String productName) {
        Vector pv;
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[]{this.modeName}));
        }
        if (descriptorName == null || productName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        Hashtable dht = (Hashtable)this.prefs.get(new CaselessStringKey(descriptorName));
        if (dht != null && (pv = (Vector)dht.get(new CaselessStringKey(productName))) != null) {
            return (Object[][])pv.toArray((T[])new Object[0][]);
        }
        return null;
    }

    void clearPreferences(String descriptorName, String productName) {
        if (!this.arePreferencesSupported) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache1", new Object[]{this.modeName}));
        }
        Hashtable dht = (Hashtable)this.prefs.get(new CaselessStringKey(descriptorName));
        if (dht != null) {
            dht.remove(new CaselessStringKey(productName));
        }
    }

    List getFactoryList(String descriptorName, String productName) {
        if (this.arePreferencesSupported) {
            ArrayList list = new ArrayList();
            Enumeration keys = this.instancesByName.keys();
            while (keys.hasMoreElements()) {
                Object instance = keys.nextElement();
                CaselessStringKey fileName = (CaselessStringKey)this.instancesByName.get(instance);
                Vector v = (Vector)this.instances.get(fileName);
                String dn = (String)v.get(2);
                String pn = (String)v.get(1);
                if (!descriptorName.equalsIgnoreCase(dn) || !productName.equalsIgnoreCase(pn)) continue;
                list.add(instance);
            }
            return list;
        }
        Object obj = this.instances.get(new CaselessStringKey(descriptorName));
        ArrayList list = new ArrayList(1);
        list.add(obj);
        return list;
    }

    String getLocalName(Object factoryInstance) {
        CaselessStringKey fileName = (CaselessStringKey)this.instancesByName.get(factoryInstance);
        if (fileName != null) {
            return fileName.getName();
        }
        return null;
    }

    private boolean checkInstance(Object factoryInstance) {
        if (!this.factoryClass.isInstance(factoryInstance)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache0", new Object[]{factoryInstance.getClass().getName(), this.modeName, this.factoryClass.getName()}));
        }
        return true;
    }

    private void checkRegistered(String descriptorName, String productName, Object factoryInstance) {
        if (this.arePreferencesSupported) {
            if (productName == null) {
                throw new IllegalArgumentException("productName : " + JaiI18N.getString("Generic0"));
            }
            CaselessStringKey fileName = (CaselessStringKey)this.instancesByName.get(factoryInstance);
            if (fileName != null) {
                Vector v = (Vector)this.instances.get(fileName);
                String pn = (String)v.get(1);
                String dn = (String)v.get(2);
                if (dn != null && dn.equalsIgnoreCase(descriptorName) && pn != null && pn.equalsIgnoreCase(productName)) {
                    return;
                }
            }
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache3", new Object[]{factoryInstance.getClass().getName(), descriptorName, productName}));
        }
        CaselessStringKey key = new CaselessStringKey(descriptorName);
        if (factoryInstance != this.instances.get(key)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("FactoryCache4", new Object[]{factoryInstance.getClass().getName(), descriptorName}));
        }
    }
}

