/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;

public interface SymbolScriptable {
    public Object get(Symbol var1, Scriptable var2);

    public boolean has(Symbol var1, Scriptable var2);

    public void put(Symbol var1, Scriptable var2, Object var3);

    public void delete(Symbol var1);
}

