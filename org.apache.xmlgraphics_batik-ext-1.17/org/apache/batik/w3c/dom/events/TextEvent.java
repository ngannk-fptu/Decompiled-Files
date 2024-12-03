/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.w3c.dom.events;

import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

public interface TextEvent
extends UIEvent {
    public String getData();

    public void initTextEvent(String var1, boolean var2, boolean var3, AbstractView var4, String var5);

    public void initTextEventNS(String var1, String var2, boolean var3, boolean var4, AbstractView var5, String var6);
}

