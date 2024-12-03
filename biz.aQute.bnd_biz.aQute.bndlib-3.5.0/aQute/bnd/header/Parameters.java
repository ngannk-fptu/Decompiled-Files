/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.header;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.lib.collections.SortedList;
import aQute.service.reporter.Reporter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Parameters
implements Map<String, Attrs> {
    private LinkedHashMap<String, Attrs> map;
    static Map<String, Attrs> EMPTY = Collections.emptyMap();
    String error;
    private final boolean allowDuplicateAttributes;

    public Parameters(boolean allowDuplicateAttributes) {
        this.allowDuplicateAttributes = allowDuplicateAttributes;
    }

    public Parameters() {
        this.allowDuplicateAttributes = false;
    }

    public Parameters(String header) {
        this.allowDuplicateAttributes = false;
        OSGiHeader.parseHeader(header, null, this);
    }

    public Parameters(String header, Reporter reporter) {
        this.allowDuplicateAttributes = false;
        OSGiHeader.parseHeader(header, reporter, this);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public void add(String key, Attrs attrs) {
        while (this.containsKey(key)) {
            key = key + "~";
        }
        this.put(key, attrs);
    }

    public boolean containsKey(String name) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    @Override
    @Deprecated
    public boolean containsKey(Object name) {
        assert (name instanceof String);
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    public boolean containsValue(Attrs value) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        assert (value instanceof Attrs);
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    public Set<Map.Entry<String, Attrs>> entrySet() {
        if (this.map == null) {
            return EMPTY.entrySet();
        }
        return this.map.entrySet();
    }

    @Override
    @Deprecated
    public Attrs get(Object key) {
        assert (key instanceof String);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public Attrs get(String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.map == null || this.map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        if (this.map == null) {
            return EMPTY.keySet();
        }
        return this.map.keySet();
    }

    @Override
    public Attrs put(String key, Attrs value) {
        assert (key != null);
        assert (value != null);
        if (this.map == null) {
            this.map = new LinkedHashMap();
        }
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Attrs> map) {
        if (this.map == null) {
            if (map.isEmpty()) {
                return;
            }
            this.map = new LinkedHashMap();
        }
        this.map.putAll(map);
    }

    public void putAllIfAbsent(Map<String, ? extends Attrs> map) {
        for (Map.Entry<String, ? extends Attrs> entry : map.entrySet()) {
            if (this.containsKey(entry.getKey())) continue;
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Deprecated
    public Attrs remove(Object var0) {
        assert (var0 instanceof String);
        if (this.map == null) {
            return null;
        }
        return (Attrs)this.map.remove(var0);
    }

    public Attrs remove(String var0) {
        if (this.map == null) {
            return null;
        }
        return (Attrs)this.map.remove(var0);
    }

    @Override
    public int size() {
        if (this.map == null) {
            return 0;
        }
        return this.map.size();
    }

    @Override
    public Collection<Attrs> values() {
        if (this.map == null) {
            return EMPTY.values();
        }
        return this.map.values();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.append(sb);
        return sb.toString();
    }

    public void append(StringBuilder sb) {
        String del = "";
        for (Map.Entry<String, Attrs> s : this.entrySet()) {
            sb.append(del);
            sb.append(this.removeDuplicateMarker(s.getKey()));
            if (!s.getValue().isEmpty()) {
                sb.append(';');
                s.getValue().append(sb);
            }
            del = ",";
        }
    }

    private Object removeDuplicateMarker(String key) {
        while (key.endsWith("~")) {
            key = key.substring(0, key.length() - 1);
        }
        return key;
    }

    @Override
    @Deprecated
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    @Deprecated
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isEqual(Parameters other) {
        SortedList lo;
        if (this == other) {
            return true;
        }
        if (other == null || this.size() != other.size()) {
            return false;
        }
        if (this.isEmpty()) {
            return true;
        }
        SortedList l = new SortedList(this.keySet());
        if (!l.isEqual(lo = new SortedList(other.keySet()))) {
            return false;
        }
        for (String key : this.keySet()) {
            Attrs valueo;
            Attrs value = this.get(key);
            if (value == (valueo = other.get(key)) || value != null && value.isEqual(valueo)) continue;
            return false;
        }
        return true;
    }

    public Map<String, ? extends Map<String, String>> asMapMap() {
        return this;
    }

    public void mergeWith(Parameters other, boolean override) {
        for (Map.Entry<String, Attrs> e : other.entrySet()) {
            Attrs existing = this.get(e.getKey());
            if (existing == null) {
                this.put(e.getKey(), new Attrs(e.getValue()));
                continue;
            }
            existing.mergeWith(e.getValue(), override);
        }
    }

    public boolean allowDuplicateAttributes() {
        return this.allowDuplicateAttributes;
    }
}

