/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.SomethingChangedEvent;
import com.mchange.v1.util.SomethingChangedListener;
import java.util.Enumeration;
import java.util.Vector;

public class SomethingChangedEventSupport {
    Object source;
    Vector listeners = new Vector();

    public SomethingChangedEventSupport(Object object) {
        this.source = object;
    }

    public synchronized void addSomethingChangedListener(SomethingChangedListener somethingChangedListener) {
        if (!this.listeners.contains(somethingChangedListener)) {
            this.listeners.addElement(somethingChangedListener);
        }
    }

    public synchronized void removeSomethingChangedListener(SomethingChangedListener somethingChangedListener) {
        this.listeners.removeElement(somethingChangedListener);
    }

    public synchronized void fireSomethingChanged() {
        SomethingChangedEvent somethingChangedEvent = new SomethingChangedEvent(this.source);
        Enumeration enumeration = this.listeners.elements();
        while (enumeration.hasMoreElements()) {
            SomethingChangedListener somethingChangedListener = (SomethingChangedListener)enumeration.nextElement();
            somethingChangedListener.somethingChanged(somethingChangedEvent);
        }
    }
}

