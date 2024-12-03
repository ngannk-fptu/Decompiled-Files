/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event;

import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.event.OperationEvent;
import java.util.List;

public interface EventStore {
    public String getCurrentEventToken(List<Long> var1);

    @Deprecated
    public Events getNewEvents(String var1, List<Long> var2) throws EventTokenExpiredException;

    public Events getNewEvents(String var1, Application var2) throws EventTokenExpiredException;

    public void storeOperationEvent(OperationEvent var1);

    public void handleApplicationEvent(Object var1);
}

