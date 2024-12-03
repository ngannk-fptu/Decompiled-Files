/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import ginga.async.multiplex.Multiplexer;

public final class multiplex$fn__12977$__GT_Multiplexer__13007
extends AFunction {
    @Override
    public Object invoke(Object inc_by_id, Object outc_by_id, Object upstream, Object downstream2, Object task_ch, Object opts) {
        Object object = inc_by_id;
        inc_by_id = null;
        Object object2 = outc_by_id;
        outc_by_id = null;
        Object object3 = upstream;
        upstream = null;
        Object object4 = downstream2;
        downstream2 = null;
        Object object5 = task_ch;
        task_ch = null;
        Object object6 = opts;
        opts = null;
        return new Multiplexer(object, object2, object3, object4, object5, object6);
    }
}

