/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async$timeout_loop$fn__9655$G__9632__9656;
import ginga.async$timeout_loop$fn__9655$G__9633__9658;
import ginga.async$timeout_loop$fn__9655$state_machine__5444__auto____9660;

public final class async$timeout_loop$fn__9655
extends AFunction {
    Object c__5667__auto__;
    Object ms;
    Object captured_bindings__5668__auto__;
    Object ch;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public async$timeout_loop$fn__9655(Object object, Object object2, Object object3, Object object4) {
        this.c__5667__auto__ = object;
        this.ms = object2;
        this.captured_bindings__5668__auto__ = object3;
        this.ch = object4;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__9682;
        async$timeout_loop$fn__9655$state_machine__5444__auto____9660 f__5669__auto__9681;
        async$timeout_loop$fn__9655$G__9633__9658 G__9633;
        this_.ms = null;
        async$timeout_loop$fn__9655$G__9632__9656 G__9632 = new async$timeout_loop$fn__9655$G__9632__9656(this_.ms);
        this_.ch = null;
        async$timeout_loop$fn__9655$G__9633__9658 async$timeout_loop$fn__9655$G__9633__9658 = G__9633 = new async$timeout_loop$fn__9655$G__9633__9658(this_.ch);
        G__9633 = null;
        async$timeout_loop$fn__9655$G__9632__9656 async$timeout_loop$fn__9655$G__9632__9656 = G__9632;
        G__9632 = null;
        async$timeout_loop$fn__9655$state_machine__5444__auto____9660 async$timeout_loop$fn__9655$state_machine__5444__auto____9660 = f__5669__auto__9681 = new async$timeout_loop$fn__9655$state_machine__5444__auto____9660(async$timeout_loop$fn__9655$G__9633__9658, async$timeout_loop$fn__9655$G__9632__9656);
        f__5669__auto__9681 = null;
        Object statearr_9679 = ((IFn)async$timeout_loop$fn__9655$state_machine__5444__auto____9660).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9679, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_9679, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_9679;
        statearr_9679 = null;
        Object object2 = state__5670__auto__9682 = object;
        state__5670__auto__9682 = null;
        async$timeout_loop$fn__9655 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

