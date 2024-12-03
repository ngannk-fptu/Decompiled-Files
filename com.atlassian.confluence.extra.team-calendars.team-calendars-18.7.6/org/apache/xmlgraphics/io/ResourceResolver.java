/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.io;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import org.apache.xmlgraphics.io.Resource;

public interface ResourceResolver {
    public Resource getResource(URI var1) throws IOException;

    public OutputStream getOutputStream(URI var1) throws IOException;
}

