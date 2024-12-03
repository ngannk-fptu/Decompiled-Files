/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.rest.statemachine;

import com.atlassian.confluence.extra.calendar3.rest.statemachine.Context;
import com.atlassian.confluence.extra.calendar3.rest.statemachine.EventUpdateStateMachine;

public interface StateHandler<C extends Context> {
    public void onState(C var1) throws Exception;

    public void register(EventUpdateStateMachine var1);
}

