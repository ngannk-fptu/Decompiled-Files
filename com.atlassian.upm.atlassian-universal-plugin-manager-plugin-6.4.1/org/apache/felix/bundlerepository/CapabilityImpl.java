/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.felix.bundlerepository.PropertyImpl;
import org.osgi.service.obr.Capability;

public class CapabilityImpl
implements Capability {
    private String m_name = null;
    private Map m_map = new TreeMap(new Comparator(){

        public int compare(Object o1, Object o2) {
            return o1.toString().compareToIgnoreCase(o2.toString());
        }
    });

    public String getName() {
        return this.m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public Map getProperties() {
        return this.m_map;
    }

    protected void addP(PropertyImpl prop) {
        this.m_map.put(prop.getN(), prop.getV());
    }

    protected void addP(String name, Object value) {
        this.m_map.put(name, value);
    }
}

