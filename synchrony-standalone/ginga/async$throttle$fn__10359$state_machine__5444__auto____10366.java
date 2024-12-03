/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.async$throttle$fn__10359$state_machine__5444__auto____10366$fn__10368;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class async$throttle$fn__10359$state_machine__5444__auto____10366
extends AFunction {
    Object G__10322;
    Object G__10323;
    Object G__10324;
    public static final Var const__1 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__2 = 0L;
    public static final Object const__3 = 1L;
    public static final Keyword const__5 = RT.keyword(null, "recur");

    public async$throttle$fn__10359$state_machine__5444__auto____10366(Object object, Object object2, Object object3) {
        this.G__10322 = object;
        this.G__10323 = object2;
        this.G__10324 = object3;
    }

    @Override
    public Object invoke(Object state_10358) {
        Object ret_value__5446__auto__10391;
        while (true) {
            Object old_frame__5445__auto__10390;
            Object object = old_frame__5445__auto__10390 = Var.getThreadBindingFrame();
            old_frame__5445__auto__10390 = null;
            ret_value__5446__auto__10391 = ((IFn)new async$throttle$fn__10359$state_machine__5444__auto____10366$fn__10368(object, this.G__10322, this.G__10323, state_10358, this.G__10324)).invoke();
            if (!Util.identical(ret_value__5446__auto__10391, const__5)) break;
            Object object2 = state_10358;
            state_10358 = null;
            state_10358 = object2;
        }
        Object var3_3 = null;
        return ret_value__5446__auto__10391;
    }

    @Override
    public Object invoke() {
        AtomicReferenceArray statearr_10367 = new AtomicReferenceArray(RT.intCast(13L));
        ((IFn)const__1.getRawRoot()).invoke(statearr_10367, const__2, this);
        ((IFn)const__1.getRawRoot()).invoke(statearr_10367, const__3, const__3);
        Object var1_1 = null;
        return statearr_10367;
    }
}

