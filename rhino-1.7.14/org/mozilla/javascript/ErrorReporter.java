/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.EvaluatorException;

public interface ErrorReporter {
    public void warning(String var1, String var2, int var3, String var4, int var5);

    public void error(String var1, String var2, int var3, String var4, int var5);

    public EvaluatorException runtimeError(String var1, String var2, int var3, String var4, int var5);
}

