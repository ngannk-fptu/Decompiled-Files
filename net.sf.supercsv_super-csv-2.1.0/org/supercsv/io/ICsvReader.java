/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.Closeable;
import java.io.IOException;

public interface ICsvReader
extends Closeable {
    public String get(int var1);

    public String[] getHeader(boolean var1) throws IOException;

    public int getLineNumber();

    public String getUntokenizedRow();

    public int getRowNumber();

    public int length();
}

