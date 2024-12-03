/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.io.Resource;

public interface TempResourceResolver {
    public Resource getResource(String var1) throws IOException;

    public OutputStream getOutputStream(String var1) throws IOException;
}

