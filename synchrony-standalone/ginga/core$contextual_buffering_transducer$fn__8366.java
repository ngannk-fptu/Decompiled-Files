/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.core$contextual_buffering_transducer$fn__8366$fn__8367;

public final class core$contextual_buffering_transducer$fn__8366
extends AFunction {
    Object step_f;
    Object remaining_f;
    Object init_f;
    Object finalize_f;
    public static final Var const__0 = RT.var("ginga.core", "preserving-reduced");
    public static final Var const__1 = RT.var("clojure.core", "volatile!");

    public core$contextual_buffering_transducer$fn__8366(Object object, Object object2, Object object3, Object object4) {
        this.step_f = object;
        this.remaining_f = object2;
        this.init_f = object3;
        this.finalize_f = object4;
    }

    @Override
    public Object invoke(Object rf) {
        Object reduced_v;
        Object rrf = ((IFn)const__0.getRawRoot()).invoke(rf);
        Object state_v = ((IFn)const__1.getRawRoot()).invoke(((IFn)this.init_f).invoke());
        Object object = reduced_v = ((IFn)const__1.getRawRoot()).invoke(Boolean.FALSE);
        reduced_v = null;
        Object object2 = state_v;
        state_v = null;
        Object object3 = rf;
        rf = null;
        Object object4 = rrf;
        rrf = null;
        return new core$contextual_buffering_transducer$fn__8366$fn__8367(this.step_f, object, object2, this.remaining_f, object3, object4, this.finalize_f);
    }
}

