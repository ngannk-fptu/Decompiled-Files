/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$some_iteration$fn__9927$G__9882__9928;
import ginga.async$some_iteration$fn__9927$G__9883__9930;
import ginga.async$some_iteration$fn__9927$G__9884__9932;
import ginga.async$some_iteration$fn__9927$state_machine__5444__auto____9934;

public final class async$some_iteration$fn__9927
extends AFunction {
    Object f;
    Object pred_QMARK_;
    Object init;
    Object captured_bindings__5668__auto__;
    Object c__5667__auto__;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public async$some_iteration$fn__9927(Object object, Object object2, Object object3, Object object4, Object object5) {
        this.f = object;
        this.pred_QMARK_ = object2;
        this.init = object3;
        this.captured_bindings__5668__auto__ = object4;
        this.c__5667__auto__ = object5;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__9971;
        async$some_iteration$fn__9927$state_machine__5444__auto____9934 f__5669__auto__9970;
        this_.pred_QMARK_ = null;
        async$some_iteration$fn__9927$G__9882__9928 G__9882 = new async$some_iteration$fn__9927$G__9882__9928(this_.pred_QMARK_);
        this_.f = null;
        async$some_iteration$fn__9927$G__9883__9930 G__9883 = new async$some_iteration$fn__9927$G__9883__9930(this_.f);
        this_.init = null;
        async$some_iteration$fn__9927$G__9884__9932 G__9884 = new async$some_iteration$fn__9927$G__9884__9932(this_.init);
        async$some_iteration$fn__9927$G__9883__9930 async$some_iteration$fn__9927$G__9883__9930 = G__9883;
        G__9883 = null;
        async$some_iteration$fn__9927$G__9884__9932 async$some_iteration$fn__9927$G__9884__9932 = G__9884;
        G__9884 = null;
        async$some_iteration$fn__9927$G__9882__9928 async$some_iteration$fn__9927$G__9882__9928 = G__9882;
        G__9882 = null;
        async$some_iteration$fn__9927$state_machine__5444__auto____9934 async$some_iteration$fn__9927$state_machine__5444__auto____9934 = f__5669__auto__9970 = new async$some_iteration$fn__9927$state_machine__5444__auto____9934(async$some_iteration$fn__9927$G__9883__9930, async$some_iteration$fn__9927$G__9884__9932, async$some_iteration$fn__9927$G__9882__9928);
        f__5669__auto__9970 = null;
        Object statearr_9968 = ((IFn)async$some_iteration$fn__9927$state_machine__5444__auto____9934).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9968, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9968, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_9968;
        statearr_9968 = null;
        Object object2 = state__5670__auto__9971 = object;
        state__5670__auto__9971 = null;
        async$some_iteration$fn__9927 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

