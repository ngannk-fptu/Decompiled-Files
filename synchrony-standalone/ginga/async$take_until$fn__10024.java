/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$take_until$fn__10024$G__9975__10025;
import ginga.async$take_until$fn__10024$G__9976__10027;
import ginga.async$take_until$fn__10024$state_machine__5444__auto____10029;

public final class async$take_until$fn__10024
extends AFunction {
    Object captured_bindings__5668__auto__;
    Object ch;
    Object c__5667__auto__;
    Object until_QMARK_;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public async$take_until$fn__10024(Object object, Object object2, Object object3, Object object4) {
        this.captured_bindings__5668__auto__ = object;
        this.ch = object2;
        this.c__5667__auto__ = object3;
        this.until_QMARK_ = object4;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__10072;
        async$take_until$fn__10024$state_machine__5444__auto____10029 f__5669__auto__10071;
        this_.until_QMARK_ = null;
        async$take_until$fn__10024$G__9975__10025 G__9975 = new async$take_until$fn__10024$G__9975__10025(this_.until_QMARK_);
        this_.ch = null;
        async$take_until$fn__10024$G__9976__10027 G__9976 = new async$take_until$fn__10024$G__9976__10027(this_.ch);
        async$take_until$fn__10024$G__9975__10025 async$take_until$fn__10024$G__9975__10025 = G__9975;
        G__9975 = null;
        async$take_until$fn__10024$G__9976__10027 async$take_until$fn__10024$G__9976__10027 = G__9976;
        G__9976 = null;
        async$take_until$fn__10024$state_machine__5444__auto____10029 async$take_until$fn__10024$state_machine__5444__auto____10029 = f__5669__auto__10071 = new async$take_until$fn__10024$state_machine__5444__auto____10029(async$take_until$fn__10024$G__9975__10025, async$take_until$fn__10024$G__9976__10027);
        f__5669__auto__10071 = null;
        Object statearr_10069 = ((IFn)async$take_until$fn__10024$state_machine__5444__auto____10029).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_10069, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_10069, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_10069;
        statearr_10069 = null;
        Object object2 = state__5670__auto__10072 = object;
        state__5670__auto__10072 = null;
        async$take_until$fn__10024 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

