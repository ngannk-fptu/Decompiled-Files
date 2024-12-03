/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox;

import java.io.IOException;
import org.apache.fontbox.encoding.Encoding;

public interface EncodedFont {
    public Encoding getEncoding() throws IOException;
}

