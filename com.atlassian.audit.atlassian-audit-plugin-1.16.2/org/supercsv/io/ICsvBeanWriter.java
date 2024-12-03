/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvWriter;

public interface ICsvBeanWriter
extends ICsvWriter {
    public void write(Object var1, String ... var2) throws IOException;

    public void write(Object var1, String[] var2, CellProcessor[] var3) throws IOException;
}

