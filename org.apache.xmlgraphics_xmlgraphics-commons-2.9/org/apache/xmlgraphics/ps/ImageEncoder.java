/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.ps;

import java.io.IOException;
import java.io.OutputStream;

public interface ImageEncoder {
    public void writeTo(OutputStream var1) throws IOException;

    public String getImplicitFilter();
}

