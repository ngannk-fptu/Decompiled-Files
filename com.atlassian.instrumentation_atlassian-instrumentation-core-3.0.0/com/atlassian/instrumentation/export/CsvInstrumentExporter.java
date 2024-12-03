/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.export;

import com.atlassian.instrumentation.AbsoluteCounter;
import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.DerivedCounter;
import com.atlassian.instrumentation.Gauge;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.caches.CacheInstrument;
import com.atlassian.instrumentation.export.InstrumentExporter;
import com.atlassian.instrumentation.operations.OpInstrument;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CsvInstrumentExporter
implements InstrumentExporter {
    @Override
    public String getMimeType() {
        return "text/csv";
    }

    @Override
    public void export(List<Instrument> instruments, Writer writer) throws IOException {
        Assertions.notNull("instruments", instruments);
        Assertions.notNull("writer", writer);
        writer.write("#name, value, type, count, time, size\n");
        for (Instrument instrument : instruments) {
            if (instrument == null) continue;
            this.writeInstrument(instrument, writer);
            writer.write("\n");
        }
    }

    @Override
    public String export(Instrument instrument) {
        Assertions.notNull("instrument", instrument);
        StringWriter out = new StringWriter();
        try {
            this.writeInstrument(instrument, out);
        }
        catch (IOException e) {
            return "";
        }
        return out.toString();
    }

    private void writeInstrument(Instrument instrument, Writer writer) throws IOException {
        StringBuilder attrs = new StringBuilder();
        this.appendAttr(attrs, instrument.getName());
        this.appendAttr(attrs, instrument.getValue());
        this.getOtherAttrs(attrs, instrument);
        writer.write(attrs.toString());
    }

    private void getOtherAttrs(StringBuilder attrs, Instrument instrument) {
        if (instrument instanceof OpInstrument) {
            this.appendAttr(attrs, "opInstrument");
            OpInstrument opInstrument = (OpInstrument)instrument;
            this.appendAttr(attrs, opInstrument.getInvocationCount());
            this.appendAttr(attrs, opInstrument.getElapsedTotalTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, opInstrument.getResultSetSize());
            this.appendAttr(attrs, opInstrument.getCpuTotalTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, opInstrument.getElapsedMinTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, opInstrument.getElapsedMaxTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, opInstrument.getCpuMinTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, opInstrument.getCpuMaxTime(TimeUnit.NANOSECONDS));
        } else if (instrument instanceof CacheInstrument) {
            this.appendAttr(attrs, "cacheInstrument");
            CacheInstrument cacheInstrument = (CacheInstrument)instrument;
            this.appendAttr(attrs, cacheInstrument.getMisses());
            this.appendAttr(attrs, cacheInstrument.getMissTime());
            this.appendAttr(attrs, cacheInstrument.getHits());
            this.appendAttr(attrs, cacheInstrument.getCacheSize());
            this.appendAttr(attrs, String.valueOf(cacheInstrument.getHitMissRatio()));
        } else if (instrument instanceof AbsoluteCounter) {
            this.appendAttr(attrs, "absoluteCounter");
        } else if (instrument instanceof DerivedCounter) {
            this.appendAttr(attrs, "derivedCounter");
        } else if (instrument instanceof Counter) {
            this.appendAttr(attrs, "counter");
        } else if (instrument instanceof Gauge) {
            this.appendAttr(attrs, "gauge");
        } else {
            this.appendAttr(attrs, instrument.getClass().getName());
        }
    }

    private void appendAttr(StringBuilder sb, String value) {
        this.needsAComma(sb);
        sb.append(this.esc(value));
    }

    private void appendAttr(StringBuilder sb, long value) {
        this.needsAComma(sb);
        sb.append(value);
    }

    private void needsAComma(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(",");
        }
    }

    private String esc(String value) {
        return value;
    }
}

