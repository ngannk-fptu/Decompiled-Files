/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.exc;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Utility {
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    private Utility() {
    }
}

