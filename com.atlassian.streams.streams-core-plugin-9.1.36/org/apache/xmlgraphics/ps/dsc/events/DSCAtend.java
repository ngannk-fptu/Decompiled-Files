/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.DSCCommentFactory;
import org.apache.xmlgraphics.ps.dsc.events.AbstractDSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;

public class DSCAtend
extends AbstractDSCComment {
    private String name;

    public DSCAtend(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasValues() {
        return false;
    }

    @Override
    public boolean isAtend() {
        return true;
    }

    @Override
    public void parseValue(String value) {
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.writeDSCComment(this.getName(), DSCConstants.ATEND);
    }

    public DSCComment createDSCCommentFromAtend() {
        return DSCCommentFactory.createDSCCommentFor(this.getName());
    }
}

