/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;
import org.apache.lucene.util.fst.Util;

public final class SpecialOperations {
    private SpecialOperations() {
    }

    static int findIndex(int c, int[] points) {
        int a = 0;
        int b = points.length;
        while (b - a > 1) {
            int d = a + b >>> 1;
            if (points[d] > c) {
                b = d;
                continue;
            }
            if (points[d] < c) {
                a = d;
                continue;
            }
            return d;
        }
        return a;
    }

    public static boolean isFinite(Automaton a) {
        if (a.isSingleton()) {
            return true;
        }
        return SpecialOperations.isFinite(a.initial, new BitSet(a.getNumberOfStates()), new BitSet(a.getNumberOfStates()));
    }

    private static boolean isFinite(State s, BitSet path, BitSet visited) {
        path.set(s.number);
        for (Transition t : s.getTransitions()) {
            if (!path.get(t.to.number) && (visited.get(t.to.number) || SpecialOperations.isFinite(t.to, path, visited))) continue;
            return false;
        }
        path.clear(s.number);
        visited.set(s.number);
        return true;
    }

    public static String getCommonPrefix(Automaton a) {
        boolean done;
        if (a.isSingleton()) {
            return a.singleton;
        }
        StringBuilder b = new StringBuilder();
        HashSet<State> visited = new HashSet<State>();
        State s = a.initial;
        do {
            done = true;
            visited.add(s);
            if (s.accept || s.numTransitions() != 1) continue;
            Transition t = s.getTransitions().iterator().next();
            if (t.min != t.max || visited.contains(t.to)) continue;
            b.appendCodePoint(t.min);
            s = t.to;
            done = false;
        } while (!done);
        return b.toString();
    }

    public static BytesRef getCommonPrefixBytesRef(Automaton a) {
        boolean done;
        if (a.isSingleton()) {
            return new BytesRef(a.singleton);
        }
        BytesRef ref = new BytesRef(10);
        HashSet<State> visited = new HashSet<State>();
        State s = a.initial;
        do {
            done = true;
            visited.add(s);
            if (s.accept || s.numTransitions() != 1) continue;
            Transition t = s.getTransitions().iterator().next();
            if (t.min != t.max || visited.contains(t.to)) continue;
            ref.grow(++ref.length);
            ref.bytes[ref.length - 1] = (byte)t.min;
            s = t.to;
            done = false;
        } while (!done);
        return ref;
    }

    public static String getCommonSuffix(Automaton a) {
        if (a.isSingleton()) {
            return a.singleton;
        }
        Automaton r = a.clone();
        SpecialOperations.reverse(r);
        r.determinize();
        return new StringBuilder(SpecialOperations.getCommonPrefix(r)).reverse().toString();
    }

    public static BytesRef getCommonSuffixBytesRef(Automaton a) {
        if (a.isSingleton()) {
            return new BytesRef(a.singleton);
        }
        Automaton r = a.clone();
        SpecialOperations.reverse(r);
        r.determinize();
        BytesRef ref = SpecialOperations.getCommonPrefixBytesRef(r);
        SpecialOperations.reverseBytes(ref);
        return ref;
    }

    private static void reverseBytes(BytesRef ref) {
        if (ref.length <= 1) {
            return;
        }
        int num = ref.length >> 1;
        for (int i = ref.offset; i < ref.offset + num; ++i) {
            byte b = ref.bytes[i];
            ref.bytes[i] = ref.bytes[ref.offset * 2 + ref.length - i - 1];
            ref.bytes[ref.offset * 2 + ref.length - i - 1] = b;
        }
    }

    public static Set<State> reverse(Automaton a) {
        a.expandSingleton();
        HashMap m = new HashMap();
        State[] states = a.getNumberedStates();
        HashSet<State> accept = new HashSet<State>();
        for (State s : states) {
            if (!s.isAccept()) continue;
            accept.add(s);
        }
        for (State r : states) {
            m.put(r, new HashSet());
            r.accept = false;
        }
        for (State r : states) {
            for (Transition t : r.getTransitions()) {
                ((HashSet)m.get(t.to)).add(new Transition(t.min, t.max, r));
            }
        }
        for (State r : states) {
            Set tr = (Set)m.get(r);
            r.setTransitions(tr.toArray(new Transition[tr.size()]));
        }
        a.initial.accept = true;
        a.initial = new State();
        for (State r : accept) {
            a.initial.addEpsilon(r);
        }
        a.deterministic = false;
        a.clearNumberedStates();
        return accept;
    }

    public static Set<IntsRef> getFiniteStrings(Automaton a, int limit) {
        HashSet<IntsRef> strings = new HashSet<IntsRef>();
        if (a.isSingleton()) {
            if (limit > 0) {
                strings.add(Util.toUTF32(a.singleton, new IntsRef()));
            }
        } else if (!SpecialOperations.getFiniteStrings(a.initial, new HashSet<State>(), strings, new IntsRef(), limit)) {
            return strings;
        }
        return strings;
    }

    private static boolean getFiniteStrings(State s, HashSet<State> pathstates, HashSet<IntsRef> strings, IntsRef path, int limit) {
        pathstates.add(s);
        for (Transition t : s.getTransitions()) {
            if (pathstates.contains(t.to)) {
                return false;
            }
            for (int n = t.min; n <= t.max; ++n) {
                path.grow(path.length + 1);
                path.ints[path.length] = n;
                ++path.length;
                if (t.to.accept) {
                    strings.add(IntsRef.deepCopyOf(path));
                    if (limit >= 0 && strings.size() > limit) {
                        return false;
                    }
                }
                if (!SpecialOperations.getFiniteStrings(t.to, pathstates, strings, path, limit)) {
                    return false;
                }
                --path.length;
            }
        }
        pathstates.remove(s);
        return true;
    }
}

