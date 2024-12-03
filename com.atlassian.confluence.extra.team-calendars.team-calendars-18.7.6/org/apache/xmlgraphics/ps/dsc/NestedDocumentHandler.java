/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps.dsc;

import java.io.IOException;
import org.apache.xmlgraphics.ps.dsc.DSCException;
import org.apache.xmlgraphics.ps.dsc.DSCParser;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;

public interface NestedDocumentHandler {
    public void handle(DSCEvent var1, DSCParser var2) throws IOException, DSCException;
}

