/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.Var;
import potemkin.utils$fast_bound_fn_STAR_$f__26228__auto____26235;
import potemkin.utils$fast_bound_fn_STAR_$fn__26237;

public final class utils$fast_bound_fn_STAR_
extends AFunction {
    public static Object invokeStatic(Object f) {
        utils$fast_bound_fn_STAR_$f__26228__auto____26235 f__26228__auto__26242;
        Object bound_frame__26227__auto__26241 = Var.cloneThreadBindingFrame();
        Object object = f;
        f = null;
        utils$fast_bound_fn_STAR_$f__26228__auto____26235 utils$fast_bound_fn_STAR_$f__26228__auto____26235 = f__26228__auto__26242 = new utils$fast_bound_fn_STAR_$f__26228__auto____26235(object);
        f__26228__auto__26242 = null;
        Object object2 = bound_frame__26227__auto__26241;
        bound_frame__26227__auto__26241 = null;
        return new utils$fast_bound_fn_STAR_$fn__26237(utils$fast_bound_fn_STAR_$f__26228__auto____26235, object2);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return utils$fast_bound_fn_STAR_.invokeStatic(object2);
    }
}

