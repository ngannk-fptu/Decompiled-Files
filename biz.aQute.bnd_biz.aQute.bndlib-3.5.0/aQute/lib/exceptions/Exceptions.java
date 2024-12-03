/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {
    private Exceptions() {
    }

    public static RuntimeException duck(Throwable t) {
        Exceptions.throwsUnchecked(t);
        throw new AssertionError((Object)"unreachable");
    }

    private static <E extends Throwable> void throwsUnchecked(Throwable throwable) throws E {
        throw throwable;
    }

    public static String toString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

