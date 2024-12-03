/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.env;

import aQute.lib.env.Header;
import aQute.lib.env.Props;
import aQute.lib.env.Selector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Sieve
implements Map<Selector, Props> {
    private LinkedHashMap<Selector, Props> map;
    public static Sieve ALWAYS = new Sieve();
    static Map<Selector, Props> EMPTY = Collections.emptyMap();

    public Sieve(Sieve other) {
        if (other.map != null && !other.map.isEmpty()) {
            this.map = new LinkedHashMap<Selector, Props>(other.map);
        }
    }

    public Sieve(Collection<String> other) {
        if (other != null) {
            for (String s : other) {
                this.put(new Selector(s), null);
            }
        }
    }

    public Sieve() {
    }

    public Sieve(Header contained) {
        this.append(contained);
    }

    public Sieve(String h) {
        this(new Header(h));
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public boolean containsKey(Selector name) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    @Override
    @Deprecated
    public boolean containsKey(Object name) {
        assert (name instanceof Selector);
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    public boolean containsValue(Props value) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    @Deprecated
    public boolean containsValue(Object value) {
        assert (value instanceof Props);
        if (this.map == null) {
            return false;
        }
        return this.map.containsValue(value);
    }

    @Override
    public Set<Map.Entry<Selector, Props>> entrySet() {
        if (this.map == null) {
            return EMPTY.entrySet();
        }
        return this.map.entrySet();
    }

    @Override
    @Deprecated
    public Props get(Object key) {
        assert (key instanceof Selector);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public Props get(Selector key) {
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
    public Set<Selector> keySet() {
        if (this.map == null) {
            return EMPTY.keySet();
        }
        return this.map.keySet();
    }

    @Override
    public Props put(Selector key, Props value) {
        if (this.map == null) {
            this.map = new LinkedHashMap();
        }
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends Selector, ? extends Props> map) {
        if (this.map == null) {
            if (map.isEmpty()) {
                return;
            }
            this.map = new LinkedHashMap();
        }
        this.map.putAll(map);
    }

    @Override
    @Deprecated
    public Props remove(Object var0) {
        assert (var0 instanceof Selector);
        if (this.map == null) {
            return null;
        }
        return (Props)this.map.remove(var0);
    }

    public Props remove(Selector var0) {
        if (this.map == null) {
            return null;
        }
        return (Props)this.map.remove(var0);
    }

    @Override
    public int size() {
        if (this.map == null) {
            return 0;
        }
        return this.map.size();
    }

    @Override
    public Collection<Props> values() {
        if (this.map == null) {
            return EMPTY.values();
        }
        return this.map.values();
    }

    public String toString() {
        return this.map == null ? "{}" : this.map.toString();
    }

    public void append(Header other) {
        for (Map.Entry<String, Props> e : other.entrySet()) {
            this.put(new Selector(e.getKey()), e.getValue());
        }
    }

    public <T> Collection<T> select(Collection<T> set, boolean emptyIsAll) {
        return this.select(set, null, emptyIsAll);
    }

    public <T> Collection<T> select(Collection<T> set, Set<Selector> unused, boolean emptyIsAll) {
        ArrayList<T> input = new ArrayList<T>(set);
        if (emptyIsAll && this.isEmpty()) {
            return input;
        }
        ArrayList result = new ArrayList();
        for (Selector instruction : this.keySet()) {
            boolean used = false;
            Iterator o = input.iterator();
            while (o.hasNext()) {
                Object oo = o.next();
                String s = oo.toString();
                if (!instruction.matches(s)) continue;
                if (!instruction.isNegated()) {
                    result.add(oo);
                }
                o.remove();
                used = true;
            }
            if (used || unused == null) continue;
            unused.add(instruction);
        }
        return result;
    }

    public <T> Collection<T> reject(Collection<T> set) {
        ArrayList<T> input = new ArrayList<T>(set);
        ArrayList result = new ArrayList();
        for (Selector instruction : this.keySet()) {
            Iterator o = input.iterator();
            while (o.hasNext()) {
                Object oo = o.next();
                String s = oo.toString();
                if (instruction.matches(s)) {
                    if (instruction.isNegated()) {
                        result.add(oo);
                    }
                    o.remove();
                    continue;
                }
                result.add(oo);
            }
        }
        return result;
    }

    public Selector matcher(String value) {
        for (Selector i : this.keySet()) {
            if (!i.matches(value)) continue;
            return i;
        }
        return null;
    }

    public Selector finder(String value) {
        for (Selector i : this.keySet()) {
            if (!i.finds(value)) continue;
            return i;
        }
        return null;
    }

    public boolean matches(String value) {
        if (this.size() == 0) {
            return true;
        }
        Selector instr = this.matcher(value);
        return instr != null && !instr.isNegated();
    }
}

