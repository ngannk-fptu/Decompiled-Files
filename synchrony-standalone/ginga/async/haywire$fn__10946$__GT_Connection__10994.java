/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import ginga.async.haywire.Connection;

public final class haywire$fn__10946$__GT_Connection__10994
extends AFunction {
    @Override
    public Object invoke(Object alive, Object close_promise, Object seq_state, Object reorder_xf, Object buffer_stats, Object win_in_stats, Object win_out_stats, Object downstream2, Object upstream, Object receive_buf, Object in2, Object out) {
        Object object = alive;
        alive = null;
        Object object2 = close_promise;
        close_promise = null;
        Object object3 = seq_state;
        seq_state = null;
        Object object4 = reorder_xf;
        reorder_xf = null;
        Object object5 = buffer_stats;
        buffer_stats = null;
        Object object6 = win_in_stats;
        win_in_stats = null;
        Object object7 = win_out_stats;
        win_out_stats = null;
        Object object8 = downstream2;
        downstream2 = null;
        Object object9 = upstream;
        upstream = null;
        Object object10 = receive_buf;
        receive_buf = null;
        Object object11 = in2;
        in2 = null;
        Object object12 = out;
        out = null;
        return new Connection(object, object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12);
    }
}

