/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$try_put_BANG_$fn__9161$G__9112__9162;
import ginga.async$try_put_BANG_$fn__9161$G__9113__9164;
import ginga.async$try_put_BANG_$fn__9161$G__9114__9166;
import ginga.async$try_put_BANG_$fn__9161$G__9115__9168;
import ginga.async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170;

public final class async$try_put_BANG_$fn__9161
extends AFunction {
    Object sink;
    Object timeout;
    Object captured_bindings__5668__auto__;
    Object v;
    Object timeout_val;
    Object c__5667__auto__;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public async$try_put_BANG_$fn__9161(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        this.sink = object;
        this.timeout = object2;
        this.captured_bindings__5668__auto__ = object3;
        this.v = object4;
        this.timeout_val = object5;
        this.c__5667__auto__ = object6;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__9200;
        async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170 f__5669__auto__9199;
        async$try_put_BANG_$fn__9161$G__9115__9168 G__9115;
        this_.sink = null;
        async$try_put_BANG_$fn__9161$G__9112__9162 G__9112 = new async$try_put_BANG_$fn__9161$G__9112__9162(this_.sink);
        this_.v = null;
        async$try_put_BANG_$fn__9161$G__9113__9164 G__9113 = new async$try_put_BANG_$fn__9161$G__9113__9164(this_.v);
        this_.timeout = null;
        async$try_put_BANG_$fn__9161$G__9114__9166 G__9114 = new async$try_put_BANG_$fn__9161$G__9114__9166(this_.timeout);
        this_.timeout_val = null;
        async$try_put_BANG_$fn__9161$G__9115__9168 async$try_put_BANG_$fn__9161$G__9115__9168 = G__9115 = new async$try_put_BANG_$fn__9161$G__9115__9168(this_.timeout_val);
        G__9115 = null;
        async$try_put_BANG_$fn__9161$G__9112__9162 async$try_put_BANG_$fn__9161$G__9112__9162 = G__9112;
        G__9112 = null;
        async$try_put_BANG_$fn__9161$G__9114__9166 async$try_put_BANG_$fn__9161$G__9114__9166 = G__9114;
        G__9114 = null;
        async$try_put_BANG_$fn__9161$G__9113__9164 async$try_put_BANG_$fn__9161$G__9113__9164 = G__9113;
        G__9113 = null;
        async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170 async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170 = f__5669__auto__9199 = new async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170(async$try_put_BANG_$fn__9161$G__9115__9168, async$try_put_BANG_$fn__9161$G__9112__9162, async$try_put_BANG_$fn__9161$G__9114__9166, async$try_put_BANG_$fn__9161$G__9113__9164);
        f__5669__auto__9199 = null;
        Object statearr_9197 = ((IFn)async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9197, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9197, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_9197;
        statearr_9197 = null;
        Object object2 = state__5670__auto__9200 = object;
        state__5670__auto__9200 = null;
        async$try_put_BANG_$fn__9161 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

