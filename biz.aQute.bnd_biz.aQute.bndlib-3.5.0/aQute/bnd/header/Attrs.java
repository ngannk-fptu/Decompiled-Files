/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.header;

import aQute.bnd.header.OSGiHeader;
import aQute.bnd.version.Version;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Attrs
implements Map<String, String> {
    public static DataType<String> STRING = new DataType<String>(){

        @Override
        public Type type() {
            return Type.STRING;
        }
    };
    public static DataType<Long> LONG = new DataType<Long>(){

        @Override
        public Type type() {
            return Type.LONG;
        }
    };
    public static DataType<Double> DOUBLE = new DataType<Double>(){

        @Override
        public Type type() {
            return Type.DOUBLE;
        }
    };
    public static DataType<Version> VERSION = new DataType<Version>(){

        @Override
        public Type type() {
            return Type.VERSION;
        }
    };
    public static DataType<List<String>> LIST_STRING = new DataType<List<String>>(){

        @Override
        public Type type() {
            return Type.STRINGS;
        }
    };
    public static DataType<List<Long>> LIST_LONG = new DataType<List<Long>>(){

        @Override
        public Type type() {
            return Type.LONGS;
        }
    };
    public static DataType<List<Double>> LIST_DOUBLE = new DataType<List<Double>>(){

        @Override
        public Type type() {
            return Type.DOUBLES;
        }
    };
    public static DataType<List<Version>> LIST_VERSION = new DataType<List<Version>>(){

        @Override
        public Type type() {
            return Type.VERSIONS;
        }
    };
    static String EXTENDED = "[\\-0-9a-zA-Z\\._]+";
    static String SCALAR = "String|Version|Long|Double";
    static String LIST = "List\\s*<\\s*(" + SCALAR + ")\\s*>";
    public static final Pattern TYPED = Pattern.compile("\\s*(" + EXTENDED + ")\\s*:\\s*(" + SCALAR + "|" + LIST + ")\\s*");
    private Map<String, String> map;
    private Map<String, Type> types = new LinkedHashMap<String, Type>();
    static Map<String, String> EMPTY = Collections.emptyMap();
    public static Attrs EMPTY_ATTRS = new Attrs();

    public Attrs() {
    }

    public Attrs(Attrs ... attrs) {
        for (Attrs a : attrs) {
            if (a == null) continue;
            this.putAll(a);
            if (a.types == null) continue;
            this.types.putAll(a.types);
        }
    }

    public void putAllTyped(Map<String, Object> attrs) {
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            this.putTyped(key, value);
        }
    }

    public void putTyped(String key, Object value) {
        if (value == null) {
            this.put(key, null);
            return;
        }
        if (!(value instanceof String)) {
            Type type;
            if (value instanceof Collection) {
                value = ((Collection)value).toArray();
            }
            if (value.getClass().isArray()) {
                type = Type.STRINGS;
                int l = Array.getLength(value);
                StringBuilder sb = new StringBuilder();
                String del = "";
                boolean first = true;
                for (int i = 0; i < l; ++i) {
                    Object member = Array.get(value, i);
                    if (member == null) continue;
                    if (first) {
                        type = this.getObjectType(member).plural();
                        first = true;
                    }
                    sb.append(del);
                    sb.append(member);
                    for (int n = sb.length(); n < sb.length(); ++n) {
                        char c = sb.charAt(n);
                        if (c != '\\' && c != ',') continue;
                        sb.insert(n, '\\');
                        ++n;
                    }
                    del = ",";
                }
                value = sb;
            } else {
                type = this.getObjectType(value);
            }
            key = key + ":" + type.toString();
        }
        this.put(key, value.toString());
    }

    private Type getObjectType(Object member) {
        if (member instanceof Double || member instanceof Float) {
            return Type.DOUBLE;
        }
        if (member instanceof Number) {
            return Type.LONG;
        }
        if (member instanceof Version) {
            return Type.VERSION;
        }
        return Type.STRING;
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
        return this.map.containsKey(name);
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
        return this.map.containsValue(value);
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
        return this.map.get(key);
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
        Matcher m;
        if (key == null) {
            return null;
        }
        if (this.map == null) {
            this.map = new LinkedHashMap<String, String>();
        }
        if ((m = TYPED.matcher(key)).matches()) {
            key = m.group(1);
            String type = m.group(2);
            Type t = Type.STRING;
            if (type.startsWith("List")) {
                type = m.group(3);
                if ("String".equals(type)) {
                    t = Type.STRINGS;
                } else if ("Long".equals(type)) {
                    t = Type.LONGS;
                } else if ("Double".equals(type)) {
                    t = Type.DOUBLES;
                } else if ("Version".equals(type)) {
                    t = Type.VERSIONS;
                }
            } else if ("String".equals(type)) {
                t = Type.STRING;
            } else if ("Long".equals(type)) {
                t = Type.LONG;
            } else if ("Double".equals(type)) {
                t = Type.DOUBLE;
            } else if ("Version".equals(type)) {
                t = Type.VERSION;
            }
            this.types.put(key, t);
        }
        return this.map.put(key, value);
    }

    public Type getType(String key) {
        if (this.types == null) {
            return Type.STRING;
        }
        Type t = this.types.get(key);
        if (t == null) {
            return Type.STRING;
        }
        return t;
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
        return this.map.remove(var0);
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
                this.append(sb, e);
                del = ";";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void append(StringBuilder sb, Map.Entry<String, String> e) throws IOException {
        Type type;
        sb.append(e.getKey());
        if (this.types != null && (type = this.types.get(e.getKey())) != null) {
            sb.append(":").append((Object)type);
        }
        sb.append("=");
        OSGiHeader.quote(sb, e.getValue());
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

    public boolean isEqual(Attrs other) {
        TreeSet<String> lo;
        if (this == other) {
            return true;
        }
        if (other == null || this.size() != other.size()) {
            return false;
        }
        if (this.isEmpty()) {
            return true;
        }
        TreeSet<String> l = new TreeSet<String>(this.keySet());
        if (!l.equals(lo = new TreeSet<String>(other.keySet()))) {
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

    public Object getTyped(String adname) {
        String s = this.get(adname);
        if (s == null) {
            return null;
        }
        Type t = this.getType(adname);
        return Attrs.convert(t, s);
    }

    public <T> T getTyped(DataType<T> type, String adname) {
        String s = this.get(adname);
        if (s == null) {
            return null;
        }
        Type t = this.getType(adname);
        if (t != type.type()) {
            throw new IllegalArgumentException("For key " + adname + ", expected " + (Object)((Object)type.type()) + " but had a " + (Object)((Object)t) + ". Value is " + s);
        }
        return (T)Attrs.convert(t, s);
    }

    public static Type toType(String type) {
        for (Type t : Type.values()) {
            if (!t.toString.equals(type)) continue;
            return t;
        }
        return null;
    }

    public static Object convert(String t, String s) {
        if (s == null) {
            return null;
        }
        Type type = Attrs.toType(t);
        if (type == null) {
            return s;
        }
        return Attrs.convert(type, s);
    }

    public static Object convert(Type t, String s) {
        if (t.sub == null) {
            switch (t) {
                case STRING: {
                    return s;
                }
                case LONG: {
                    return Long.parseLong(s.trim());
                }
                case VERSION: {
                    return Version.parseVersion(s);
                }
                case DOUBLE: {
                    return Double.parseDouble(s.trim());
                }
                case DOUBLES: 
                case LONGS: 
                case STRINGS: 
                case VERSIONS: {
                    return null;
                }
            }
            return null;
        }
        ArrayList<Object> list = new ArrayList<Object>();
        List<String> split = Attrs.splitListAttribute(s);
        for (String p : split) {
            list.add(Attrs.convert(t.sub, p));
        }
        return list;
    }

    static List<String> splitListAttribute(String input) throws IllegalArgumentException {
        LinkedList<String> result = new LinkedList<String>();
        StringBuilder builder = new StringBuilder();
        block4: for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            switch (c) {
                case '\\': {
                    if (++i >= input.length()) {
                        throw new IllegalArgumentException("Trailing blackslash in multi-valued attribute value");
                    }
                    c = input.charAt(i);
                    builder.append(c);
                    continue block4;
                }
                case ',': {
                    result.add(builder.toString());
                    builder = new StringBuilder();
                    continue block4;
                }
                default: {
                    builder.append(c);
                }
            }
        }
        result.add(builder.toString());
        return result;
    }

    public void mergeWith(Attrs other, boolean override) {
        for (Map.Entry<String, String> e : other.entrySet()) {
            String local = this.get(e.getKey());
            if (!override && local != null) continue;
            this.put(e.getKey(), e.getValue());
        }
    }

    public static String toDirective(String key) {
        if (key == null || !key.endsWith(":")) {
            return null;
        }
        return key.substring(0, key.length() - 1);
    }

    public static Attrs create(String key, String value) {
        Attrs attrs = new Attrs();
        attrs.put(key, value);
        return attrs;
    }

    public Attrs with(String key, String value) {
        this.put(key, value);
        return this;
    }

    static {
        Attrs.EMPTY_ATTRS.map = Collections.emptyMap();
    }

    public static enum Type {
        STRING(null, "String"),
        LONG(null, "Long"),
        VERSION(null, "Version"),
        DOUBLE(null, "Double"),
        STRINGS(STRING, "List<String>"),
        LONGS(LONG, "List<Long>"),
        VERSIONS(VERSION, "List<Version>"),
        DOUBLES(DOUBLE, "List<Double>");

        Type sub;
        String toString;

        private Type(Type sub, String toString) {
            this.sub = sub;
            this.toString = toString;
        }

        public String toString() {
            return this.toString;
        }

        public Type plural() {
            switch (this) {
                case DOUBLE: {
                    return DOUBLES;
                }
                case LONG: {
                    return LONGS;
                }
                case STRING: {
                    return STRINGS;
                }
                case VERSION: {
                    return VERSIONS;
                }
            }
            return null;
        }
    }

    public static interface DataType<T> {
        public Type type();
    }
}

