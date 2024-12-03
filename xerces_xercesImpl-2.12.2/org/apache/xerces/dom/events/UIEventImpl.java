/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom.events;

import org.apache.xerces.dom.events.EventImpl;
import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

public class UIEventImpl
extends EventImpl
implements UIEvent {
    private AbstractView fView;
    private int fDetail;

    @Override
    public AbstractView getView() {
        return this.fView;
    }

    @Override
    public int getDetail() {
        return this.fDetail;
    }

    @Override
    public void initUIEvent(String string, boolean bl, boolean bl2, AbstractView abstractView, int n) {
        this.fView = abstractView;
        this.fDetail = n;
        super.initEvent(string, bl, bl2);
    }
}

