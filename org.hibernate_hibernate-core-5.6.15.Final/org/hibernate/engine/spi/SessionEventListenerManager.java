/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.SessionEventListener;

public interface SessionEventListenerManager
extends SessionEventListener {
    public void addListener(SessionEventListener ... var1);
}

