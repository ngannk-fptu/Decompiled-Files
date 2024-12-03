/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc;

import java.io.IOException;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;

public interface DSCHandler {
    public void startDocument(String var1) throws IOException;

    public void endDocument() throws IOException;

    public void handleDSCComment(DSCComment var1) throws IOException;

    public void line(String var1) throws IOException;

    public void comment(String var1) throws IOException;
}

