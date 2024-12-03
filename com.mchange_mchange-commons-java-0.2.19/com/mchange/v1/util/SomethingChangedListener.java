/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import com.mchange.v1.util.SomethingChangedEvent;
import java.util.EventListener;

public interface SomethingChangedListener
extends EventListener {
    public void somethingChanged(SomethingChangedEvent var1);
}

