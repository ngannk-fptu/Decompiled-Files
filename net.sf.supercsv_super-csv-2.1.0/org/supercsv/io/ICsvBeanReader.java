/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvReader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ICsvBeanReader
extends ICsvReader {
    public <T> T read(Class<T> var1, String ... var2) throws IOException;

    public <T> T read(Class<T> var1, String[] var2, CellProcessor ... var3) throws IOException;
}

