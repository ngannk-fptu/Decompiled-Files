/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.Counter
 *  com.atlassian.instrumentation.Gauge
 *  com.atlassian.instrumentation.Instrument
 *  com.atlassian.instrumentation.InstrumentRegistry
 *  com.atlassian.instrumentation.caches.CacheInstrument
 *  com.atlassian.instrumentation.operations.OpInstrument
 */
package com.atlassian.instrumentation.expose.jmx;

import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.Gauge;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentRegistry;
import com.atlassian.instrumentation.caches.CacheInstrument;
import com.atlassian.instrumentation.expose.jmx.JmxInstrumentNamer;
import com.atlassian.instrumentation.operations.OpInstrument;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class JmxInstrumentNamerImpl
implements JmxInstrumentNamer {
    @Override
    public ObjectName getObjectName(Instrument instrument, InstrumentRegistry instrumentRegistry) throws MalformedObjectNameException {
        return new ObjectName(String.format("%s%s:type=%s,name=%s", "com.atlassian.instrumentation", this.determinePrefix(instrumentRegistry), this.determineType(instrument), instrument.getName()));
    }

    private String determineType(Instrument instrument) {
        if (instrument instanceof Counter) {
            return "Counter";
        }
        if (instrument instanceof Gauge) {
            return "Gauge";
        }
        if (instrument instanceof OpInstrument) {
            return "Operation";
        }
        if (instrument instanceof CacheInstrument) {
            return "Cache";
        }
        return "Instrument";
    }

    private String determinePrefix(InstrumentRegistry instrumentRegistry) {
        StringBuilder prefix = new StringBuilder(instrumentRegistry.getRegistryConfiguration().getRegistryName());
        if (prefix.length() > 0) {
            prefix.insert(0, ".");
        }
        return this.safeEncode(prefix.toString());
    }

    private String safeEncode(String s) {
        s = s.replaceAll(":", "x");
        s = s.replaceAll(" ", "_");
        s = s.replaceAll(" ", "_");
        s = s.replaceAll("\\*", "x");
        s = s.replaceAll("\\?", "x");
        s = s.replaceAll("/", "_");
        return s;
    }
}

