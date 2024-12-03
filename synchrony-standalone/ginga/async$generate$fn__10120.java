/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$generate$fn__10120$G__10076__10121;
import ginga.async$generate$fn__10120$G__10077__10123;
import ginga.async$generate$fn__10120$G__10078__10125;
import ginga.async$generate$fn__10120$state_machine__5444__auto____10127;

public final class async$generate$fn__10120
extends AFunction {
    Object c__5667__auto__;
    Object r;
    Object captured_bindings__5668__auto__;
    Object f;
    Object init;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public async$generate$fn__10120(Object object, Object object2, Object object3, Object object4, Object object5) {
        this.c__5667__auto__ = object;
        this.r = object2;
        this.captured_bindings__5668__auto__ = object3;
        this.f = object4;
        this.init = object5;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__10161;
        async$generate$fn__10120$state_machine__5444__auto____10127 f__5669__auto__10160;
        this_.f = null;
        async$generate$fn__10120$G__10076__10121 G__10076 = new async$generate$fn__10120$G__10076__10121(this_.f);
        this_.init = null;
        async$generate$fn__10120$G__10077__10123 G__10077 = new async$generate$fn__10120$G__10077__10123(this_.init);
        this_.r = null;
        async$generate$fn__10120$G__10078__10125 G__10078 = new async$generate$fn__10120$G__10078__10125(this_.r);
        async$generate$fn__10120$G__10077__10123 async$generate$fn__10120$G__10077__10123 = G__10077;
        G__10077 = null;
        async$generate$fn__10120$G__10078__10125 async$generate$fn__10120$G__10078__10125 = G__10078;
        G__10078 = null;
        async$generate$fn__10120$G__10076__10121 async$generate$fn__10120$G__10076__10121 = G__10076;
        G__10076 = null;
        async$generate$fn__10120$state_machine__5444__auto____10127 async$generate$fn__10120$state_machine__5444__auto____10127 = f__5669__auto__10160 = new async$generate$fn__10120$state_machine__5444__auto____10127(async$generate$fn__10120$G__10077__10123, async$generate$fn__10120$G__10078__10125, async$generate$fn__10120$G__10076__10121);
        f__5669__auto__10160 = null;
        Object statearr_10158 = ((IFn)async$generate$fn__10120$state_machine__5444__auto____10127).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_10158, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_10158, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_10158;
        statearr_10158 = null;
        Object object2 = state__5670__auto__10161 = object;
        state__5670__auto__10161 = null;
        async$generate$fn__10120 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

