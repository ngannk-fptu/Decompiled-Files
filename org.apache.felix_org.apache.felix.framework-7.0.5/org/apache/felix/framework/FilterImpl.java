/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.felix.framework.ServiceRegistrationImpl;
import org.apache.felix.framework.capabilityset.CapabilitySet;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.wiring.BundleCapabilityImpl;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleRevision;

public class FilterImpl
implements Filter {
    private final SimpleFilter m_filter;

    public FilterImpl(String filterStr) throws InvalidSyntaxException {
        try {
            this.m_filter = SimpleFilter.parse(filterStr);
        }
        catch (Throwable th) {
            throw new InvalidSyntaxException(th.getMessage(), filterStr);
        }
    }

    public boolean match(ServiceReference sr) {
        if (sr instanceof ServiceRegistrationImpl.ServiceReferenceImpl) {
            return CapabilitySet.matches((ServiceRegistrationImpl.ServiceReferenceImpl)sr, this.m_filter);
        }
        return CapabilitySet.matches(new WrapperCapability(sr), this.m_filter);
    }

    @Override
    public boolean match(Dictionary<String, ?> dctnr) {
        return CapabilitySet.matches(new WrapperCapability(dctnr, false), this.m_filter);
    }

    @Override
    public boolean matchCase(Dictionary<String, ?> dctnr) {
        return CapabilitySet.matches(new WrapperCapability(dctnr, true), this.m_filter);
    }

    @Override
    public boolean matches(Map<String, ?> map) {
        return CapabilitySet.matches(new WrapperCapability(map), this.m_filter);
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return this.m_filter.toString();
    }

    private static class DictionaryToMap
    implements Map {
        private final Map m_map;
        private final Dictionary m_dict;

        public DictionaryToMap(Dictionary dict, boolean caseSensitive) {
            if (!caseSensitive) {
                this.m_dict = null;
                this.m_map = new StringMap();
                if (dict != null) {
                    Enumeration keys = dict.keys();
                    while (keys.hasMoreElements()) {
                        Object key = keys.nextElement();
                        if (this.m_map.get(key) == null) {
                            this.m_map.put(key, dict.get(key));
                            continue;
                        }
                        throw new IllegalArgumentException("Duplicate attribute: " + key.toString());
                    }
                }
            } else {
                this.m_dict = dict;
                this.m_map = null;
            }
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsKey(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean containsValue(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Object get(Object o) {
            if (this.m_dict != null) {
                return this.m_dict.get(o);
            }
            if (this.m_map != null) {
                return this.m_map.get(o);
            }
            return null;
        }

        public Object put(Object k, Object v) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Object remove(Object o) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void putAll(Map map) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Set<Object> keySet() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Collection<Object> values() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Set<Map.Entry<Object, Object>> entrySet() {
            return Collections.EMPTY_SET;
        }
    }

    static class WrapperCapability
    extends BundleCapabilityImpl {
        private final Map m_map;

        public WrapperCapability(Map map) {
            super(null, null, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
            this.m_map = map == null ? Collections.EMPTY_MAP : map;
        }

        public WrapperCapability(Dictionary dict, boolean caseSensitive) {
            super(null, null, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
            this.m_map = new DictionaryToMap(dict, caseSensitive);
        }

        public WrapperCapability(ServiceReference sr) {
            super(null, null, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
            this.m_map = new StringMap();
            for (String key : sr.getPropertyKeys()) {
                this.m_map.put(key, sr.getProperty(key));
            }
        }

        @Override
        public BundleRevision getRevision() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getNamespace() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<String, String> getDirectives() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<String, Object> getAttributes() {
            return this.m_map;
        }

        @Override
        public List<String> getUses() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}

