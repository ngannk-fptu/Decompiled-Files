/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.core$contextual_transducer$fn__8358$fn__8359;

public final class core$contextual_transducer$fn__8358
extends AFunction {
    Object finalize_f;
    Object step_f;
    Object init_f;
    public static final Var const__0 = RT.var("clojure.core", "volatile!");

    public core$contextual_transducer$fn__8358(Object object, Object object2, Object object3) {
        this.finalize_f = object;
        this.step_f = object2;
        this.init_f = object3;
    }

    @Override
    public Object invoke(Object rf) {
        Object state_v = ((IFn)const__0.getRawRoot()).invoke(((IFn)this.init_f).invoke());
        Object object = rf;
        rf = null;
        Object object2 = state_v;
        state_v = null;
        return new core$contextual_transducer$fn__8358$fn__8359(this.finalize_f, this.step_f, object, object2);
    }
}

