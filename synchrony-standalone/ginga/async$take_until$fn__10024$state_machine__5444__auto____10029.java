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
import ginga.async$take_until$fn__10024$state_machine__5444__auto____10029$fn__10032;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class async$take_until$fn__10024$state_machine__5444__auto____10029
extends AFunction {
    Object G__9975;
    Object G__9976;
    public static final Var const__1 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__2 = 0L;
    public static final Object const__3 = 1L;
    public static final Keyword const__5 = RT.keyword(null, "recur");

    public async$take_until$fn__10024$state_machine__5444__auto____10029(Object object, Object object2) {
        this.G__9975 = object;
        this.G__9976 = object2;
    }

    @Override
    public Object invoke(Object state_10023) {
        Object ret_value__5446__auto__10068;
        while (true) {
            Object old_frame__5445__auto__10067;
            Object object = old_frame__5445__auto__10067 = Var.getThreadBindingFrame();
            old_frame__5445__auto__10067 = null;
            ret_value__5446__auto__10068 = ((IFn)new async$take_until$fn__10024$state_machine__5444__auto____10029$fn__10032(this.G__9975, this.G__9976, state_10023, object)).invoke();
            if (!Util.identical(ret_value__5446__auto__10068, const__5)) break;
            Object object2 = state_10023;
            state_10023 = null;
            state_10023 = object2;
        }
        Object var3_3 = null;
        return ret_value__5446__auto__10068;
    }

    @Override
    public Object invoke() {
        AtomicReferenceArray statearr_10030 = new AtomicReferenceArray(RT.intCast(14L));
        ((IFn)const__1.getRawRoot()).invoke(statearr_10030, const__2, this);
        ((IFn)const__1.getRawRoot()).invoke(statearr_10030, const__3, const__3);
        Object var1_1 = null;
        return statearr_10030;
    }
}

