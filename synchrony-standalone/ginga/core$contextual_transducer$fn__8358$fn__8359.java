/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$contextual_transducer$fn__8358$fn__8359
extends AFunction {
    Object finalize_f;
    Object step_f;
    Object rf;
    Object state_v;
    public static final Var const__0 = RT.var("clojure.core", "deref");
    public static final Var const__1 = RT.var("clojure.core", "vreset!");

    public core$contextual_transducer$fn__8358$fn__8359(Object object, Object object2, Object object3, Object object4) {
        this.finalize_f = object;
        this.step_f = object2;
        this.rf = object3;
        this.state_v = object4;
    }

    @Override
    public Object invoke(Object result, Object input2) {
        Object object = input2;
        input2 = null;
        Object vec__8360 = ((IFn)this_.step_f).invoke(((IFn)const__0.getRawRoot()).invoke(this_.state_v), object);
        Object new_state = RT.nth(vec__8360, RT.intCast(0L), null);
        Object object2 = vec__8360;
        vec__8360 = null;
        Object input3 = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = new_state;
        new_state = null;
        ((IFn)const__1.getRawRoot()).invoke(this_.state_v, object3);
        Object object4 = result;
        result = null;
        Object object5 = input3;
        input3 = null;
        core$contextual_transducer$fn__8358$fn__8359 this_ = null;
        return ((IFn)this_.rf).invoke(object4, object5);
    }

    @Override
    public Object invoke(Object result) {
        Object state2 = ((IFn)const__0.getRawRoot()).invoke(this_.state_v);
        ((IFn)const__1.getRawRoot()).invoke(this_.state_v, null);
        Object object = state2;
        state2 = null;
        Object object2 = result;
        result = null;
        core$contextual_transducer$fn__8358$fn__8359 this_ = null;
        return ((IFn)this_.finalize_f).invoke(object, ((IFn)this_.rf).invoke(object2));
    }

    @Override
    public Object invoke() {
        core$contextual_transducer$fn__8358$fn__8359 this_ = null;
        return ((IFn)this_.rf).invoke();
    }
}

