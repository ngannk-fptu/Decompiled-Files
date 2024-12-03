/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.manager;

import java.util.concurrent.atomic.AtomicReference;

class StateTracker {
    private final AtomicReference<State> state = new AtomicReference<State>(State.NOT_STARTED);

    StateTracker() {
    }

    public State get() {
        return this.state.get();
    }

    StateTracker setState(State newState) {
        State oldState;
        do {
            oldState = this.get();
            oldState.check(newState);
        } while (!this.state.compareAndSet(oldState, newState));
        return this;
    }

    public String toString() {
        return this.get().toString();
    }

    static enum State {
        NOT_STARTED{

            @Override
            void check(State newState) {
                if (newState != STARTING && newState != SHUTTING_DOWN) {
                    this.illegalState(newState);
                }
            }
        }
        ,
        STARTING,
        DELAYED{

            @Override
            void check(State newState) {
                if (newState != RESUMING && newState != SHUTTING_DOWN) {
                    this.illegalState(newState);
                }
            }
        }
        ,
        RESUMING,
        STARTED{

            @Override
            void check(State newState) {
                if (newState != WARM_RESTARTING && newState != SHUTTING_DOWN) {
                    this.illegalState(newState);
                }
            }
        }
        ,
        WARM_RESTARTING{

            @Override
            void check(State newState) {
                if (newState != STARTED) {
                    this.illegalState(newState);
                }
            }
        }
        ,
        SHUTTING_DOWN,
        SHUTDOWN{

            @Override
            void check(State newState) {
                if (newState != STARTING) {
                    this.illegalState(newState);
                }
            }
        };


        void check(State newState) {
            if (this.ordinal() + 1 != newState.ordinal()) {
                this.illegalState(newState);
            }
        }

        void illegalState(State newState) {
            throw new IllegalStateException("Cannot go from State: " + (Object)((Object)this) + " to: " + (Object)((Object)newState));
        }
    }
}

