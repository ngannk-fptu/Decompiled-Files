/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.events;

import org.apache.batik.dom.events.EventSupport;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public interface NodeEventTarget
extends EventTarget {
    public EventSupport getEventSupport();

    public NodeEventTarget getParentNodeEventTarget();

    @Override
    public boolean dispatchEvent(Event var1) throws EventException, DOMException;

    public void addEventListenerNS(String var1, String var2, EventListener var3, boolean var4, Object var5);

    public void removeEventListenerNS(String var1, String var2, EventListener var3, boolean var4);
}

