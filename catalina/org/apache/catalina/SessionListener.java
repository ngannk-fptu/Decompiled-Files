/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.util.EventListener;
import org.apache.catalina.SessionEvent;

public interface SessionListener
extends EventListener {
    public void sessionEvent(SessionEvent var1);
}

