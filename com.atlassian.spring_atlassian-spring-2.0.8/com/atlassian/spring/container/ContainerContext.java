/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 */
package com.atlassian.spring.container;

import com.atlassian.event.Event;
import com.atlassian.spring.container.ComponentNotFoundException;

public interface ContainerContext {
    public Object getComponent(Object var1) throws ComponentNotFoundException;

    public Object createComponent(Class var1);

    public Object createCompleteComponent(Class var1);

    public void autowireComponent(Object var1);

    public void refresh();

    public boolean isSetup();

    public void publishEvent(Event var1);
}

