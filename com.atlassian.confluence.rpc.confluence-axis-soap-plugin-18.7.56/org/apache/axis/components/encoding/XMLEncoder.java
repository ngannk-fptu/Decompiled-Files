/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.encoding;

import java.io.IOException;
import java.io.Writer;

public interface XMLEncoder {
    public String getEncoding();

    public String encode(String var1);

    public void writeEncoded(Writer var1, String var2) throws IOException;
}

