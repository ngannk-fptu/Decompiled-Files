/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.automaton.BasicOperations;
import org.apache.lucene.util.automaton.MinimizationOperations;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;

public class Automaton
implements Cloneable {
    public static final int MINIMIZE_HOPCROFT = 2;
    static int minimization = 2;
    State initial;
    boolean deterministic;
    transient Object info;
    String singleton;
    static boolean minimize_always = false;
    static boolean allow_mutation = false;
    private State[] numberedStates;

    public Automaton(State initial) {
        this.initial = initial;
        this.deterministic = true;
        this.singleton = null;
    }

    public Automaton() {
        this(new State());
    }

    public static void setMinimization(int algorithm) {
        minimization = algorithm;
    }

    public static void setMinimizeAlways(boolean flag) {
        minimize_always = flag;
    }

    public static boolean setAllowMutate(boolean flag) {
        boolean b = allow_mutation;
        allow_mutation = flag;
        return b;
    }

    static boolean getAllowMutate() {
        return allow_mutation;
    }

    void checkMinimizeAlways() {
        if (minimize_always) {
            MinimizationOperations.minimize(this);
        }
    }

    boolean isSingleton() {
        return this.singleton != null;
    }

    public String getSingleton() {
        return this.singleton;
    }

    public State getInitialState() {
        this.expandSingleton();
        return this.initial;
    }

    public boolean isDeterministic() {
        return this.deterministic;
    }

    public void setDeterministic(boolean deterministic) {
        this.deterministic = deterministic;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public Object getInfo() {
        return this.info;
    }

    public State[] getNumberedStates() {
        if (this.numberedStates == null) {
            this.expandSingleton();
            HashSet<State> visited = new HashSet<State>();
            LinkedList<State> worklist = new LinkedList<State>();
            this.numberedStates = new State[4];
            int upto = 0;
            worklist.add(this.initial);
            visited.add(this.initial);
            this.initial.number = upto;
            this.numberedStates[upto] = this.initial;
            ++upto;
            while (worklist.size() > 0) {
                State s = (State)worklist.removeFirst();
                for (int i = 0; i < s.numTransitions; ++i) {
                    Transition t = s.transitionsArray[i];
                    if (visited.contains(t.to)) continue;
                    visited.add(t.to);
                    worklist.add(t.to);
                    t.to.number = upto;
                    if (upto == this.numberedStates.length) {
                        State[] newArray = new State[ArrayUtil.oversize(1 + upto, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                        System.arraycopy(this.numberedStates, 0, newArray, 0, upto);
                        this.numberedStates = newArray;
                    }
                    this.numberedStates[upto] = t.to;
                    ++upto;
                }
            }
            if (this.numberedStates.length != upto) {
                State[] newArray = new State[upto];
                System.arraycopy(this.numberedStates, 0, newArray, 0, upto);
                this.numberedStates = newArray;
            }
        }
        return this.numberedStates;
    }

    public void setNumberedStates(State[] states) {
        this.setNumberedStates(states, states.length);
    }

    public void setNumberedStates(State[] states, int count) {
        assert (count <= states.length);
        if (count < states.length) {
            State[] newArray = new State[count];
            System.arraycopy(states, 0, newArray, 0, count);
            this.numberedStates = newArray;
        } else {
            this.numberedStates = states;
        }
    }

    public void clearNumberedStates() {
        this.numberedStates = null;
    }

    public Set<State> getAcceptStates() {
        this.expandSingleton();
        HashSet<State> accepts = new HashSet<State>();
        HashSet<State> visited = new HashSet<State>();
        LinkedList<State> worklist = new LinkedList<State>();
        worklist.add(this.initial);
        visited.add(this.initial);
        while (worklist.size() > 0) {
            State s = (State)worklist.removeFirst();
            if (s.accept) {
                accepts.add(s);
            }
            for (Transition t : s.getTransitions()) {
                if (visited.contains(t.to)) continue;
                visited.add(t.to);
                worklist.add(t.to);
            }
        }
        return accepts;
    }

    void totalize() {
        State s = new State();
        s.addTransition(new Transition(0, 0x10FFFF, s));
        for (State p : this.getNumberedStates()) {
            int maxi = 0;
            p.sortTransitions(Transition.CompareByMinMaxThenDest);
            for (Transition t : p.getTransitions()) {
                if (t.min > maxi) {
                    p.addTransition(new Transition(maxi, t.min - 1, s));
                }
                if (t.max + 1 <= maxi) continue;
                maxi = t.max + 1;
            }
            if (maxi > 0x10FFFF) continue;
            p.addTransition(new Transition(maxi, 0x10FFFF, s));
        }
        this.clearNumberedStates();
    }

    public void restoreInvariant() {
        this.removeDeadTransitions();
    }

    public void reduce() {
        State[] states = this.getNumberedStates();
        if (this.isSingleton()) {
            return;
        }
        for (State s : states) {
            s.reduce();
        }
    }

    int[] getStartPoints() {
        State[] states = this.getNumberedStates();
        HashSet<Integer> pointset = new HashSet<Integer>();
        pointset.add(0);
        for (State s : states) {
            for (Transition t : s.getTransitions()) {
                pointset.add(t.min);
                if (t.max >= 0x10FFFF) continue;
                pointset.add(t.max + 1);
            }
        }
        int[] points = new int[pointset.size()];
        int n = 0;
        for (Integer m : pointset) {
            points[n++] = m;
        }
        Arrays.sort(points);
        return points;
    }

    private State[] getLiveStates() {
        State[] states = this.getNumberedStates();
        HashSet<State> live = new HashSet<State>();
        for (State q : states) {
            if (!q.isAccept()) continue;
            live.add(q);
        }
        Set[] map = new Set[states.length];
        for (int i = 0; i < map.length; ++i) {
            map[i] = new HashSet();
        }
        for (State s : states) {
            for (int i = 0; i < s.numTransitions; ++i) {
                map[s.transitionsArray[i].to.number].add(s);
            }
        }
        LinkedList<State> worklist = new LinkedList<State>(live);
        while (worklist.size() > 0) {
            State s = (State)worklist.removeFirst();
            for (State p : map[s.number]) {
                if (live.contains(p)) continue;
                live.add(p);
                worklist.add(p);
            }
        }
        return live.toArray(new State[live.size()]);
    }

    public void removeDeadTransitions() {
        State[] states = this.getNumberedStates();
        if (this.isSingleton()) {
            return;
        }
        State[] live = this.getLiveStates();
        BitSet liveSet = new BitSet(states.length);
        for (State s : live) {
            liveSet.set(s.number);
        }
        for (State s : states) {
            int upto = 0;
            for (int i = 0; i < s.numTransitions; ++i) {
                Transition t = s.transitionsArray[i];
                if (!liveSet.get(t.to.number)) continue;
                s.transitionsArray[upto++] = s.transitionsArray[i];
            }
            s.numTransitions = upto;
        }
        for (int i = 0; i < live.length; ++i) {
            live[i].number = i;
        }
        if (live.length > 0) {
            this.setNumberedStates(live);
        } else {
            this.clearNumberedStates();
        }
        this.reduce();
    }

    public Transition[][] getSortedTransitions() {
        State[] states = this.getNumberedStates();
        Transition[][] transitions = new Transition[states.length][];
        for (State s : states) {
            s.sortTransitions(Transition.CompareByMinMaxThenDest);
            s.trimTransitionsArray();
            transitions[s.number] = s.transitionsArray;
            assert (s.transitionsArray != null);
        }
        return transitions;
    }

    public void expandSingleton() {
        if (this.isSingleton()) {
            State p;
            this.initial = p = new State();
            int cp = 0;
            for (int i = 0; i < this.singleton.length(); i += Character.charCount(cp)) {
                State q = new State();
                cp = this.singleton.codePointAt(i);
                p.addTransition(new Transition(cp, q));
                p = q;
            }
            p.accept = true;
            this.deterministic = true;
            this.singleton = null;
        }
    }

    public int getNumberOfStates() {
        if (this.isSingleton()) {
            return this.singleton.codePointCount(0, this.singleton.length()) + 1;
        }
        return this.getNumberedStates().length;
    }

    public int getNumberOfTransitions() {
        if (this.isSingleton()) {
            return this.singleton.codePointCount(0, this.singleton.length());
        }
        int c = 0;
        for (State s : this.getNumberedStates()) {
            c += s.numTransitions();
        }
        return c;
    }

    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("use BasicOperations.sameLanguage instead");
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        if (this.isSingleton()) {
            b.append("singleton: ");
            int length = this.singleton.codePointCount(0, this.singleton.length());
            int[] codepoints = new int[length];
            int j = 0;
            int cp = 0;
            for (int i = 0; i < this.singleton.length(); i += Character.charCount(cp)) {
                codepoints[j++] = cp = this.singleton.codePointAt(i);
            }
            for (int c : codepoints) {
                Transition.appendCharString(c, b);
            }
            b.append("\n");
        } else {
            State[] states = this.getNumberedStates();
            b.append("initial state: ").append(this.initial.number).append("\n");
            for (State s : states) {
                b.append(s.toString());
            }
        }
        return b.toString();
    }

    public String toDot() {
        State[] states;
        StringBuilder b = new StringBuilder("digraph Automaton {\n");
        b.append("  rankdir = LR;\n");
        for (State s : states = this.getNumberedStates()) {
            b.append("  ").append(s.number);
            if (s.accept) {
                b.append(" [shape=doublecircle,label=\"\"];\n");
            } else {
                b.append(" [shape=circle,label=\"\"];\n");
            }
            if (s == this.initial) {
                b.append("  initial [shape=plaintext,label=\"\"];\n");
                b.append("  initial -> ").append(s.number).append("\n");
            }
            for (Transition t : s.getTransitions()) {
                b.append("  ").append(s.number);
                t.appendDot(b);
            }
        }
        return b.append("}\n").toString();
    }

    Automaton cloneExpanded() {
        Automaton a = this.clone();
        a.expandSingleton();
        return a;
    }

    Automaton cloneExpandedIfRequired() {
        if (allow_mutation) {
            this.expandSingleton();
            return this;
        }
        return this.cloneExpanded();
    }

    public Automaton clone() {
        try {
            Automaton a = (Automaton)super.clone();
            if (!this.isSingleton()) {
                State[] states;
                HashMap<State, State> m = new HashMap<State, State>();
                for (State s : states = this.getNumberedStates()) {
                    m.put(s, new State());
                }
                for (State s : states) {
                    State p = (State)m.get(s);
                    p.accept = s.accept;
                    if (s == this.initial) {
                        a.initial = p;
                    }
                    for (Transition t : s.getTransitions()) {
                        p.addTransition(new Transition(t.min, t.max, (State)m.get(t.to)));
                    }
                }
            }
            a.clearNumberedStates();
            return a;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    Automaton cloneIfRequired() {
        if (allow_mutation) {
            return this;
        }
        return this.clone();
    }

    public Automaton concatenate(Automaton a) {
        return BasicOperations.concatenate(this, a);
    }

    public static Automaton concatenate(List<Automaton> l) {
        return BasicOperations.concatenate(l);
    }

    public Automaton optional() {
        return BasicOperations.optional(this);
    }

    public Automaton repeat() {
        return BasicOperations.repeat(this);
    }

    public Automaton repeat(int min) {
        return BasicOperations.repeat(this, min);
    }

    public Automaton repeat(int min, int max) {
        return BasicOperations.repeat(this, min, max);
    }

    public Automaton complement() {
        return BasicOperations.complement(this);
    }

    public Automaton minus(Automaton a) {
        return BasicOperations.minus(this, a);
    }

    public Automaton intersection(Automaton a) {
        return BasicOperations.intersection(this, a);
    }

    public boolean subsetOf(Automaton a) {
        return BasicOperations.subsetOf(this, a);
    }

    public Automaton union(Automaton a) {
        return BasicOperations.union(this, a);
    }

    public static Automaton union(Collection<Automaton> l) {
        return BasicOperations.union(l);
    }

    public void determinize() {
        BasicOperations.determinize(this);
    }

    public boolean isEmptyString() {
        return BasicOperations.isEmptyString(this);
    }

    public static Automaton minimize(Automaton a) {
        MinimizationOperations.minimize(a);
        return a;
    }
}

