/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine;

import com.atlassian.confluence.extra.calendar3.rest.statemachine.Context;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.StateHandler;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.Transition;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventUpdateStateMachine<C extends Context> {
    private States currentState;
    private List<Transition> transitionList;
    private Map<States, List<StateHandler>> stateHandlers = new HashMap<States, List<StateHandler>>();
    private Map<Events, List<StateHandler>> eventhandlers = new HashMap<Events, List<StateHandler>>();
    private final C context;

    public EventUpdateStateMachine(C context) {
        this.context = context;
        ((Context)this.context).forStateMachine(this);
        this.transitionList = Lists.newArrayList((Object[])new Transition[]{new Transition(States.START, Events.create, States.CREATING_EVENT), new Transition(States.START, Events.update, States.UPDATING_EVENT), new Transition(States.UPDATING_EVENT, Events.update_original, States.UPDATING_ORIGINAL_EVENT), new Transition(States.UPDATING_ORIGINAL_EVENT, Events.move, States.MOVE_EVENT), new Transition(States.UPDATING_ORIGINAL_EVENT, Events.change_event_type, States.CHANGE_EVENT_TYPE), new Transition(States.UPDATING_ORIGINAL_EVENT, Events.update, States.UPDATE_EVENT), new Transition(States.UPDATING_EVENT, Events.update_reschedule, States.UPDATE_RESCHEDULED_EVENT), new Transition(States.UPDATE_RESCHEDULED_EVENT, Events.move, States.MOVE_EVENT), new Transition(States.UPDATE_RESCHEDULED_EVENT, Events.change_event_type, States.CHANGE_EVENT_TYPE), new Transition(States.UPDATE_RESCHEDULED_EVENT, Events.update, States.UPDATED_RESCHEDULED_EVENT)});
    }

    public States getCurrentState() {
        return this.currentState;
    }

    public void start(States beginState) {
        this.currentState = beginState;
    }

    public void trigger(Events trigger) throws Exception {
        Transition matchedTransition = (Transition)Iterables.getFirst((Iterable)Collections2.filter(this.transitionList, transition -> transition.getFromState().equals((Object)this.currentState) && transition.getTrigger().equals((Object)trigger)), null);
        Preconditions.checkNotNull((Object)matchedTransition, (Object)String.format("Could not find next Transition, Current state is %s and the event is %s", this.currentState.toString(), trigger.toString()));
        this.triggerOnEvent(trigger);
        this.currentState = matchedTransition.getToState();
        this.triggerOnState(this.currentState);
    }

    private void triggerOnState(States currentState) throws Exception {
        List<StateHandler> handlers = this.stateHandlers.get((Object)currentState);
        if (handlers == null) {
            return;
        }
        for (StateHandler handler : handlers) {
            handler.onState(this.context);
        }
    }

    private void triggerOnEvent(Events trigger) throws Exception {
        List<StateHandler> handlers = this.eventhandlers.get((Object)trigger);
        if (handlers == null) {
            return;
        }
        for (StateHandler handler : handlers) {
            handler.onState(this.context);
        }
    }

    public void onState(States state, StateHandler handler) {
        Preconditions.checkNotNull((Object)handler, (Object)"Handler should not be null");
        List<StateHandler> foundEntry = this.stateHandlers.get((Object)state);
        if (foundEntry == null) {
            this.stateHandlers.put(state, Lists.newArrayList((Object[])new StateHandler[]{handler}));
            return;
        }
        this.stateHandlers.get((Object)state).add(handler);
    }

    public void onEvent(Events event, StateHandler handler) {
        Preconditions.checkNotNull((Object)handler, (Object)"Handler should not be null");
        List<StateHandler> foundEntry = this.eventhandlers.get((Object)event);
        if (foundEntry == null) {
            this.eventhandlers.put(event, Lists.newArrayList((Object[])new StateHandler[]{handler}));
            return;
        }
        this.eventhandlers.get((Object)event).add(handler);
    }

    public void registerHandler(List<StateHandler> handlers) {
        handlers = handlers == null ? Lists.newArrayList() : handlers;
        for (StateHandler stateHandler : handlers) {
            stateHandler.register(this);
        }
    }

    public static enum Events {
        create,
        update,
        update_original,
        update_reschedule,
        move,
        change_event_type;

    }

    public static enum States {
        START,
        CREATING_EVENT,
        UPDATING_EVENT,
        UPDATING_ORIGINAL_EVENT,
        UPDATE_RESCHEDULED_EVENT,
        UPDATED_RESCHEDULED_EVENT,
        MOVE_EVENT,
        CHANGE_EVENT_TYPE,
        UPDATE_EVENT,
        RESCHEDULE_EVENT;

    }
}

