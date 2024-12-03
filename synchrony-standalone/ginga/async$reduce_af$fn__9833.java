/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$reduce_af$fn__9833$G__9784__9834;
import ginga.async$reduce_af$fn__9833$G__9785__9836;
import ginga.async$reduce_af$fn__9833$G__9786__9838;
import ginga.async$reduce_af$fn__9833$state_machine__5444__auto____9840;

public final class async$reduce_af$fn__9833
extends AFunction {
    Object f;
    Object ch;
    Object captured_bindings__5668__auto__;
    Object init;
    Object c__5667__auto__;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public async$reduce_af$fn__9833(Object object, Object object2, Object object3, Object object4, Object object5) {
        this.f = object;
        this.ch = object2;
        this.captured_bindings__5668__auto__ = object3;
        this.init = object4;
        this.c__5667__auto__ = object5;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__9878;
        async$reduce_af$fn__9833$state_machine__5444__auto____9840 f__5669__auto__9877;
        async$reduce_af$fn__9833$G__9786__9838 G__9786;
        this_.f = null;
        async$reduce_af$fn__9833$G__9784__9834 G__9784 = new async$reduce_af$fn__9833$G__9784__9834(this_.f);
        this_.init = null;
        async$reduce_af$fn__9833$G__9785__9836 G__9785 = new async$reduce_af$fn__9833$G__9785__9836(this_.init);
        this_.ch = null;
        async$reduce_af$fn__9833$G__9786__9838 async$reduce_af$fn__9833$G__9786__9838 = G__9786 = new async$reduce_af$fn__9833$G__9786__9838(this_.ch);
        G__9786 = null;
        async$reduce_af$fn__9833$G__9785__9836 async$reduce_af$fn__9833$G__9785__9836 = G__9785;
        G__9785 = null;
        async$reduce_af$fn__9833$G__9784__9834 async$reduce_af$fn__9833$G__9784__9834 = G__9784;
        G__9784 = null;
        async$reduce_af$fn__9833$state_machine__5444__auto____9840 async$reduce_af$fn__9833$state_machine__5444__auto____9840 = f__5669__auto__9877 = new async$reduce_af$fn__9833$state_machine__5444__auto____9840(async$reduce_af$fn__9833$G__9786__9838, async$reduce_af$fn__9833$G__9785__9836, async$reduce_af$fn__9833$G__9784__9834);
        f__5669__auto__9877 = null;
        Object statearr_9875 = ((IFn)async$reduce_af$fn__9833$state_machine__5444__auto____9840).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9875, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9875, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_9875;
        statearr_9875 = null;
        Object object2 = state__5670__auto__9878 = object;
        state__5670__auto__9878 = null;
        async$reduce_af$fn__9833 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

