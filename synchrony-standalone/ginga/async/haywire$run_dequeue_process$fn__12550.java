/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class haywire$run_dequeue_process$fn__12550
extends AFunction {
    Object buffer_stats;
    Object msg_size;
    Object win_out_stats;
    public static final Var const__0 = RT.var("clojure.core", "swap!");
    public static final Var const__1 = RT.var("ginga.async.win", "stats-=");
    public static final Object const__2 = 1L;
    public static final Var const__3 = RT.var("ginga.async.win", "stats+=");

    public haywire$run_dequeue_process$fn__12550(Object object, Object object2, Object object3) {
        this.buffer_stats = object;
        this.msg_size = object2;
        this.win_out_stats = object3;
    }

    @Override
    public Object invoke(Object msg) {
        Object size2 = ((IFn)this.msg_size).invoke(msg);
        ((IFn)const__0.getRawRoot()).invoke(this.buffer_stats, const__1.getRawRoot(), const__2, size2);
        Object object = size2;
        size2 = null;
        ((IFn)const__0.getRawRoot()).invoke(this.win_out_stats, const__3.getRawRoot(), const__2, object);
        Object var1_1 = null;
        return msg;
    }
}

