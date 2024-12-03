/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.Instrument
 *  com.atlassian.instrumentation.InstrumentRegistry
 */
package com.atlassian.instrumentation.expose.jmx;

import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentRegistry;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public interface JmxInstrumentNamer {
    public ObjectName getObjectName(Instrument var1, InstrumentRegistry var2) throws MalformedObjectNameException;
}

