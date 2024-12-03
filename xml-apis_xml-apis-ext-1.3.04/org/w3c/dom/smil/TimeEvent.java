/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.smil;

import org.w3c.dom.events.Event;
import org.w3c.dom.views.AbstractView;

public interface TimeEvent
extends Event {
    public AbstractView getView();

    public int getDetail();

    public void initTimeEvent(String var1, AbstractView var2, int var3);
}

