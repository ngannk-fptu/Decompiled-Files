/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;

public class DSCCommentEndOfFile
extends AbstractDSCComment {
    @Override
    public String getName() {
        return "EOF";
    }

    @Override
    public boolean hasValues() {
        return false;
    }

    @Override
    public void parseValue(String value) {
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.writeDSCComment(this.getName());
    }

    @Override
    public int getEventType() {
        return 4;
    }
}

