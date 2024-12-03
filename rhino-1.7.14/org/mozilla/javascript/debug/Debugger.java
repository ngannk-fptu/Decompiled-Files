/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.debug;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.debug.DebugFrame;
import org.mozilla.javascript.debug.DebuggableScript;

public interface Debugger {
    public void handleCompilationDone(Context var1, DebuggableScript var2, String var3);

    public DebugFrame getFrame(Context var1, DebuggableScript var2);
}

