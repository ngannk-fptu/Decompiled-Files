/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax.events;

import com.sun.xml.fastinfoset.stax.events.EventBase;
import javax.xml.stream.events.EndDocument;

public class EndDocumentEvent
extends EventBase
implements EndDocument {
    public EndDocumentEvent() {
        super(8);
    }

    public String toString() {
        return "<? EndDocument ?>";
    }
}

