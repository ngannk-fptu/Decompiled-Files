/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public final class NativeStringIterator
extends ES6Iterator {
    private static final long serialVersionUID = 1L;
    private static final String ITERATOR_TAG = "StringIterator";
    private String string;
    private int index;

    static void init(ScriptableObject scope, boolean sealed) {
        ES6Iterator.init(scope, sealed, new NativeStringIterator(), ITERATOR_TAG);
    }

    private NativeStringIterator() {
    }

    NativeStringIterator(Scriptable scope, Object stringLike) {
        super(scope, ITERATOR_TAG);
        this.index = 0;
        this.string = ScriptRuntime.toString(stringLike);
    }

    @Override
    public String getClassName() {
        return "String Iterator";
    }

    @Override
    protected boolean isDone(Context cx, Scriptable scope) {
        return this.index >= this.string.length();
    }

    @Override
    protected Object nextValue(Context cx, Scriptable scope) {
        int newIndex = this.string.offsetByCodePoints(this.index, 1);
        String value = this.string.substring(this.index, newIndex);
        this.index = newIndex;
        return value;
    }

    @Override
    protected String getTag() {
        return ITERATOR_TAG;
    }
}

