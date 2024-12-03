/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.map;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class MAP {
    public static <Kx, Vx> MAPX<Kx, Vx> $(Kx key, Vx value) {
        MAPX<Kx, Vx> map = new MAPX<Kx, Vx>();
        map.put(key, value);
        return map;
    }

    public <K, V> Map<K, V> dictionary(Dictionary<K, V> dict) {
        LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
        Enumeration<K> e = dict.keys();
        while (e.hasMoreElements()) {
            K k = e.nextElement();
            V v = dict.get(k);
            map.put(k, v);
        }
        return map;
    }

    public static class MAPX<K, V>
    extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;

        public MAPX<K, V> $(K key, V value) {
            this.put(key, value);
            return this;
        }

        public MAPX<K, V> $(Map<K, V> all) {
            this.putAll(all);
            return this;
        }

        public Hashtable<K, V> asHashtable() {
            return new Hashtable(this);
        }
    }
}

