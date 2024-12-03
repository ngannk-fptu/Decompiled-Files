/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util.io;

import java.io.IOException;

public interface CharDecoder {
    public static final int END_OF_STREAM = -1;

    public int readChar() throws IOException;

    public void dispose() throws IOException;
}

