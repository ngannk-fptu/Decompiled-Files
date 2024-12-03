/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ErrorReporter;

public interface IdeErrorReporter
extends ErrorReporter {
    public void warning(String var1, String var2, int var3, int var4);

    public void error(String var1, String var2, int var3, int var4);
}

