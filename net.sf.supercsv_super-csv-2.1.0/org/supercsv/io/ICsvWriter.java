/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public interface ICsvWriter
extends Closeable,
Flushable {
    public int getLineNumber();

    public int getRowNumber();

    public void writeComment(String var1) throws IOException;

    public void writeHeader(String ... var1) throws IOException;
}

