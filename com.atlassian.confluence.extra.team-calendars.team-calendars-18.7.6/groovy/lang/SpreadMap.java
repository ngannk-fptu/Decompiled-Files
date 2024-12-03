/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class SpreadMap
extends HashMap {
    private int hashCode;

    public SpreadMap(Object[] values) {
        int i = 0;
        while (i < values.length) {
            super.put(values[i++], values[i++]);
        }
    }

    public SpreadMap(Map map) {
        super(map);
    }

    public SpreadMap(List list) {
        this(list.toArray());
    }

    @Override
    public Object put(Object key, Object value) {
        throw new RuntimeException("SpreadMap: " + this + " is an immutable map, and so (" + key + ": " + value + ") cannot be added.");
    }

    @Override
    public Object remove(Object key) {
        throw new RuntimeException("SpreadMap: " + this + " is an immutable map, and so the key (" + key + ") cannot be deleted.");
    }

    @Override
    public void putAll(Map t) {
        throw new RuntimeException("SpreadMap: " + this + " is an immutable map, and so the map (" + t + ") cannot be put in this spreadMap.");
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof SpreadMap) {
            return this.equals((SpreadMap)that);
        }
        return false;
    }

    public boolean equals(SpreadMap that) {
        if (that == null) {
            return false;
        }
        if (this.size() == that.size()) {
            Iterator iterator = this.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry e;
                Map.Entry entry = e = iterator.next();
                Object key = entry.getKey();
                if (DefaultTypeTransformation.compareEqual(entry.getValue(), that.get(key))) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            for (Object key : this.keySet()) {
                int hash = key != null ? key.hashCode() : 47806;
                this.hashCode ^= hash;
            }
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "*:[:]";
        }
        StringBuilder sb = new StringBuilder("*:[");
        Iterator iter = this.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            sb.append(key).append(":").append(this.get(key));
            if (!iter.hasNext()) continue;
            sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}

