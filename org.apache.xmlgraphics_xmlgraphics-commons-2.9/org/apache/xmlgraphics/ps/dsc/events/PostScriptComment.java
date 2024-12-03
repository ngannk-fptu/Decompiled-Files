/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractEvent;

public class PostScriptComment
extends AbstractEvent {
    private String comment;

    public PostScriptComment(String comment) {
        this.comment = comment != null && comment.startsWith("%") ? comment.substring(1) : comment;
    }

    public String getComment() {
        return this.comment;
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.commentln("%" + this.getComment());
    }

    @Override
    public int getEventType() {
        return 2;
    }

    @Override
    public boolean isComment() {
        return true;
    }
}

