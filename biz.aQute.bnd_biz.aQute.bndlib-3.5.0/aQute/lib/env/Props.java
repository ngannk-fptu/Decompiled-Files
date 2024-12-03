/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.env;

import aQute.lib.collections.SortedList;
import aQute.lib.env.Header;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Props
implements Map<String, String> {
    static String EXTENDED = "[\\-0-9a-zA-Z\\._]+";
    private Map<String, String> map;
    static Map<String, String> EMPTY = Collections.emptyMap();
    public static Props EMPTY_ATTRS = new Props(new Props[0]);

    public Props(Props ... attrs) {
        for (Props a : attrs) {
            if (a == null) continue;
            this.putAll(a);
        }
    }

    @Override
    public void clear() {
        this.map.clear();
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
        return this.map.containsKey((String)name);
    }

    public boolean containsValue(String value) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        assert (value instanceof String);
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue((String)value);
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        if (this.map == null) {
            return EMPTY.entrySet();
        }
        return this.map.entrySet();
    }

    @Override
    @Deprecated
    public String get(Object key) {
        assert (key instanceof String);
        if (this.map == null) {
            return null;
        }
        return this.map.get((String)key);
    }

    public String get(String key) {
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public String get(String key, String deflt) {
        String s = this.get(key);
        if (s == null) {
            return deflt;
        }
        return s;
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
    public String put(String key, String value) {
        if (key == null) {
            return null;
        }
        if (this.map == null) {
            this.map = new LinkedHashMap<String, String>();
        }
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        for (Map.Entry<? extends String, ? extends String> e : map.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    @Deprecated
    public String remove(Object var0) {
        assert (var0 instanceof String);
        if (this.map == null) {
            return null;
        }
        return this.map.remove((String)var0);
    }

    public String remove(String var0) {
        if (this.map == null) {
            return null;
        }
        return this.map.remove(var0);
    }

    @Override
    public int size() {
        if (this.map == null) {
            return 0;
        }
        return this.map.size();
    }

    @Override
    public Collection<String> values() {
        if (this.map == null) {
            return EMPTY.values();
        }
        return this.map.values();
    }

    public String getVersion() {
        return this.get("version");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.append(sb);
        return sb.toString();
    }

    public void append(StringBuilder sb) {
        try {
            String del = "";
            for (Map.Entry<String, String> e : this.entrySet()) {
                sb.append(del);
                sb.append(e.getKey());
                sb.append("=");
                Header.quote(sb, e.getValue());
                del = ";";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean isEqual(Props other) {
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
            String valueo;
            String value = this.get(key);
            if (value == (valueo = other.get(key)) || value != null && value.equals(valueo)) continue;
            return false;
        }
        return true;
    }

    static {
        Props.EMPTY_ATTRS.map = Collections.emptyMap();
    }
}

