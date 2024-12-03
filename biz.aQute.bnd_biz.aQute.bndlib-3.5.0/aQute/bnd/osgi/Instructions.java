/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Instruction;
import aQute.lib.io.IO;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Instructions
implements Map<Instruction, Attrs> {
    private LinkedHashMap<Instruction, Attrs> map;
    public static Instructions ALWAYS = new Instructions();
    static Map<Instruction, Attrs> EMPTY = Collections.emptyMap();

    public Instructions(Instructions other) {
        if (other.map != null && !other.map.isEmpty()) {
            this.map = new LinkedHashMap<Instruction, Attrs>(other.map);
        }
    }

    public Instructions(Collection<String> other) {
        if (other != null) {
            for (String s : other) {
                this.put(new Instruction(s), null);
            }
        }
    }

    public Instructions() {
    }

    public Instructions(Parameters contained) {
        this.append(contained);
    }

    public Instructions(String h) {
        this(new Parameters(h));
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public boolean containsKey(Instruction name) {
        if (this.map == null) {
            return false;
        }
        return this.map.containsKey(name);
    }

    @Override
    @Deprecated
    public boolean containsKey(Object name) {
        assert (name instanceof Instruction);
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
    public Set<Map.Entry<Instruction, Attrs>> entrySet() {
        if (this.map == null) {
            return EMPTY.entrySet();
        }
        return this.map.entrySet();
    }

    @Override
    @Deprecated
    public Attrs get(Object key) {
        assert (key instanceof Instruction);
        if (this.map == null) {
            return null;
        }
        return this.map.get(key);
    }

    public Attrs get(Instruction key) {
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
    public Set<Instruction> keySet() {
        if (this.map == null) {
            return EMPTY.keySet();
        }
        return this.map.keySet();
    }

    @Override
    public Attrs put(Instruction key, Attrs value) {
        if (this.map == null) {
            this.map = new LinkedHashMap();
        }
        return this.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends Instruction, ? extends Attrs> map) {
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
    public Attrs remove(Object var0) {
        assert (var0 instanceof Instruction);
        if (this.map == null) {
            return null;
        }
        return (Attrs)this.map.remove(var0);
    }

    public Attrs remove(Instruction var0) {
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
        return this.map == null ? "{}" : this.map.toString();
    }

    public void append(Parameters other) {
        for (Map.Entry<String, Attrs> e : other.entrySet()) {
            this.put(new Instruction(e.getKey()), e.getValue());
        }
    }

    public <T> Collection<T> select(Collection<T> set, boolean emptyIsAll) {
        return this.select(set, null, emptyIsAll);
    }

    public <T> Collection<T> select(Collection<T> set, Set<Instruction> unused, boolean emptyIsAll) {
        ArrayList<T> input = new ArrayList<T>(set);
        if (emptyIsAll && this.isEmpty()) {
            return input;
        }
        ArrayList result = new ArrayList();
        for (Instruction instruction : this.keySet()) {
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
        for (Instruction instruction : this.keySet()) {
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

    public Instruction matcher(String value) {
        for (Instruction i : this.keySet()) {
            if (!i.matches(value)) continue;
            return i;
        }
        return null;
    }

    public Instruction finder(String value) {
        for (Instruction i : this.keySet()) {
            if (!i.finds(value)) continue;
            return i;
        }
        return null;
    }

    public boolean matches(String value) {
        if (this.size() == 0) {
            return true;
        }
        Instruction instr = this.matcher(value);
        return instr != null && !instr.isNegated();
    }

    public Map<File, Attrs> select(File base) {
        HashMap<File, Attrs> result = new HashMap<File, Attrs>();
        for (Map.Entry<Instruction, Attrs> instr : this.entrySet()) {
            File f;
            if (!instr.getKey().isLiteral() || instr.getKey().isNegated() || !(f = IO.getFile(base, instr.getKey().getLiteral())).isFile()) continue;
            result.put(f, instr.getValue());
        }
        if (base != null) {
            block1: for (File f : base.listFiles()) {
                for (Map.Entry<Instruction, Attrs> instr : this.entrySet()) {
                    String name = f.getName();
                    if (!instr.getKey().matches(name)) continue;
                    if (instr.getKey().isNegated()) continue block1;
                    result.put(f, instr.getValue());
                    continue block1;
                }
            }
        }
        return result;
    }
}

