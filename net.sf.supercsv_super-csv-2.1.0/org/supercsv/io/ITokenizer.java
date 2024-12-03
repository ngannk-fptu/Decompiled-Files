/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ITokenizer
extends Closeable {
    public int getLineNumber();

    public String getUntokenizedRow();

    public boolean readColumns(List<String> var1) throws IOException;
}

