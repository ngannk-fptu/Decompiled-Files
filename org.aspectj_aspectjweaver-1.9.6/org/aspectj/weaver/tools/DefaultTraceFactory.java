/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.aspectj.weaver.tools.DefaultTrace;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class DefaultTraceFactory
extends TraceFactory {
    public static final String ENABLED_PROPERTY = "org.aspectj.tracing.enabled";
    public static final String FILE_PROPERTY = "org.aspectj.tracing.file";
    private boolean tracingEnabled;
    private PrintStream print;

    public DefaultTraceFactory() {
        block3: {
            this.tracingEnabled = DefaultTraceFactory.getBoolean(ENABLED_PROPERTY, false);
            String filename = System.getProperty(FILE_PROPERTY);
            if (filename != null) {
                File file = new File(filename);
                try {
                    this.print = new PrintStream(new FileOutputStream(file));
                }
                catch (IOException ex) {
                    if (!debug) break block3;
                    ex.printStackTrace();
                }
            }
        }
    }

    public boolean isEnabled() {
        return this.tracingEnabled;
    }

    @Override
    public Trace getTrace(Class clazz) {
        DefaultTrace trace = new DefaultTrace(clazz);
        trace.setTraceEnabled(this.tracingEnabled);
        if (this.print != null) {
            trace.setPrintStream(this.print);
        }
        return trace;
    }
}

