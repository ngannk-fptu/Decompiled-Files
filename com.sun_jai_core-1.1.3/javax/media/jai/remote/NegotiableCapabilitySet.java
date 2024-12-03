/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.NegotiableCapability;
import javax.media.jai.util.CaselessStringKey;

public class NegotiableCapabilitySet
implements Serializable {
    private Hashtable categories = new Hashtable();
    private boolean isPreference = false;

    public NegotiableCapabilitySet(boolean isPreference) {
        this.isPreference = isPreference;
    }

    public boolean isPreference() {
        return this.isPreference;
    }

    public void add(NegotiableCapability capability) {
        if (capability == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet0"));
        }
        if (this.isPreference != capability.isPreference()) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet1"));
        }
        SequentialMap map = this.getCategoryMap(capability.getCategory());
        map.put(capability);
    }

    public void remove(NegotiableCapability capability) {
        if (capability == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet0"));
        }
        SequentialMap map = this.getCategoryMap(capability.getCategory());
        map.remove(capability);
    }

    public List get(String category, String capabilityName) {
        if (category == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
        }
        if (capabilityName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet4"));
        }
        SequentialMap map = this.getCategoryMap(category);
        return map.getNCList(capabilityName);
    }

    public List get(String category) {
        if (category == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
        }
        SequentialMap map = this.getCategoryMap(category);
        Vector capNames = map.getCapabilityNames();
        Vector allNC = new Vector();
        Iterator e = capNames.iterator();
        while (e.hasNext()) {
            Vector curr = (Vector)map.getNCList((String)e.next());
            Iterator i = curr.iterator();
            while (i.hasNext()) {
                Object obj = i.next();
                if (allNC.contains(obj)) continue;
                allNC.add(obj);
            }
        }
        return allNC;
    }

    public List getCategories() {
        Vector<String> v = new Vector<String>();
        Enumeration e = this.categories.keys();
        while (e.hasMoreElements()) {
            CaselessStringKey key = (CaselessStringKey)e.nextElement();
            v.add(key.toString());
        }
        return v;
    }

    public List getCapabilityNames(String category) {
        if (category == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
        }
        SequentialMap map = this.getCategoryMap(category);
        Vector names = map.getCapabilityNames();
        return names;
    }

    public NegotiableCapabilitySet negotiate(NegotiableCapabilitySet other) {
        if (other == null) {
            return null;
        }
        NegotiableCapabilitySet negotiated = new NegotiableCapabilitySet(this.isPreference & other.isPreference());
        Vector commonCategories = new Vector(this.getCategories());
        commonCategories.retainAll(other.getCategories());
        Iterator c = commonCategories.iterator();
        while (c.hasNext()) {
            String currCategory = (String)c.next();
            List thisCapabilities = this.get(currCategory);
            List otherCapabilities = other.get(currCategory);
            Iterator t = thisCapabilities.iterator();
            while (t.hasNext()) {
                NegotiableCapability thisCap = (NegotiableCapability)t.next();
                Iterator o = otherCapabilities.iterator();
                while (o.hasNext()) {
                    NegotiableCapability otherCap = (NegotiableCapability)o.next();
                    NegotiableCapability negCap = thisCap.negotiate(otherCap);
                    if (negCap == null) continue;
                    negotiated.add(negCap);
                }
            }
        }
        if (negotiated.isEmpty()) {
            return null;
        }
        return negotiated;
    }

    public NegotiableCapability getNegotiatedValue(String category) {
        if (category == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
        }
        List thisCapabilities = this.get(category);
        if (thisCapabilities.isEmpty()) {
            return null;
        }
        return (NegotiableCapability)thisCapabilities.get(0);
    }

    public NegotiableCapability getNegotiatedValue(NegotiableCapabilitySet other, String category) {
        if (other == null) {
            return null;
        }
        if (category == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet3"));
        }
        List thisCapabilities = this.get(category);
        List otherCapabilities = other.get(category);
        Iterator t = thisCapabilities.iterator();
        while (t.hasNext()) {
            NegotiableCapability thisCap = (NegotiableCapability)t.next();
            Iterator o = otherCapabilities.iterator();
            while (o.hasNext()) {
                NegotiableCapability otherCap = (NegotiableCapability)o.next();
                NegotiableCapability negCap = thisCap.negotiate(otherCap);
                if (negCap == null) continue;
                return negCap;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this.categories.isEmpty();
    }

    private SequentialMap getCategoryMap(String category) {
        CaselessStringKey categoryKey = new CaselessStringKey(category);
        SequentialMap map = (SequentialMap)this.categories.get(categoryKey);
        if (map == null) {
            map = new SequentialMap();
            this.categories.put(categoryKey, map);
        }
        return map;
    }

    class SequentialMap
    implements Serializable {
        Vector keys = new Vector();
        Vector values = new Vector();

        SequentialMap() {
        }

        void put(NegotiableCapability capability) {
            CaselessStringKey capNameKey = new CaselessStringKey(capability.getCapabilityName());
            int index = this.keys.indexOf(capNameKey);
            if (index == -1) {
                this.keys.add(capNameKey);
                Vector<NegotiableCapability> v = new Vector<NegotiableCapability>();
                v.add(capability);
                this.values.add(v);
            } else {
                Vector<NegotiableCapability> v = (Vector<NegotiableCapability>)this.values.elementAt(index);
                if (v == null) {
                    v = new Vector<NegotiableCapability>();
                }
                v.add(capability);
            }
        }

        List getNCList(String capabilityName) {
            CaselessStringKey capNameKey = new CaselessStringKey(capabilityName);
            int index = this.keys.indexOf(capNameKey);
            if (index == -1) {
                Vector v = new Vector();
                return v;
            }
            Vector v = (Vector)this.values.elementAt(index);
            return v;
        }

        void remove(NegotiableCapability capability) {
            CaselessStringKey capNameKey = new CaselessStringKey(capability.getCapabilityName());
            int index = this.keys.indexOf(capNameKey);
            if (index == -1) {
                throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet2"));
            }
            Vector v = (Vector)this.values.elementAt(index);
            if (!v.remove(capability)) {
                throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapabilitySet2"));
            }
            if (v.isEmpty()) {
                this.keys.remove(capNameKey);
                this.values.remove(index);
            }
            if (this.keys.isEmpty()) {
                NegotiableCapabilitySet.this.categories.remove(new CaselessStringKey(capability.getCategory()));
            }
        }

        Vector getCapabilityNames() {
            Vector<String> v = new Vector<String>();
            Iterator i = this.keys.iterator();
            while (i.hasNext()) {
                CaselessStringKey name = (CaselessStringKey)i.next();
                v.add(name.getName());
            }
            return v;
        }
    }
}

