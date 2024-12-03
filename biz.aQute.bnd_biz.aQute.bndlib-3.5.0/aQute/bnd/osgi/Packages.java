/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Descriptors;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Packages
implements Map<Descriptors.PackageRef, Attrs> {
    private LinkedHashMap<Descriptors.PackageRef, Attrs> map;
    static Map<Descriptors.PackageRef, Attrs> EMPTY = Collections.emptyMap();

    public Packages(Packages other) {
        if (other.map != null) {
            this.map = new LinkedHashMap<Descriptors.PackageRef, Attrs>(other.map);
        }
    }

    public Packages() {
    }

    @Override
    public void clear() {
        if (this.map != null) {
            this.map.clear();
        }
    }

    public boolean containsKey(Descriptors.PackageRef name) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    @Override
    @Deprecated
    public boolean containsKey(Object name) {
        assert (name instanceof Descriptors.PackageRef);
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
    public Set<Map.Entry<Descriptors.PackageRef, Attrs>> entrySet() {
        if (this.map == null) {
            return EMPTY.entrySet();
        }
        return this.map.entrySet();
    }

    @Override
    @Deprecated
    public Attrs get(Object key) {
        assert (key instanceof Descriptors.PackageRef);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public Attrs get(Descriptors.PackageRef key) {
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
    public Set<Descriptors.PackageRef> keySet() {
        if (this.map == null) {
            return EMPTY.keySet();
        }
        return this.map.keySet();
    }

    public Attrs put(Descriptors.PackageRef ref) {
        Attrs attrs = this.get(ref);
        if (attrs != null) {
            return attrs;
        }
        attrs = new Attrs();
        this.put(ref, attrs);
        return attrs;
    }

    @Override
    public Attrs put(Descriptors.PackageRef key, Attrs value) {
        if (this.map == null) {
            this.map = new LinkedHashMap();
        }
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends Descriptors.PackageRef, ? extends Attrs> map) {
        if (this.map == null) {
            if (map.isEmpty()) {
                return;
            }
            this.map = new LinkedHashMap();
        }
        this.map.putAll(map);
    }

    public void putAllIfAbsent(Map<Descriptors.PackageRef, ? extends Attrs> map) {
        for (Map.Entry<Descriptors.PackageRef, ? extends Attrs> entry : map.entrySet()) {
            if (this.containsKey(entry.getKey())) continue;
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Deprecated
    public Attrs remove(Object var0) {
        assert (var0 instanceof Descriptors.PackageRef);
        if (this.map == null) {
            return null;
        }
        return (Attrs)this.map.remove(var0);
    }

    public Attrs remove(Descriptors.PackageRef var0) {
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

    public Attrs getByFQN(String s) {
        if (this.map == null) {
            return null;
        }
        for (Map.Entry<Descriptors.PackageRef, Attrs> pr : this.map.entrySet()) {
            if (!pr.getKey().getFQN().equals(s)) continue;
            return pr.getValue();
        }
        return null;
    }

    public Attrs getByBinaryName(String s) {
        if (this.map == null) {
            return null;
        }
        for (Map.Entry<Descriptors.PackageRef, Attrs> pr : this.map.entrySet()) {
            if (!pr.getKey().getBinary().equals(s)) continue;
            return pr.getValue();
        }
        return null;
    }

    public boolean containsFQN(String s) {
        return this.getByFQN(s) != null;
    }

    public boolean containsBinaryName(String s) {
        return this.getByBinaryName(s) != null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.append(sb);
        return sb.toString();
    }

    public void append(StringBuilder sb) {
        String del = "";
        for (Map.Entry<Descriptors.PackageRef, Attrs> s : this.entrySet()) {
            sb.append(del);
            sb.append(s.getKey());
            if (!s.getValue().isEmpty()) {
                sb.append(';');
                s.getValue().append(sb);
            }
            del = ",";
        }
    }

    public void merge(Descriptors.PackageRef ref, boolean unique, Attrs ... attrs) {
        if (unique) {
            while (this.containsKey(ref)) {
                ref = ref.getDuplicate();
            }
        }
        Attrs org = this.put(ref);
        for (Attrs a : attrs) {
            if (a == null) continue;
            org.putAll(a);
        }
    }

    public Attrs get(Descriptors.PackageRef packageRef, Attrs deflt) {
        Attrs mine = this.get(packageRef);
        if (mine != null) {
            return mine;
        }
        return deflt;
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

    public static enum QUERY {
        ANY,
        ANNOTATED,
        NAMED,
        VERSIONED;

    }
}

