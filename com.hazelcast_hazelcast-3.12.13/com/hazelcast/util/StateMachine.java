/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util;

import com.hazelcast.util.Preconditions;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateMachine<T extends Enum<T>> {
    private Map<T, Set<T>> transitions = new HashMap<T, Set<T>>();
    private T currentState;

    public StateMachine(T initialState) {
        this.currentState = initialState;
    }

    public static <T extends Enum<T>> StateMachine<T> of(T initialState) {
        return new StateMachine<T>(initialState);
    }

    public StateMachine<T> withTransition(T from, T to, T ... moreTo) {
        this.transitions.put(from, EnumSet.of(to, moreTo));
        return this;
    }

    public StateMachine<T> next(T nextState) throws IllegalStateException {
        Set<T> allowed = this.transitions.get(this.currentState);
        Preconditions.checkNotNull(allowed, "No transitions from state " + this.currentState);
        Preconditions.checkState(allowed.contains(nextState), "Transition not allowed from state " + this.currentState + " to " + nextState);
        this.currentState = nextState;
        return this;
    }

    public void nextOrStay(T nextState) {
        if (!this.is((Enum)nextState, new Enum[0])) {
            this.next(nextState);
        }
    }

    public boolean is(T state, T ... otherStates) {
        return EnumSet.of(state, otherStates).contains(this.currentState);
    }

    public String toString() {
        return "StateMachine{state=" + this.currentState + "}";
    }
}

