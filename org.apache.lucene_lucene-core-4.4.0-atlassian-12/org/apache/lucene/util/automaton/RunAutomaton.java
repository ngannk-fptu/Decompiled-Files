/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.SpecialOperations;
import org.apache.lucene.util.automaton.State;
import org.apache.lucene.util.automaton.Transition;

public abstract class RunAutomaton {
    final int maxInterval;
    final int size;
    final boolean[] accept;
    final int initial;
    final int[] transitions;
    final int[] points;
    final int[] classmap;

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("initial state: ").append(this.initial).append("\n");
        for (int i = 0; i < this.size; ++i) {
            b.append("state " + i);
            if (this.accept[i]) {
                b.append(" [accept]:\n");
            } else {
                b.append(" [reject]:\n");
            }
            for (int j = 0; j < this.points.length; ++j) {
                int k = this.transitions[i * this.points.length + j];
                if (k == -1) continue;
                int min = this.points[j];
                int max = j + 1 < this.points.length ? this.points[j + 1] - 1 : this.maxInterval;
                b.append(" ");
                Transition.appendCharString(min, b);
                if (min != max) {
                    b.append("-");
                    Transition.appendCharString(max, b);
                }
                b.append(" -> ").append(k).append("\n");
            }
        }
        return b.toString();
    }

    public final int getSize() {
        return this.size;
    }

    public final boolean isAccept(int state) {
        return this.accept[state];
    }

    public final int getInitialState() {
        return this.initial;
    }

    public final int[] getCharIntervals() {
        return (int[])this.points.clone();
    }

    final int getCharClass(int c) {
        return SpecialOperations.findIndex(c, this.points);
    }

    public RunAutomaton(Automaton a, int maxInterval, boolean tableize) {
        this.maxInterval = maxInterval;
        a.determinize();
        this.points = a.getStartPoints();
        State[] states = a.getNumberedStates();
        this.initial = a.initial.number;
        this.size = states.length;
        this.accept = new boolean[this.size];
        this.transitions = new int[this.size * this.points.length];
        for (int n = 0; n < this.size * this.points.length; ++n) {
            this.transitions[n] = -1;
        }
        for (State s : states) {
            int n = s.number;
            this.accept[n] = s.accept;
            for (int c = 0; c < this.points.length; ++c) {
                State q = s.step(this.points[c]);
                if (q == null) continue;
                this.transitions[n * this.points.length + c] = q.number;
            }
        }
        if (tableize) {
            this.classmap = new int[maxInterval + 1];
            int i = 0;
            for (int j = 0; j <= maxInterval; ++j) {
                if (i + 1 >= this.points.length || j == this.points[i + 1]) {
                    // empty if block
                }
                this.classmap[j] = ++i;
            }
        } else {
            this.classmap = null;
        }
    }

    public final int step(int state, int c) {
        if (this.classmap == null) {
            return this.transitions[state * this.points.length + this.getCharClass(c)];
        }
        return this.transitions[state * this.points.length + this.classmap[c]];
    }
}

