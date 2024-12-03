/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.events;

import org.w3c.dom.events.Event;
import org.w3c.dom.views.AbstractView;

public interface UIEvent
extends Event {
    public AbstractView getView();

    public int getDetail();

    public void initUIEvent(String var1, boolean var2, boolean var3, AbstractView var4, int var5);
}

