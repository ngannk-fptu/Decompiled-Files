/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ES6Iterator;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

public final class NativeArrayIterator
extends ES6Iterator {
    private static final long serialVersionUID = 1L;
    private static final String ITERATOR_TAG = "ArrayIterator";
    private ARRAY_ITERATOR_TYPE type;
    private Scriptable arrayLike;
    private int index;

    static void init(ScriptableObject scope, boolean sealed) {
        ES6Iterator.init(scope, sealed, new NativeArrayIterator(), ITERATOR_TAG);
    }

    private NativeArrayIterator() {
    }

    public NativeArrayIterator(Scriptable scope, Scriptable arrayLike, ARRAY_ITERATOR_TYPE type) {
        super(scope, ITERATOR_TAG);
        this.index = 0;
        this.arrayLike = arrayLike;
        this.type = type;
    }

    @Override
    public String getClassName() {
        return "Array Iterator";
    }

    @Override
    protected boolean isDone(Context cx, Scriptable scope) {
        return (long)this.index >= NativeArray.getLengthProperty(cx, this.arrayLike);
    }

    @Override
    protected Object nextValue(Context cx, Scriptable scope) {
        if (this.type == ARRAY_ITERATOR_TYPE.KEYS) {
            return this.index++;
        }
        Object value = this.arrayLike.get(this.index, this.arrayLike);
        if (value == Scriptable.NOT_FOUND) {
            value = Undefined.instance;
        }
        if (this.type == ARRAY_ITERATOR_TYPE.ENTRIES) {
            value = cx.newArray(scope, new Object[]{this.index, value});
        }
        ++this.index;
        return value;
    }

    @Override
    protected String getTag() {
        return ITERATOR_TAG;
    }

    public static enum ARRAY_ITERATOR_TYPE {
        ENTRIES,
        KEYS,
        VALUES;

    }
}

