/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom.events;

import org.apache.xerces.dom.events.UIEventImpl;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.views.AbstractView;

public class MouseEventImpl
extends UIEventImpl
implements MouseEvent {
    private int fScreenX;
    private int fScreenY;
    private int fClientX;
    private int fClientY;
    private boolean fCtrlKey;
    private boolean fAltKey;
    private boolean fShiftKey;
    private boolean fMetaKey;
    private short fButton;
    private EventTarget fRelatedTarget;

    @Override
    public int getScreenX() {
        return this.fScreenX;
    }

    @Override
    public int getScreenY() {
        return this.fScreenY;
    }

    @Override
    public int getClientX() {
        return this.fClientX;
    }

    @Override
    public int getClientY() {
        return this.fClientY;
    }

    @Override
    public boolean getCtrlKey() {
        return this.fCtrlKey;
    }

    @Override
    public boolean getAltKey() {
        return this.fAltKey;
    }

    @Override
    public boolean getShiftKey() {
        return this.fShiftKey;
    }

    @Override
    public boolean getMetaKey() {
        return this.fMetaKey;
    }

    @Override
    public short getButton() {
        return this.fButton;
    }

    @Override
    public EventTarget getRelatedTarget() {
        return this.fRelatedTarget;
    }

    @Override
    public void initMouseEvent(String string, boolean bl, boolean bl2, AbstractView abstractView, int n, int n2, int n3, int n4, int n5, boolean bl3, boolean bl4, boolean bl5, boolean bl6, short s, EventTarget eventTarget) {
        this.fScreenX = n2;
        this.fScreenY = n3;
        this.fClientX = n4;
        this.fClientY = n5;
        this.fCtrlKey = bl3;
        this.fAltKey = bl4;
        this.fShiftKey = bl5;
        this.fMetaKey = bl6;
        this.fButton = s;
        this.fRelatedTarget = eventTarget;
        super.initUIEvent(string, bl, bl2, abstractView, n);
    }
}

