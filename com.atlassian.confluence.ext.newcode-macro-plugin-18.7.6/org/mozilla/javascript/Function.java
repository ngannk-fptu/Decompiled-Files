/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Constructable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface Function
extends Scriptable,
Callable,
Constructable {
    @Override
    public Object call(Context var1, Scriptable var2, Scriptable var3, Object[] var4);

    @Override
    public Scriptable construct(Context var1, Scriptable var2, Object[] var3);
}

