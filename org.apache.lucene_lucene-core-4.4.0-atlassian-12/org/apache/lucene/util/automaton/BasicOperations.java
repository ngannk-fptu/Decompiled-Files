/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.BasicAutomata;
import org.apache.lucene.util.automaton.SortedIntSet;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.StatePair;
import org.apache.lucene.util.automaton.Transition;

public final class BasicOperations {
    private BasicOperations() {
    }

    public static Automaton concatenate(Automaton a1, Automaton a2) {
        boolean deterministic;
        if (a1.isSingleton() && a2.isSingleton()) {
            return BasicAutomata.makeString(a1.singleton + a2.singleton);
        }
        if (BasicOperations.isEmpty(a1) || BasicOperations.isEmpty(a2)) {
            return BasicAutomata.makeEmpty();
        }
        boolean bl = deterministic = a1.isSingleton() && a2.isDeterministic();
        if (a1 == a2) {
            a1 = a1.cloneExpanded();
            a2 = a2.cloneExpanded();
        } else {
            a1 = a1.cloneExpandedIfRequired();
            a2 = a2.cloneExpandedIfRequired();
        }
        for (State s : a1.getAcceptStates()) {
            s.accept = false;
            s.addEpsilon(a2.initial);
        }
        a1.deterministic = deterministic;
        a1.clearNumberedStates();
        a1.checkMinimizeAlways();
        return a1;
    }

    public static Automaton concatenate(List<Automaton> l) {
        if (l.isEmpty()) {
            return BasicAutomata.makeEmptyString();
        }
        boolean all_singleton = true;
        for (Automaton automaton : l) {
            if (automaton.isSingleton()) continue;
            all_singleton = false;
            break;
        }
        if (all_singleton) {
            StringBuilder b = new StringBuilder();
            for (Automaton a : l) {
                b.append(a.singleton);
            }
            return BasicAutomata.makeString(b.toString());
        }
        for (Automaton automaton : l) {
            if (!BasicOperations.isEmpty(automaton)) continue;
            return BasicAutomata.makeEmpty();
        }
        HashSet<Integer> ids = new HashSet<Integer>();
        for (Automaton a : l) {
            ids.add(System.identityHashCode(a));
        }
        boolean bl = ids.size() != l.size();
        Automaton b = l.get(0);
        b = bl ? b.cloneExpanded() : b.cloneExpandedIfRequired();
        Set<State> ac = b.getAcceptStates();
        boolean first = true;
        for (Automaton a : l) {
            if (first) {
                first = false;
                continue;
            }
            if (a.isEmptyString()) continue;
            Automaton aa = a;
            aa = bl ? aa.cloneExpanded() : aa.cloneExpandedIfRequired();
            Set<State> ns = aa.getAcceptStates();
            for (State s : ac) {
                s.accept = false;
                s.addEpsilon(aa.initial);
                if (!s.accept) continue;
                ns.add(s);
            }
            ac = ns;
        }
        b.deterministic = false;
        b.clearNumberedStates();
        b.checkMinimizeAlways();
        return b;
    }

    public static Automaton optional(Automaton a) {
        a = a.cloneExpandedIfRequired();
        State s = new State();
        s.addEpsilon(a.initial);
        s.accept = true;
        a.initial = s;
        a.deterministic = false;
        a.clearNumberedStates();
        a.checkMinimizeAlways();
        return a;
    }

    public static Automaton repeat(Automaton a) {
        a = a.cloneExpanded();
        State s = new State();
        s.accept = true;
        s.addEpsilon(a.initial);
        for (State p : a.getAcceptStates()) {
            p.addEpsilon(s);
        }
        a.initial = s;
        a.deterministic = false;
        a.clearNumberedStates();
        a.checkMinimizeAlways();
        return a;
    }

    public static Automaton repeat(Automaton a, int min) {
        if (min == 0) {
            return BasicOperations.repeat(a);
        }
        ArrayList<Automaton> as = new ArrayList<Automaton>();
        while (min-- > 0) {
            as.add(a);
        }
        as.add(BasicOperations.repeat(a));
        return BasicOperations.concatenate(as);
    }

    public static Automaton repeat(Automaton a, int min, int max) {
        Automaton b;
        if (min > max) {
            return BasicAutomata.makeEmpty();
        }
        max -= min;
        a.expandSingleton();
        if (min == 0) {
            b = BasicAutomata.makeEmptyString();
        } else if (min == 1) {
            b = a.clone();
        } else {
            ArrayList<Automaton> as = new ArrayList<Automaton>();
            while (min-- > 0) {
                as.add(a);
            }
            b = BasicOperations.concatenate(as);
        }
        if (max > 0) {
            Automaton d = a.clone();
            while (--max > 0) {
                Automaton c = a.clone();
                for (State p : c.getAcceptStates()) {
                    p.addEpsilon(d.initial);
                }
                d = c;
            }
            for (State p : b.getAcceptStates()) {
                p.addEpsilon(d.initial);
            }
            b.deterministic = false;
            b.clearNumberedStates();
            b.checkMinimizeAlways();
        }
        return b;
    }

    public static Automaton complement(Automaton a) {
        a = a.cloneExpandedIfRequired();
        a.determinize();
        a.totalize();
        for (State p : a.getNumberedStates()) {
            p.accept = !p.accept;
        }
        a.removeDeadTransitions();
        return a;
    }

    public static Automaton minus(Automaton a1, Automaton a2) {
        if (BasicOperations.isEmpty(a1) || a1 == a2) {
            return BasicAutomata.makeEmpty();
        }
        if (BasicOperations.isEmpty(a2)) {
            return a1.cloneIfRequired();
        }
        if (a1.isSingleton()) {
            if (BasicOperations.run(a2, a1.singleton)) {
                return BasicAutomata.makeEmpty();
            }
            return a1.cloneIfRequired();
        }
        return BasicOperations.intersection(a1, a2.complement());
    }

    public static Automaton intersection(Automaton a1, Automaton a2) {
        if (a1.isSingleton()) {
            if (BasicOperations.run(a2, a1.singleton)) {
                return a1.cloneIfRequired();
            }
            return BasicAutomata.makeEmpty();
        }
        if (a2.isSingleton()) {
            if (BasicOperations.run(a1, a2.singleton)) {
                return a2.cloneIfRequired();
            }
            return BasicAutomata.makeEmpty();
        }
        if (a1 == a2) {
            return a1.cloneIfRequired();
        }
        Transition[][] transitions1 = a1.getSortedTransitions();
        Transition[][] transitions2 = a2.getSortedTransitions();
        Automaton c = new Automaton();
        LinkedList<StatePair> worklist = new LinkedList<StatePair>();
        HashMap<StatePair, StatePair> newstates = new HashMap<StatePair, StatePair>();
        StatePair p = new StatePair(c.initial, a1.initial, a2.initial);
        worklist.add(p);
        newstates.put(p, p);
        while (worklist.size() > 0) {
            p = (StatePair)worklist.removeFirst();
            p.s.accept = p.s1.accept && p.s2.accept;
            Transition[] t1 = transitions1[p.s1.number];
            Transition[] t2 = transitions2[p.s2.number];
            int b2 = 0;
            for (int n1 = 0; n1 < t1.length; ++n1) {
                while (b2 < t2.length && t2[b2].max < t1[n1].min) {
                    ++b2;
                }
                for (int n2 = b2; n2 < t2.length && t1[n1].max >= t2[n2].min; ++n2) {
                    if (t2[n2].max < t1[n1].min) continue;
                    StatePair q = new StatePair(t1[n1].to, t2[n2].to);
                    StatePair r = (StatePair)newstates.get(q);
                    if (r == null) {
                        q.s = new State();
                        worklist.add(q);
                        newstates.put(q, q);
                        r = q;
                    }
                    int min = t1[n1].min > t2[n2].min ? t1[n1].min : t2[n2].min;
                    int max = t1[n1].max < t2[n2].max ? t1[n1].max : t2[n2].max;
                    p.s.addTransition(new Transition(min, max, r.s));
                }
            }
        }
        c.deterministic = a1.deterministic && a2.deterministic;
        c.removeDeadTransitions();
        c.checkMinimizeAlways();
        return c;
    }

    public static boolean sameLanguage(Automaton a1, Automaton a2) {
        if (a1 == a2) {
            return true;
        }
        if (a1.isSingleton() && a2.isSingleton()) {
            return a1.singleton.equals(a2.singleton);
        }
        if (a1.isSingleton()) {
            return BasicOperations.subsetOf(a1, a2) && BasicOperations.subsetOf(a2, a1);
        }
        return BasicOperations.subsetOf(a2, a1) && BasicOperations.subsetOf(a1, a2);
    }

    public static boolean subsetOf(Automaton a1, Automaton a2) {
        if (a1 == a2) {
            return true;
        }
        if (a1.isSingleton()) {
            if (a2.isSingleton()) {
                return a1.singleton.equals(a2.singleton);
            }
            return BasicOperations.run(a2, a1.singleton);
        }
        a2.determinize();
        Transition[][] transitions1 = a1.getSortedTransitions();
        Transition[][] transitions2 = a2.getSortedTransitions();
        LinkedList<StatePair> worklist = new LinkedList<StatePair>();
        HashSet<StatePair> visited = new HashSet<StatePair>();
        StatePair p = new StatePair(a1.initial, a2.initial);
        worklist.add(p);
        visited.add(p);
        while (worklist.size() > 0) {
            p = (StatePair)worklist.removeFirst();
            if (p.s1.accept && !p.s2.accept) {
                return false;
            }
            Transition[] t1 = transitions1[p.s1.number];
            Transition[] t2 = transitions2[p.s2.number];
            int b2 = 0;
            for (int n1 = 0; n1 < t1.length; ++n1) {
                while (b2 < t2.length && t2[b2].max < t1[n1].min) {
                    ++b2;
                }
                int min1 = t1[n1].min;
                int max1 = t1[n1].max;
                for (int n2 = b2; n2 < t2.length && t1[n1].max >= t2[n2].min; ++n2) {
                    if (t2[n2].min > min1) {
                        return false;
                    }
                    if (t2[n2].max < 0x10FFFF) {
                        min1 = t2[n2].max + 1;
                    } else {
                        min1 = 0x10FFFF;
                        max1 = 0;
                    }
                    StatePair q = new StatePair(t1[n1].to, t2[n2].to);
                    if (visited.contains(q)) continue;
                    worklist.add(q);
                    visited.add(q);
                }
                if (min1 > max1) continue;
                return false;
            }
        }
        return true;
    }

    public static Automaton union(Automaton a1, Automaton a2) {
        if (a1.isSingleton() && a2.isSingleton() && a1.singleton.equals(a2.singleton) || a1 == a2) {
            return a1.cloneIfRequired();
        }
        if (a1 == a2) {
            a1 = a1.cloneExpanded();
            a2 = a2.cloneExpanded();
        } else {
            a1 = a1.cloneExpandedIfRequired();
            a2 = a2.cloneExpandedIfRequired();
        }
        State s = new State();
        s.addEpsilon(a1.initial);
        s.addEpsilon(a2.initial);
        a1.initial = s;
        a1.deterministic = false;
        a1.clearNumberedStates();
        a1.checkMinimizeAlways();
        return a1;
    }

    public static Automaton union(Collection<Automaton> l) {
        HashSet<Integer> ids = new HashSet<Integer>();
        for (Automaton a : l) {
            ids.add(System.identityHashCode(a));
        }
        boolean has_aliases = ids.size() != l.size();
        State s = new State();
        for (Automaton b : l) {
            if (BasicOperations.isEmpty(b)) continue;
            Automaton bb = b;
            bb = has_aliases ? bb.cloneExpanded() : bb.cloneExpandedIfRequired();
            s.addEpsilon(bb.initial);
        }
        Automaton a = new Automaton();
        a.initial = s;
        a.deterministic = false;
        a.clearNumberedStates();
        a.checkMinimizeAlways();
        return a;
    }

    public static void determinize(Automaton a) {
        if (a.deterministic || a.isSingleton()) {
            return;
        }
        State[] allStates = a.getNumberedStates();
        boolean initAccept = a.initial.accept;
        int initNumber = a.initial.number;
        a.initial = new State();
        SortedIntSet.FrozenIntSet initialset = new SortedIntSet.FrozenIntSet(initNumber, a.initial);
        LinkedList<SortedIntSet.FrozenIntSet> worklist = new LinkedList<SortedIntSet.FrozenIntSet>();
        HashMap<SortedIntSet.FrozenIntSet, State> newstate = new HashMap<SortedIntSet.FrozenIntSet, State>();
        worklist.add(initialset);
        a.initial.accept = initAccept;
        newstate.put(initialset, a.initial);
        int newStateUpto = 0;
        State[] newStatesArray = new State[5];
        newStatesArray[newStateUpto] = a.initial;
        a.initial.number = newStateUpto++;
        PointTransitionSet points = new PointTransitionSet();
        SortedIntSet statesSet = new SortedIntSet(5);
        while (worklist.size() > 0) {
            SortedIntSet.FrozenIntSet s = (SortedIntSet.FrozenIntSet)worklist.removeFirst();
            for (int i = 0; i < s.values.length; ++i) {
                State s0 = allStates[s.values[i]];
                for (int j = 0; j < s0.numTransitions; ++j) {
                    points.add(s0.transitionsArray[j]);
                }
            }
            if (points.count == 0) continue;
            points.sort();
            int lastPoint = -1;
            int accCount = 0;
            State r = s.state;
            for (int i = 0; i < points.count; ++i) {
                Integer num;
                Transition t;
                int j;
                int point = points.points[i].point;
                if (statesSet.upto > 0) {
                    assert (lastPoint != -1);
                    statesSet.computeHash();
                    State q = (State)newstate.get(statesSet);
                    if (q == null) {
                        q = new State();
                        SortedIntSet.FrozenIntSet p = statesSet.freeze(q);
                        worklist.add(p);
                        if (newStateUpto == newStatesArray.length) {
                            State[] newArray = new State[ArrayUtil.oversize(1 + newStateUpto, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                            System.arraycopy(newStatesArray, 0, newArray, 0, newStateUpto);
                            newStatesArray = newArray;
                        }
                        newStatesArray[newStateUpto] = q;
                        q.number = newStateUpto++;
                        q.accept = accCount > 0;
                        newstate.put(p, q);
                    } else assert (accCount > 0 == q.accept) : "accCount=" + accCount + " vs existing accept=" + q.accept + " states=" + statesSet;
                    r.addTransition(new Transition(lastPoint, point - 1, q));
                }
                Transition[] transitions = points.points[i].ends.transitions;
                int limit = points.points[i].ends.count;
                for (j = 0; j < limit; ++j) {
                    t = transitions[j];
                    num = t.to.number;
                    statesSet.decr(num);
                    accCount -= t.to.accept ? 1 : 0;
                }
                points.points[i].ends.count = 0;
                transitions = points.points[i].starts.transitions;
                limit = points.points[i].starts.count;
                for (j = 0; j < limit; ++j) {
                    t = transitions[j];
                    num = t.to.number;
                    statesSet.incr(num);
                    accCount += t.to.accept ? 1 : 0;
                }
                lastPoint = point;
                points.points[i].starts.count = 0;
            }
            points.reset();
            assert (statesSet.upto == 0) : "upto=" + statesSet.upto;
        }
        a.deterministic = true;
        a.setNumberedStates(newStatesArray, newStateUpto);
    }

    public static void addEpsilons(Automaton a, Collection<StatePair> pairs) {
        a.expandSingleton();
        HashMap<State, HashSet<State>> forward = new HashMap<State, HashSet<State>>();
        HashMap<State, HashSet<State>> back = new HashMap<State, HashSet<State>>();
        for (StatePair p : pairs) {
            HashSet<State> to = (HashSet<State>)forward.get(p.s1);
            if (to == null) {
                to = new HashSet<State>();
                forward.put(p.s1, to);
            }
            to.add(p.s2);
            HashSet<State> from = (HashSet<State>)back.get(p.s2);
            if (from == null) {
                from = new HashSet<State>();
                back.put(p.s2, from);
            }
            from.add(p.s1);
        }
        LinkedList<StatePair> worklist = new LinkedList<StatePair>(pairs);
        HashSet<StatePair> workset = new HashSet<StatePair>(pairs);
        while (!worklist.isEmpty()) {
            StatePair p = worklist.removeFirst();
            workset.remove(p);
            HashSet to = (HashSet)forward.get(p.s2);
            HashSet from = (HashSet)back.get(p.s1);
            if (to == null) continue;
            for (State s : to) {
                StatePair pp = new StatePair(p.s1, s);
                if (pairs.contains(pp)) continue;
                pairs.add(pp);
                ((HashSet)forward.get(p.s1)).add(s);
                ((HashSet)back.get(s)).add(p.s1);
                worklist.add(pp);
                workset.add(pp);
                if (from == null) continue;
                for (State q : from) {
                    StatePair qq = new StatePair(q, p.s1);
                    if (workset.contains(qq)) continue;
                    worklist.add(qq);
                    workset.add(qq);
                }
            }
        }
        for (StatePair p : pairs) {
            p.s1.addEpsilon(p.s2);
        }
        a.deterministic = false;
        a.clearNumberedStates();
        a.checkMinimizeAlways();
    }

    public static boolean isEmptyString(Automaton a) {
        if (a.isSingleton()) {
            return a.singleton.length() == 0;
        }
        return a.initial.accept && a.initial.numTransitions() == 0;
    }

    public static boolean isEmpty(Automaton a) {
        if (a.isSingleton()) {
            return false;
        }
        return !a.initial.accept && a.initial.numTransitions() == 0;
    }

    public static boolean isTotal(Automaton a) {
        if (a.isSingleton()) {
            return false;
        }
        if (a.initial.accept && a.initial.numTransitions() == 1) {
            Transition t = a.initial.getTransitions().iterator().next();
            return t.to == a.initial && t.min == 0 && t.max == 0x10FFFF;
        }
        return false;
    }

    public static boolean run(Automaton a, String s) {
        if (a.isSingleton()) {
            return s.equals(a.singleton);
        }
        if (a.deterministic) {
            State p = a.initial;
            int cp = 0;
            for (int i = 0; i < s.length(); i += Character.charCount(cp)) {
                cp = s.codePointAt(i);
                State q = p.step(cp);
                if (q == null) {
                    return false;
                }
                p = q;
            }
            return p.accept;
        }
        State[] states = a.getNumberedStates();
        LinkedList<State> pp = new LinkedList<State>();
        LinkedList<State> pp_other = new LinkedList<State>();
        BitSet bb = new BitSet(states.length);
        BitSet bb_other = new BitSet(states.length);
        pp.add(a.initial);
        ArrayList<State> dest = new ArrayList<State>();
        boolean accept = a.initial.accept;
        int c = 0;
        for (int i = 0; i < s.length(); i += Character.charCount(c)) {
            c = s.codePointAt(i);
            accept = false;
            pp_other.clear();
            bb_other.clear();
            for (State p : pp) {
                dest.clear();
                p.step(c, dest);
                for (State q : dest) {
                    if (q.accept) {
                        accept = true;
                    }
                    if (bb_other.get(q.number)) continue;
                    bb_other.set(q.number);
                    pp_other.add(q);
                }
            }
            LinkedList<State> tp = pp;
            pp = pp_other;
            pp_other = tp;
            BitSet tb = bb;
            bb = bb_other;
            bb_other = tb;
        }
        return accept;
    }

    private static final class PointTransitionSet {
        int count;
        PointTransitions[] points = new PointTransitions[5];
        private static final int HASHMAP_CUTOVER = 30;
        private final HashMap<Integer, PointTransitions> map = new HashMap();
        private boolean useHash = false;

        private PointTransitionSet() {
        }

        private PointTransitions next(int point) {
            PointTransitions points0;
            if (this.count == this.points.length) {
                PointTransitions[] newArray = new PointTransitions[ArrayUtil.oversize(1 + this.count, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.points, 0, newArray, 0, this.count);
                this.points = newArray;
            }
            if ((points0 = this.points[this.count]) == null) {
                points0 = this.points[this.count] = new PointTransitions();
            }
            points0.reset(point);
            ++this.count;
            return points0;
        }

        private PointTransitions find(int point) {
            if (this.useHash) {
                Integer pi = point;
                PointTransitions p = this.map.get(pi);
                if (p == null) {
                    p = this.next(point);
                    this.map.put(pi, p);
                }
                return p;
            }
            for (int i = 0; i < this.count; ++i) {
                if (this.points[i].point != point) continue;
                return this.points[i];
            }
            PointTransitions p = this.next(point);
            if (this.count == 30) {
                assert (this.map.size() == 0);
                for (int i = 0; i < this.count; ++i) {
                    this.map.put(this.points[i].point, this.points[i]);
                }
                this.useHash = true;
            }
            return p;
        }

        public void reset() {
            if (this.useHash) {
                this.map.clear();
                this.useHash = false;
            }
            this.count = 0;
        }

        public void sort() {
            if (this.count > 1) {
                ArrayUtil.timSort((Comparable[])this.points, (int)0, (int)this.count);
            }
        }

        public void add(Transition t) {
            this.find((int)t.min).starts.add(t);
            this.find((int)(1 + t.max)).ends.add(t);
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < this.count; ++i) {
                if (i > 0) {
                    s.append(' ');
                }
                s.append(this.points[i].point).append(':').append(this.points[i].starts.count).append(',').append(this.points[i].ends.count);
            }
            return s.toString();
        }
    }

    private static final class PointTransitions
    implements Comparable<PointTransitions> {
        int point;
        final TransitionList ends = new TransitionList();
        final TransitionList starts = new TransitionList();

        private PointTransitions() {
        }

        @Override
        public int compareTo(PointTransitions other) {
            return this.point - other.point;
        }

        public void reset(int point) {
            this.point = point;
            this.ends.count = 0;
            this.starts.count = 0;
        }

        public boolean equals(Object other) {
            return ((PointTransitions)other).point == this.point;
        }

        public int hashCode() {
            return this.point;
        }
    }

    private static final class TransitionList {
        Transition[] transitions = new Transition[2];
        int count;

        private TransitionList() {
        }

        public void add(Transition t) {
            if (this.transitions.length == this.count) {
                Transition[] newArray = new Transition[ArrayUtil.oversize(1 + this.count, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
                System.arraycopy(this.transitions, 0, newArray, 0, this.count);
                this.transitions = newArray;
            }
            this.transitions[this.count++] = t;
        }
    }
}

