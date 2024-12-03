/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine;

import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;

public abstract class Context {
    protected EventUpdateStateMachine stateMachine;

    public void forStateMachine(EventUpdateStateMachine stateMachine) {
        this.stateMachine = stateMachine;
    }

    public EventUpdateStateMachine getStateMachine() {
        return this.stateMachine;
    }

    public void trigger(EventUpdateStateMachine.Events event) throws Exception {
        this.stateMachine.trigger(event);
    }
}

