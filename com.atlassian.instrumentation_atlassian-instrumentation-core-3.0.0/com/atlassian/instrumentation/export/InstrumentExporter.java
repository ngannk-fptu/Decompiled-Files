/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.export;

import com.atlassian.instrumentation.Instrument;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public interface InstrumentExporter {
    public void export(List<Instrument> var1, Writer var2) throws IOException;

    public String export(Instrument var1);

    public String getMimeType();
}

