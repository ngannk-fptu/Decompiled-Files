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
import ginga.async$retry_BANG_$fn__9562$state_machine__5444__auto____9581$fn__9583;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class async$retry_BANG_$fn__9562$state_machine__5444__auto____9581
extends AFunction {
    Object G__9471;
    Object G__9467;
    Object G__9469;
    Object G__9472;
    Object G__9464;
    Object G__9470;
    Object G__9468;
    Object G__9465;
    Object G__9466;
    public static final Var const__1 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__2 = 0L;
    public static final Object const__3 = 1L;
    public static final Keyword const__5 = RT.keyword(null, "recur");

    public async$retry_BANG_$fn__9562$state_machine__5444__auto____9581(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9) {
        this.G__9471 = object;
        this.G__9467 = object2;
        this.G__9469 = object3;
        this.G__9472 = object4;
        this.G__9464 = object5;
        this.G__9470 = object6;
        this.G__9468 = object7;
        this.G__9465 = object8;
        this.G__9466 = object9;
    }

    @Override
    public Object invoke(Object state_9561) {
        Object ret_value__5446__auto__9624;
        while (true) {
            Object old_frame__5445__auto__9623;
            Object object = old_frame__5445__auto__9623 = Var.getThreadBindingFrame();
            old_frame__5445__auto__9623 = null;
            ret_value__5446__auto__9624 = ((IFn)new async$retry_BANG_$fn__9562$state_machine__5444__auto____9581$fn__9583(this.G__9471, object, this.G__9467, this.G__9469, this.G__9472, this.G__9464, this.G__9470, this.G__9468, this.G__9465, state_9561, this.G__9466)).invoke();
            if (!Util.identical(ret_value__5446__auto__9624, const__5)) break;
            Object object2 = state_9561;
            state_9561 = null;
            state_9561 = object2;
        }
        Object var3_3 = null;
        return ret_value__5446__auto__9624;
    }

    @Override
    public Object invoke() {
        AtomicReferenceArray statearr_9582 = new AtomicReferenceArray(RT.intCast(29L));
        ((IFn)const__1.getRawRoot()).invoke(statearr_9582, const__2, this);
        ((IFn)const__1.getRawRoot()).invoke(statearr_9582, const__3, const__3);
        Object var1_1 = null;
        return statearr_9582;
    }
}

