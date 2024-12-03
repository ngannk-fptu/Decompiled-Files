/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.util.logging;

import org.radeox.util.logging.LogHandler;

public class SystemOutLogger
implements LogHandler {
    public void log(String output) {
        System.out.println(output);
    }

    public void log(String output, Throwable e) {
        System.out.println(output);
        e.printStackTrace(System.out);
    }
}

