/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc.events;

import java.io.IOException;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.DSCParserConstants;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.PostScriptLine;

public interface DSCEvent
extends DSCParserConstants {
    public int getEventType();

    public DSCComment asDSCComment();

    public PostScriptLine asLine();

    public boolean isDSCComment();

    public boolean isComment();

    public boolean isHeaderComment();

    public boolean isLine();

    public void generate(PSGenerator var1) throws IOException;
}

