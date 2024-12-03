/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ICsvListReader
extends ICsvReader {
    public List<String> read() throws IOException;

    public List<Object> read(CellProcessor ... var1) throws IOException;

    public List<Object> executeProcessors(CellProcessor ... var1);
}

