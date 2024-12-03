/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import org.apache.lucene.util.automaton.State;

public class StatePair {
    State s;
    State s1;
    State s2;

    StatePair(State s, State s1, State s2) {
        this.s = s;
        this.s1 = s1;
        this.s2 = s2;
    }

    public StatePair(State s1, State s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public State getFirstState() {
        return this.s1;
    }

    public State getSecondState() {
        return this.s2;
    }

    public boolean equals(Object obj) {
        if (obj instanceof StatePair) {
            StatePair p = (StatePair)obj;
            return p.s1 == this.s1 && p.s2 == this.s2;
        }
        return false;
    }

    public int hashCode() {
        return this.s1.hashCode() + this.s2.hashCode();
    }
}

