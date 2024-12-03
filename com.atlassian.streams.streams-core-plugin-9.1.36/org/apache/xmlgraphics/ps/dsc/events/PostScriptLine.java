/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.AbstractEvent;

public class PostScriptLine
extends AbstractEvent {
    private String line;

    public PostScriptLine(String line) {
        this.line = line;
    }

    public String getLine() {
        return this.line;
    }

    @Override
    public void generate(PSGenerator gen) throws IOException {
        gen.writeln(this.getLine());
    }

    @Override
    public int getEventType() {
        return 3;
    }

    @Override
    public PostScriptLine asLine() {
        return this;
    }

    @Override
    public boolean isLine() {
        return true;
    }
}

