/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;
import org.apache.xmlgraphics.ps.dsc.events.PostScriptLine;

public abstract class AbstractEvent
implements DSCEvent {
    @Override
    public boolean isComment() {
        return false;
    }

    @Override
    public boolean isDSCComment() {
        return false;
    }

    @Override
    public boolean isHeaderComment() {
        return false;
    }

    @Override
    public boolean isLine() {
        return false;
    }

    @Override
    public DSCComment asDSCComment() {
        throw new ClassCastException(this.getClass().getName());
    }

    @Override
    public PostScriptLine asLine() {
        throw new ClassCastException(this.getClass().getName());
    }
}

