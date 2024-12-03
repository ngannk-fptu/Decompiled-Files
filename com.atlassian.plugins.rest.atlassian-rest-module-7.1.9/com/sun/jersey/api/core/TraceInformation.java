/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.core;

import com.sun.jersey.server.impl.application.WebApplicationContext;
import java.util.ArrayList;
import java.util.List;

public class TraceInformation {
    private final List<String> traces = new ArrayList<String>();
    private final WebApplicationContext c;

    public TraceInformation(WebApplicationContext c) {
        this.c = c;
    }

    public void trace(String message) {
        this.traces.add(message);
    }

    public void addTraceHeaders() {
        this.addTraceHeaders(new TraceHeaderListener(){

            @Override
            public void onHeader(String name, String value) {
                TraceInformation.this.c.getContainerResponse().getHttpHeaders().add(name, value);
            }
        });
    }

    public void addTraceHeaders(TraceHeaderListener x) {
        for (int i = 0; i < this.traces.size(); ++i) {
            x.onHeader(String.format("X-Jersey-Trace-%03d", i), this.traces.get(i));
        }
        this.traces.clear();
    }

    public static interface TraceHeaderListener {
        public void onHeader(String var1, String var2);
    }
}

