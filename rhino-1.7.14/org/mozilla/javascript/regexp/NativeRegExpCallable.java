/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.mozilla.javascript.regexp.RECompiled;

class NativeRegExpCallable
extends NativeRegExp
implements Function {
    NativeRegExpCallable(Scriptable scope, RECompiled compiled) {
        super(scope, compiled);
    }

    NativeRegExpCallable() {
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return this.execSub(cx, scope, args, 1);
    }

    @Override
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        return (Scriptable)this.execSub(cx, scope, args, 1);
    }
}

