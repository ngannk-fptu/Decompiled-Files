/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

public class IteratorLikeIterable
implements Iterable<Object>,
Closeable {
    private final Context cx;
    private final Scriptable scope;
    private final Callable next;
    private final Callable returnFunc;
    private final Scriptable iterator;
    private boolean closed;

    public IteratorLikeIterable(Context cx, Scriptable scope, Object target) {
        this.cx = cx;
        this.scope = scope;
        this.next = ScriptRuntime.getPropFunctionAndThis(target, "next", cx, scope);
        this.iterator = ScriptRuntime.lastStoredScriptable(cx);
        Object retObj = ScriptRuntime.getObjectPropNoWarn(target, "return", cx, scope);
        if (retObj != null && !Undefined.isUndefined(retObj)) {
            if (!(retObj instanceof Callable)) {
                throw ScriptRuntime.notFunctionError(target, retObj, "return");
            }
            this.returnFunc = (Callable)retObj;
        } else {
            this.returnFunc = null;
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            if (this.returnFunc != null) {
                this.returnFunc.call(this.cx, this.scope, this.iterator, ScriptRuntime.emptyArgs);
            }
        }
    }

    public Itr iterator() {
        return new Itr();
    }

    public final class Itr
    implements Iterator<Object> {
        private Object nextVal;
        private boolean isDone;

        @Override
        public boolean hasNext() {
            if (this.isDone) {
                return false;
            }
            Object val = IteratorLikeIterable.this.next.call(IteratorLikeIterable.this.cx, IteratorLikeIterable.this.scope, IteratorLikeIterable.this.iterator, ScriptRuntime.emptyArgs);
            Object doneval = ScriptableObject.getProperty(ScriptableObject.ensureScriptable(val), "done");
            if (doneval == Scriptable.NOT_FOUND) {
                doneval = Undefined.instance;
            }
            if (ScriptRuntime.toBoolean(doneval)) {
                this.isDone = true;
                return false;
            }
            this.nextVal = ScriptRuntime.getObjectPropNoWarn(val, "value", IteratorLikeIterable.this.cx, IteratorLikeIterable.this.scope);
            return true;
        }

        @Override
        public Object next() {
            if (this.isDone) {
                throw new NoSuchElementException();
            }
            return this.nextVal;
        }

        public boolean isDone() {
            return this.isDone;
        }

        public void setDone(boolean done) {
            this.isDone = done;
        }
    }
}

