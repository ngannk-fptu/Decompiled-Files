/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.tools.shell;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.SecurityController;

public abstract class SecurityProxy
extends SecurityController {
    protected abstract void callProcessFileSecure(Context var1, Scriptable var2, String var3);
}

