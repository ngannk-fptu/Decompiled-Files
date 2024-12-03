/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;

public interface DSCComment
extends DSCEvent {
    public String getName();

    public void parseValue(String var1);

    public boolean hasValues();

    public boolean isAtend();

    @Override
    public void generate(PSGenerator var1) throws IOException;
}

