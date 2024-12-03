/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import ginga.async.multiplex.OutConnection;

public final class multiplex$fn__12928$__GT_OutConnection__12955
extends AFunction {
    @Override
    public Object invoke(Object win_stats, Object close_promise, Object task_cnt, Object id2, Object ch) {
        Object object = win_stats;
        win_stats = null;
        Object object2 = close_promise;
        close_promise = null;
        Object object3 = task_cnt;
        task_cnt = null;
        Object object4 = id2;
        id2 = null;
        Object object5 = ch;
        ch = null;
        return new OutConnection(object, object2, object3, object4, object5);
    }
}

