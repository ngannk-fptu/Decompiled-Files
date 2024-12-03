/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.ls;

import org.w3c.dom.Document;
import org.w3c.dom.events.Event;
import org.w3c.dom.ls.LSInput;

public interface LSLoadEvent
extends Event {
    public Document getNewDocument();

    public LSInput getInput();
}

