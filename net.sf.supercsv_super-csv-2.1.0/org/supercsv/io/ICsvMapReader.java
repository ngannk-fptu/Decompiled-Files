/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.util.Map;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ICsvMapReader
extends ICsvReader {
    public Map<String, String> read(String ... var1) throws IOException;

    public Map<String, Object> read(String[] var1, CellProcessor[] var2) throws IOException;
}

