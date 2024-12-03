/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;

public final class MinimizationOperations {
    private MinimizationOperations() {
    }

    public static void minimize(Automaton a) {
        if (!a.isSingleton()) {
            MinimizationOperations.minimizeHopcroft(a);
        }
    }

    public static void minimizeHopcroft(Automaton a) {
        int n;
        int q;
        a.determinize();
        if (a.initial.numTransitions == 1) {
            Transition t = a.initial.transitionsArray[0];
            if (t.to == a.initial && t.min == 0 && t.max == 0x10FFFF) {
                return;
            }
        }
        a.totalize();
        int[] sigma = a.getStartPoints();
        State[] states = a.getNumberedStates();
        int sigmaLen = sigma.length;
        int statesLen = states.length;
        ArrayList[][] reverse = new ArrayList[statesLen][sigmaLen];
        HashSet[] partition = new HashSet[statesLen];
        ArrayList[] splitblock = new ArrayList[statesLen];
        int[] block = new int[statesLen];
        StateList[][] active = new StateList[statesLen][sigmaLen];
        StateListNode[][] active2 = new StateListNode[statesLen][sigmaLen];
        LinkedList<IntPair> pending = new LinkedList<IntPair>();
        BitSet pending2 = new BitSet(sigmaLen * statesLen);
        BitSet split = new BitSet(statesLen);
        BitSet refine = new BitSet(statesLen);
        BitSet refine2 = new BitSet(statesLen);
        for (q = 0; q < statesLen; ++q) {
            splitblock[q] = new ArrayList();
            partition[q] = new HashSet();
            for (int x = 0; x < sigmaLen; ++x) {
                active[q][x] = new StateList();
            }
        }
        for (q = 0; q < statesLen; ++q) {
            State qq = states[q];
            int j = qq.accept ? 0 : 1;
            partition[j].add(qq);
            block[q] = j;
            for (int x = 0; x < sigmaLen; ++x) {
                ArrayList[] r = reverse[qq.step((int)sigma[x]).number];
                if (r[x] == null) {
                    r[x] = new ArrayList();
                }
                r[x].add(qq);
            }
        }
        for (int j = 0; j <= 1; ++j) {
            for (int x = 0; x < sigmaLen; ++x) {
                for (State qq : partition[j]) {
                    if (reverse[qq.number][x] == null) continue;
                    active2[qq.number][x] = active[j][x].add(qq);
                }
            }
        }
        for (int x = 0; x < sigmaLen; ++x) {
            int j = active[0][x].size <= active[1][x].size ? 0 : 1;
            pending.add(new IntPair(j, x));
            pending2.set(x * statesLen + j);
        }
        int k = 2;
        while (!pending.isEmpty()) {
            IntPair ip = (IntPair)pending.removeFirst();
            int p = ip.n1;
            int x = ip.n2;
            pending2.clear(x * statesLen + p);
            StateListNode m = active[p][x].first;
            while (m != null) {
                ArrayList r = reverse[m.q.number][x];
                if (r != null) {
                    for (State s : r) {
                        int i = s.number;
                        if (split.get(i)) continue;
                        split.set(i);
                        int j = block[i];
                        splitblock[j].add(s);
                        if (refine2.get(j)) continue;
                        refine2.set(j);
                        refine.set(j);
                    }
                }
                m = m.next;
            }
            int j = refine.nextSetBit(0);
            while (j >= 0) {
                ArrayList sb = splitblock[j];
                if (sb.size() < partition[j].size()) {
                    HashSet b1 = partition[j];
                    HashSet b2 = partition[k];
                    for (State s : sb) {
                        b1.remove(s);
                        b2.add(s);
                        block[s.number] = k;
                        for (int c = 0; c < sigmaLen; ++c) {
                            StateListNode sn = active2[s.number][c];
                            if (sn == null || sn.sl != active[j][c]) continue;
                            sn.remove();
                            active2[s.number][c] = active[k][c].add(s);
                        }
                    }
                    for (int c = 0; c < sigmaLen; ++c) {
                        int aj = active[j][c].size;
                        int ak = active[k][c].size;
                        int ofs = c * statesLen;
                        if (!pending2.get(ofs + j) && 0 < aj && aj <= ak) {
                            pending2.set(ofs + j);
                            pending.add(new IntPair(j, c));
                            continue;
                        }
                        pending2.set(ofs + k);
                        pending.add(new IntPair(k, c));
                    }
                    ++k;
                }
                refine2.clear(j);
                for (State s : sb) {
                    split.clear(s.number);
                }
                sb.clear();
                j = refine.nextSetBit(j + 1);
            }
            refine.clear();
        }
        State[] newstates = new State[k];
        for (n = 0; n < newstates.length; ++n) {
            State s;
            newstates[n] = s = new State();
            for (State q2 : partition[n]) {
                if (q2 == a.initial) {
                    a.initial = s;
                }
                s.accept = q2.accept;
                s.number = q2.number;
                q2.number = n;
            }
        }
        for (n = 0; n < newstates.length; ++n) {
            State s = newstates[n];
            s.accept = states[s.number].accept;
            for (Transition t : states[s.number].getTransitions()) {
                s.addTransition(new Transition(t.min, t.max, newstates[t.to.number]));
            }
        }
        a.clearNumberedStates();
        a.removeDeadTransitions();
    }

    static final class StateListNode {
        final State q;
        StateListNode next;
        StateListNode prev;
        final StateList sl;

        StateListNode(State q, StateList sl) {
            this.q = q;
            this.sl = sl;
            if (sl.size++ == 0) {
                sl.first = sl.last = this;
            } else {
                sl.last.next = this;
                this.prev = sl.last;
                sl.last = this;
            }
        }

        void remove() {
            --this.sl.size;
            if (this.sl.first == this) {
                this.sl.first = this.next;
            } else {
                this.prev.next = this.next;
            }
            if (this.sl.last == this) {
                this.sl.last = this.prev;
            } else {
                this.next.prev = this.prev;
            }
        }
    }

    static final class StateList {
        int size;
        StateListNode first;
        StateListNode last;

        StateList() {
        }

        StateListNode add(State q) {
            return new StateListNode(q, this);
        }
    }

    static final class IntPair {
        final int n1;
        final int n2;

        IntPair(int n1, int n2) {
            this.n1 = n1;
            this.n2 = n2;
        }
    }
}

