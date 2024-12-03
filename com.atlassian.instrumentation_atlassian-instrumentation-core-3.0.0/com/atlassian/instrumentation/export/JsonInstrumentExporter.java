/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
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
import org.apache.commons.text.StringEscapeUtils;

public class JsonInstrumentExporter
implements InstrumentExporter {
    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public void export(List<Instrument> instruments, Writer writer) throws IOException {
        Assertions.notNull("instruments", instruments);
        Assertions.notNull("writer", writer);
        writer.write("{\"instruments\":[");
        for (Instrument instrument : instruments) {
            if (instrument == null) continue;
            this.writeInstrument(instrument, writer);
        }
        writer.write("]}");
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
        this.appendAttr(attrs, "name", instrument.getName());
        this.appendAttr(attrs, "value", instrument.getValue());
        this.getOtherAttrs(attrs, instrument);
        writer.write("{\"instrument\":{");
        writer.write(attrs.toString());
        writer.write("}");
    }

    private void getOtherAttrs(StringBuilder attrs, Instrument instrument) {
        if (instrument instanceof OpInstrument) {
            this.appendAttr(attrs, "type", "opInstrument");
            OpInstrument opInstrument = (OpInstrument)instrument;
            this.appendAttr(attrs, "count", opInstrument.getInvocationCount());
            this.appendAttr(attrs, "elapsedTotal", opInstrument.getElapsedTotalTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, "sizeTotal", opInstrument.getResultSetSize());
            this.appendAttr(attrs, "cpuTotal", opInstrument.getCpuTotalTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, "elapsedMin", opInstrument.getElapsedMinTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, "elapsedMax", opInstrument.getElapsedMaxTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, "cpuMin", opInstrument.getCpuMinTime(TimeUnit.NANOSECONDS));
            this.appendAttr(attrs, "cpuMax", opInstrument.getCpuMaxTime(TimeUnit.NANOSECONDS));
        } else if (instrument instanceof CacheInstrument) {
            this.appendAttr(attrs, "type", "cacheInstrument");
            CacheInstrument cacheInstrument = (CacheInstrument)instrument;
            this.appendAttr(attrs, "misses", cacheInstrument.getMisses());
            this.appendAttr(attrs, "missTime", cacheInstrument.getMissTime());
            this.appendAttr(attrs, "hits", cacheInstrument.getHits());
            this.appendAttr(attrs, "size", cacheInstrument.getCacheSize());
            this.appendAttr(attrs, "hitMissRatio", String.valueOf(cacheInstrument.getHitMissRatio()));
        } else if (instrument instanceof AbsoluteCounter) {
            this.appendAttr(attrs, "type", "absoluteCounter");
        } else if (instrument instanceof DerivedCounter) {
            this.appendAttr(attrs, "type", "derivedCounter");
        } else if (instrument instanceof Counter) {
            this.appendAttr(attrs, "type", "counter");
        } else if (instrument instanceof Gauge) {
            this.appendAttr(attrs, "type", "gauge");
        } else {
            this.appendAttr(attrs, "type", instrument.getClass().getName());
        }
    }

    private void appendAttr(StringBuilder sb, String name, String value) {
        this.needsAComma(sb);
        sb.append("\"").append(name).append("\":\"").append(this.esc(value)).append("\"");
    }

    private void appendAttr(StringBuilder sb, String name, long value) {
        this.needsAComma(sb);
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
    }

    private void needsAComma(StringBuilder sb) {
        int posQuote = sb.lastIndexOf("\"");
        if (posQuote != -1 && posQuote == sb.length() - 1) {
            sb.append(",");
        }
    }

    private String esc(String value) {
        return StringEscapeUtils.escapeEcmaScript((String)value);
    }
}

