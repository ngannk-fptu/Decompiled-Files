/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.ls;

import org.w3c.dom.events.Event;
import org.w3c.dom.ls.LSInput;

public interface LSProgressEvent
extends Event {
    public LSInput getInput();

    public int getPosition();

    public int getTotalSize();
}

