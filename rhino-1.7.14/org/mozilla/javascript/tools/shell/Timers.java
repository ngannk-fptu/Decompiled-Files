/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.tools.shell;

import java.util.HashMap;
import java.util.PriorityQueue;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

public class Timers {
    private int lastId = 0;
    private final HashMap<Integer, Timeout> timers = new HashMap();
    private final PriorityQueue<Timeout> timerQueue = new PriorityQueue();

    public void install(Scriptable scope) {
        LambdaFunction setTimeout = new LambdaFunction(scope, "setTimeout", 1, (lcx, lscope, thisObj, args) -> this.setTimeout(args));
        ScriptableObject.defineProperty(scope, "setTimeout", setTimeout, 2);
        LambdaFunction clearTimeout = new LambdaFunction(scope, "clearTimeout", 1, (lcx, lscope, thisObj, args) -> this.clearTimeout(args));
        ScriptableObject.defineProperty(scope, "clearTimeout", clearTimeout, 2);
    }

    public void runAllTimers(Context cx, Scriptable scope) throws InterruptedException {
        boolean executed;
        do {
            cx.processMicrotasks();
        } while (executed = this.executeNext(cx, scope));
        cx.processMicrotasks();
    }

    private boolean executeNext(Context cx, Scriptable scope) throws InterruptedException {
        Timeout t = this.timerQueue.peek();
        if (t == null) {
            return false;
        }
        long remaining = t.expiration - System.currentTimeMillis();
        if (remaining > 0L) {
            Thread.sleep(remaining);
        }
        this.timerQueue.remove();
        this.timers.remove(t.id);
        cx.enqueueMicrotask(() -> t.func.call(cx, scope, scope, t.funcArgs));
        return true;
    }

    private Object setTimeout(Object[] args) {
        if (args.length == 0) {
            throw ScriptRuntime.typeError("Expected function parameter");
        }
        if (!(args[0] instanceof Function)) {
            throw ScriptRuntime.typeError("Expected first argument to be a function");
        }
        int id = ++this.lastId;
        Timeout t = new Timeout();
        t.id = id;
        t.func = (Function)args[0];
        int delay = 0;
        if (args.length > 1) {
            delay = ScriptRuntime.toInt32(args[1]);
        }
        t.expiration = System.currentTimeMillis() + (long)delay;
        if (args.length > 2) {
            t.funcArgs = new Object[args.length - 2];
            System.arraycopy(args, 2, t.funcArgs, 0, t.funcArgs.length);
        }
        this.timers.put(id, t);
        this.timerQueue.add(t);
        return id;
    }

    private Object clearTimeout(Object[] args) {
        if (args.length == 0) {
            throw ScriptRuntime.typeError("Expected function parameter");
        }
        int id = ScriptRuntime.toInt32(args[0]);
        Timeout t = this.timers.remove(id);
        if (t != null) {
            this.timerQueue.remove(t);
        }
        return Undefined.instance;
    }

    private static final class Timeout
    implements Comparable<Timeout> {
        int id;
        Function func;
        Object[] funcArgs = ScriptRuntime.emptyArgs;
        long expiration;

        private Timeout() {
        }

        @Override
        public int compareTo(Timeout o) {
            return Long.compare(this.expiration, o.expiration);
        }

        public boolean equals(Object obj) {
            try {
                return this.expiration == ((Timeout)obj).expiration;
            }
            catch (ClassCastException cce) {
                return false;
            }
        }

        public int hashCode() {
            assert (false);
            return (int)this.expiration;
        }
    }
}

