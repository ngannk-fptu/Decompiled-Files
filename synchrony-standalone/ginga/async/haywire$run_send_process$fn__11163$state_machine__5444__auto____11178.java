/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.async.haywire$run_send_process$fn__11163$state_machine__5444__auto____11178$fn__11180;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class haywire$run_send_process$fn__11163$state_machine__5444__auto____11178
extends AFunction {
    Object G__11012;
    Object G__11011;
    Object G__11013;
    Object G__11017;
    Object G__11014;
    Object G__11015;
    Object G__11016;
    public static final Var const__1 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__2 = 0L;
    public static final Object const__3 = 1L;
    public static final Keyword const__5 = RT.keyword(null, "recur");

    public haywire$run_send_process$fn__11163$state_machine__5444__auto____11178(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
        this.G__11012 = object;
        this.G__11011 = object2;
        this.G__11013 = object3;
        this.G__11017 = object4;
        this.G__11014 = object5;
        this.G__11015 = object6;
        this.G__11016 = object7;
    }

    @Override
    public Object invoke(Object state_11162) {
        Object ret_value__5446__auto__11256;
        while (true) {
            Object old_frame__5445__auto__11255;
            Object object = old_frame__5445__auto__11255 = Var.getThreadBindingFrame();
            old_frame__5445__auto__11255 = null;
            ret_value__5446__auto__11256 = ((IFn)new haywire$run_send_process$fn__11163$state_machine__5444__auto____11178$fn__11180(this.G__11012, this.G__11011, this.G__11013, this.G__11017, this.G__11014, this.G__11015, this.G__11016, state_11162, object)).invoke();
            if (!Util.identical(ret_value__5446__auto__11256, const__5)) break;
            Object object2 = state_11162;
            state_11162 = null;
            state_11162 = object2;
        }
        Object var3_3 = null;
        return ret_value__5446__auto__11256;
    }

    @Override
    public Object invoke() {
        AtomicReferenceArray statearr_11179 = new AtomicReferenceArray(RT.intCast(16L));
        ((IFn)const__1.getRawRoot()).invoke(statearr_11179, const__2, this);
        ((IFn)const__1.getRawRoot()).invoke(statearr_11179, const__3, const__3);
        Object var1_1 = null;
        return statearr_11179;
    }
}

