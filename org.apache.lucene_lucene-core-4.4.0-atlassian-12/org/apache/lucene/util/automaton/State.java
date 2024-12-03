/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.automaton.Transition;

public class State
implements Comparable<State> {
    boolean accept;
    public Transition[] transitionsArray;
    public int numTransitions;
    int number;
    int id;
    static int next_id;

    public State() {
        this.resetTransitions();
        this.id = next_id++;
    }

    final void resetTransitions() {
        this.transitionsArray = new Transition[0];
        this.numTransitions = 0;
    }

    public Iterable<Transition> getTransitions() {
        return new TransitionsIterable();
    }

    public int numTransitions() {
        return this.numTransitions;
    }

    public void setTransitions(Transition[] transitions) {
        this.numTransitions = transitions.length;
        this.transitionsArray = transitions;
    }

    public void addTransition(Transition t) {
        if (this.numTransitions == this.transitionsArray.length) {
            Transition[] newArray = new Transition[ArrayUtil.oversize(1 + this.numTransitions, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
            System.arraycopy(this.transitionsArray, 0, newArray, 0, this.numTransitions);
            this.transitionsArray = newArray;
        }
        this.transitionsArray[this.numTransitions++] = t;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public boolean isAccept() {
        return this.accept;
    }

    public State step(int c) {
        assert (c >= 0);
        for (int i = 0; i < this.numTransitions; ++i) {
            Transition t = this.transitionsArray[i];
            if (t.min > c || c > t.max) continue;
            return t.to;
        }
        return null;
    }

    public void step(int c, Collection<State> dest) {
        for (int i = 0; i < this.numTransitions; ++i) {
            Transition t = this.transitionsArray[i];
            if (t.min > c || c > t.max) continue;
            dest.add(t.to);
        }
    }

    void addEpsilon(State to) {
        if (to.accept) {
            this.accept = true;
        }
        for (Transition t : to.getTransitions()) {
            this.addTransition(t);
        }
    }

    public void trimTransitionsArray() {
        if (this.numTransitions < this.transitionsArray.length) {
            Transition[] newArray = new Transition[this.numTransitions];
            System.arraycopy(this.transitionsArray, 0, newArray, 0, this.numTransitions);
            this.transitionsArray = newArray;
        }
    }

    public void reduce() {
        if (this.numTransitions <= 1) {
            return;
        }
        this.sortTransitions(Transition.CompareByDestThenMinMax);
        State p = null;
        int min = -1;
        int max = -1;
        int upto = 0;
        for (int i = 0; i < this.numTransitions; ++i) {
            Transition t = this.transitionsArray[i];
            if (p == t.to) {
                if (t.min <= max + 1) {
                    if (t.max <= max) continue;
                    max = t.max;
                    continue;
                }
                if (p != null) {
                    this.transitionsArray[upto++] = new Transition(min, max, p);
                }
                min = t.min;
                max = t.max;
                continue;
            }
            if (p != null) {
                this.transitionsArray[upto++] = new Transition(min, max, p);
            }
            p = t.to;
            min = t.min;
            max = t.max;
        }
        if (p != null) {
            this.transitionsArray[upto++] = new Transition(min, max, p);
        }
        this.numTransitions = upto;
    }

    public void sortTransitions(Comparator<Transition> comparator) {
        if (this.numTransitions > 1) {
            ArrayUtil.timSort(this.transitionsArray, 0, this.numTransitions, comparator);
        }
    }

    public int getNumber() {
        return this.number;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("state ").append(this.number);
        if (this.accept) {
            b.append(" [accept]");
        } else {
            b.append(" [reject]");
        }
        b.append(":\n");
        for (Transition t : this.getTransitions()) {
            b.append("  ").append(t.toString()).append("\n");
        }
        return b.toString();
    }

    @Override
    public int compareTo(State s) {
        return s.id - this.id;
    }

    public int hashCode() {
        return this.id;
    }

    private class TransitionsIterable
    implements Iterable<Transition> {
        private TransitionsIterable() {
        }

        @Override
        public Iterator<Transition> iterator() {
            return new Iterator<Transition>(){
                int upto;

                @Override
                public boolean hasNext() {
                    return this.upto < State.this.numTransitions;
                }

                @Override
                public Transition next() {
                    return State.this.transitionsArray[this.upto++];
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}

