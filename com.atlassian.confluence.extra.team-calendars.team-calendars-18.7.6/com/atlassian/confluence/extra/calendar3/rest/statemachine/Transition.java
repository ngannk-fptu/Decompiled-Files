/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine;

import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;

public class Transition {
    private EventUpdateStateMachine.States fromState;
    private EventUpdateStateMachine.States toState;
    private EventUpdateStateMachine.Events trigger;

    public EventUpdateStateMachine.States getFromState() {
        return this.fromState;
    }

    public EventUpdateStateMachine.States getToState() {
        return this.toState;
    }

    public EventUpdateStateMachine.Events getTrigger() {
        return this.trigger;
    }

    public Transition(EventUpdateStateMachine.States fromState, EventUpdateStateMachine.Events onEvent, EventUpdateStateMachine.States toState) {
        this.fromState = fromState;
        this.toState = toState;
        this.trigger = onEvent;
    }
}

