/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractEvent;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;

public class UnparsedDSCComment
extends AbstractEvent
implements DSCComment {
    private String name;
    private String value;

    public UnparsedDSCComment(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasValues() {
        return this.value != null;
    }

    @Override
    public boolean isAtend() {
        return false;
    }

    @Override
    public void parseValue(String value) {
        this.value = value;
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.writeln("%%" + this.name + (this.hasValues() ? ": " + this.value : ""));
    }

    @Override
    public boolean isDSCComment() {
        return true;
    }

    @Override
    public int getEventType() {
        return 1;
    }

    @Override
    public DSCComment asDSCComment() {
        return this;
    }
}

