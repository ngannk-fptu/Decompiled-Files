/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.ps;

import java.io.IOException;
import org.apache.xmlgraphics.java2d.TextHandler;

public interface PSTextHandler
extends TextHandler {
    public void writeSetup() throws IOException;

    public void writePageSetup() throws IOException;
}

