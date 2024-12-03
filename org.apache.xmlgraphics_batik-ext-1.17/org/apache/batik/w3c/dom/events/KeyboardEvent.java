/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.w3c.dom.events;

import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

public interface KeyboardEvent
extends UIEvent {
    public static final int DOM_KEY_LOCATION_STANDARD = 0;
    public static final int DOM_KEY_LOCATION_LEFT = 1;
    public static final int DOM_KEY_LOCATION_RIGHT = 2;
    public static final int DOM_KEY_LOCATION_NUMPAD = 3;

    public String getKeyIdentifier();

    public int getKeyLocation();

    public boolean getCtrlKey();

    public boolean getShiftKey();

    public boolean getAltKey();

    public boolean getMetaKey();

    public boolean getModifierState(String var1);

    public void initKeyboardEvent(String var1, boolean var2, boolean var3, AbstractView var4, String var5, int var6, String var7);

    public void initKeyboardEventNS(String var1, String var2, boolean var3, boolean var4, AbstractView var5, String var6, int var7, String var8);
}

