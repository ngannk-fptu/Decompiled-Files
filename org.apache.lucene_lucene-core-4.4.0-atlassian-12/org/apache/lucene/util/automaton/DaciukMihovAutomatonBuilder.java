/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.Transition;

final class DaciukMihovAutomatonBuilder {
    private HashMap<State, State> stateRegistry = new HashMap();
    private State root = new State();
    private CharsRef previous;
    private static final Comparator<CharsRef> comparator = CharsRef.getUTF16SortedAsUTF8Comparator();

    DaciukMihovAutomatonBuilder() {
    }

    public void add(CharsRef current) {
        State next;
        int pos;
        assert (this.stateRegistry != null) : "Automaton already built.";
        assert (this.previous == null || comparator.compare(this.previous, current) <= 0) : "Input must be in sorted UTF-8 order: " + this.previous + " >= " + current;
        assert (this.setPrevious(current));
        int max = current.length();
        State state = this.root;
        for (pos = 0; pos < max && (next = state.lastChild(Character.codePointAt(current, pos))) != null; pos += Character.charCount(Character.codePointAt(current, pos))) {
            state = next;
        }
        if (state.hasChildren()) {
            this.replaceOrRegister(state);
        }
        this.addSuffix(state, current, pos);
    }

    public State complete() {
        if (this.stateRegistry == null) {
            throw new IllegalStateException();
        }
        if (this.root.hasChildren()) {
            this.replaceOrRegister(this.root);
        }
        this.stateRegistry = null;
        return this.root;
    }

    private static org.apache.lucene.util.automaton.State convert(State s, IdentityHashMap<State, org.apache.lucene.util.automaton.State> visited) {
        org.apache.lucene.util.automaton.State converted = visited.get(s);
        if (converted != null) {
            return converted;
        }
        converted = new org.apache.lucene.util.automaton.State();
        converted.setAccept(s.is_final);
        visited.put(s, converted);
        int i = 0;
        int[] labels = s.labels;
        for (State target : s.states) {
            converted.addTransition(new Transition(labels[i++], DaciukMihovAutomatonBuilder.convert(target, visited)));
        }
        return converted;
    }

    public static Automaton build(Collection<BytesRef> input) {
        DaciukMihovAutomatonBuilder builder = new DaciukMihovAutomatonBuilder();
        CharsRef scratch = new CharsRef();
        for (BytesRef b : input) {
            UnicodeUtil.UTF8toUTF16(b, scratch);
            builder.add(scratch);
        }
        Automaton a = new Automaton();
        a.initial = DaciukMihovAutomatonBuilder.convert(builder.complete(), new IdentityHashMap<State, org.apache.lucene.util.automaton.State>());
        a.deterministic = true;
        return a;
    }

    private boolean setPrevious(CharsRef current) {
        this.previous = CharsRef.deepCopyOf(current);
        return true;
    }

    private void replaceOrRegister(State state) {
        State registered;
        State child = state.lastChild();
        if (child.hasChildren()) {
            this.replaceOrRegister(child);
        }
        if ((registered = this.stateRegistry.get(child)) != null) {
            state.replaceLastChild(registered);
        } else {
            this.stateRegistry.put(child, child);
        }
    }

    private void addSuffix(State state, CharSequence current, int fromIndex) {
        int len = current.length();
        while (fromIndex < len) {
            int cp = Character.codePointAt(current, fromIndex);
            state = state.newState(cp);
            fromIndex += Character.charCount(cp);
        }
        state.is_final = true;
    }

    private static final class State {
        private static final int[] NO_LABELS = new int[0];
        private static final State[] NO_STATES = new State[0];
        int[] labels = NO_LABELS;
        State[] states = NO_STATES;
        boolean is_final;

        private State() {
        }

        State getState(int label) {
            int index = Arrays.binarySearch(this.labels, label);
            return index >= 0 ? this.states[index] : null;
        }

        public boolean equals(Object obj) {
            State other = (State)obj;
            return this.is_final == other.is_final && Arrays.equals(this.labels, other.labels) && State.referenceEquals(this.states, other.states);
        }

        public int hashCode() {
            int hash = this.is_final ? 1 : 0;
            hash ^= hash * 31 + this.labels.length;
            for (int c : this.labels) {
                hash ^= hash * 31 + c;
            }
            for (State s : this.states) {
                hash ^= System.identityHashCode(s);
            }
            return hash;
        }

        boolean hasChildren() {
            return this.labels.length > 0;
        }

        State newState(int label) {
            assert (Arrays.binarySearch(this.labels, label) < 0) : "State already has transition labeled: " + label;
            this.labels = Arrays.copyOf(this.labels, this.labels.length + 1);
            this.states = Arrays.copyOf(this.states, this.states.length + 1);
            this.labels[this.labels.length - 1] = label;
            State state = new State();
            this.states[this.states.length - 1] = state;
            return state;
        }

        State lastChild() {
            assert (this.hasChildren()) : "No outgoing transitions.";
            return this.states[this.states.length - 1];
        }

        State lastChild(int label) {
            int index = this.labels.length - 1;
            State s = null;
            if (index >= 0 && this.labels[index] == label) {
                s = this.states[index];
            }
            assert (s == this.getState(label));
            return s;
        }

        void replaceLastChild(State state) {
            assert (this.hasChildren()) : "No outgoing transitions.";
            this.states[this.states.length - 1] = state;
        }

        private static boolean referenceEquals(Object[] a1, Object[] a2) {
            if (a1.length != a2.length) {
                return false;
            }
            for (int i = 0; i < a1.length; ++i) {
                if (a1[i] == a2[i]) continue;
                return false;
            }
            return true;
        }
    }
}

