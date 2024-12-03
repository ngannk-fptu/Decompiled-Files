/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractEvent;

public class DSCHeaderComment
extends AbstractEvent {
    private String comment;

    public DSCHeaderComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean isPSAdobe30() {
        return this.getComment().startsWith("%!PS-Adobe-3.0".substring(2));
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.writeln("%!" + this.getComment());
    }

    @Override
    public int getEventType() {
        return 0;
    }

    @Override
    public boolean isHeaderComment() {
        return true;
    }
}

