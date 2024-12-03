/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import java.io.IOException;

abstract class FormatPostingsPositionsConsumer {
    FormatPostingsPositionsConsumer() {
    }

    abstract void addPosition(int var1, byte[] var2, int var3, int var4) throws IOException;

    abstract void finish() throws IOException;
}

