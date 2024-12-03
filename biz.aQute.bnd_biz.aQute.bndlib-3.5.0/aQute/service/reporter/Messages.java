/*
 * Decompiled with CFR 0.152.
 */
package aQute.service.reporter;

import aQute.service.reporter.Reporter;

public interface Messages {
    public ERROR NoSuchFile_(Object var1);

    public ERROR Unexpected_Error_(String var1, Exception var2);

    public static interface WARNING
    extends Reporter.SetLocation {
    }

    public static interface ERROR
    extends Reporter.SetLocation {
    }
}

