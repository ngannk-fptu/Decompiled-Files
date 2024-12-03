/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.events;

import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

public interface MouseEvent
extends UIEvent {
    public int getScreenX();

    public int getScreenY();

    public int getClientX();

    public int getClientY();

    public boolean getCtrlKey();

    public boolean getShiftKey();

    public boolean getAltKey();

    public boolean getMetaKey();

    public short getButton();

    public EventTarget getRelatedTarget();

    public void initMouseEvent(String var1, boolean var2, boolean var3, AbstractView var4, int var5, int var6, int var7, int var8, int var9, boolean var10, boolean var11, boolean var12, boolean var13, short var14, EventTarget var15);
}

