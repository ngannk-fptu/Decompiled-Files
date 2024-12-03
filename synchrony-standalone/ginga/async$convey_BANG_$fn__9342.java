/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$convey_BANG_$fn__9342$G__9291__9343;
import ginga.async$convey_BANG_$fn__9342$G__9292__9345;
import ginga.async$convey_BANG_$fn__9342$G__9293__9347;
import ginga.async$convey_BANG_$fn__9342$state_machine__5444__auto____9349;

public final class async$convey_BANG_$fn__9342
extends AFunction {
    Object message;
    Object c__5667__auto__;
    Object out;
    Object captured_bindings__5668__auto__;
    Object close_promise;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public async$convey_BANG_$fn__9342(Object object, Object object2, Object object3, Object object4, Object object5) {
        this.message = object;
        this.c__5667__auto__ = object2;
        this.out = object3;
        this.captured_bindings__5668__auto__ = object4;
        this.close_promise = object5;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__9381;
        async$convey_BANG_$fn__9342$state_machine__5444__auto____9349 f__5669__auto__9380;
        this_.out = null;
        async$convey_BANG_$fn__9342$G__9291__9343 G__9291 = new async$convey_BANG_$fn__9342$G__9291__9343(this_.out);
        this_.message = null;
        async$convey_BANG_$fn__9342$G__9292__9345 G__9292 = new async$convey_BANG_$fn__9342$G__9292__9345(this_.message);
        this_.close_promise = null;
        async$convey_BANG_$fn__9342$G__9293__9347 G__9293 = new async$convey_BANG_$fn__9342$G__9293__9347(this_.close_promise);
        async$convey_BANG_$fn__9342$G__9291__9343 async$convey_BANG_$fn__9342$G__9291__9343 = G__9291;
        G__9291 = null;
        async$convey_BANG_$fn__9342$G__9293__9347 async$convey_BANG_$fn__9342$G__9293__9347 = G__9293;
        G__9293 = null;
        async$convey_BANG_$fn__9342$G__9292__9345 async$convey_BANG_$fn__9342$G__9292__9345 = G__9292;
        G__9292 = null;
        async$convey_BANG_$fn__9342$state_machine__5444__auto____9349 async$convey_BANG_$fn__9342$state_machine__5444__auto____9349 = f__5669__auto__9380 = new async$convey_BANG_$fn__9342$state_machine__5444__auto____9349(async$convey_BANG_$fn__9342$G__9291__9343, async$convey_BANG_$fn__9342$G__9293__9347, async$convey_BANG_$fn__9342$G__9292__9345);
        f__5669__auto__9380 = null;
        Object statearr_9378 = ((IFn)async$convey_BANG_$fn__9342$state_machine__5444__auto____9349).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9378, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9378, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_9378;
        statearr_9378 = null;
        Object object2 = state__5670__auto__9381 = object;
        state__5670__auto__9381 = null;
        async$convey_BANG_$fn__9342 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

