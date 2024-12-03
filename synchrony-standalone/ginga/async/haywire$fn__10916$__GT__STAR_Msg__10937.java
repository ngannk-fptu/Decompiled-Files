/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import ginga.async.haywire.*Msg;

public final class haywire$fn__10916$__GT__STAR_Msg__10937
extends AFunction {
    @Override
    public Object invoke(Object type2, Object seq2, Object msg) {
        Object object = type2;
        type2 = null;
        Object object2 = seq2;
        seq2 = null;
        Object object3 = msg;
        msg = null;
        return new *Msg(object, object2, object3);
    }
}

