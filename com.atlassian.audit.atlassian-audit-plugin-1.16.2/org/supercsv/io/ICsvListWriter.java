/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.util.List;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvWriter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ICsvListWriter
extends ICsvWriter {
    public void write(List<?> var1) throws IOException;

    public void write(List<?> var1, CellProcessor[] var2) throws IOException;

    public void write(Object ... var1) throws IOException;

    public void write(String ... var1) throws IOException;
}

