/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.util.logging;

import org.radeox.util.logging.LogHandler;

public class SystemErrLogger
implements LogHandler {
    public void log(String output) {
        System.err.println(output);
    }

    public void log(String output, Throwable e) {
        System.err.println(output);
        e.printStackTrace(System.err);
    }
}

