/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Tuple;

public final class win$stats_PLUS__EQ_
extends AFunction {
    public static Object invokeStatic(Object p__10668, Object inc_cnt, Object inc_size) {
        Object object = p__10668;
        p__10668 = null;
        Object vec__10669 = object;
        Object cnt = RT.nth(vec__10669, RT.intCast(0L), null);
        Object object2 = vec__10669;
        vec__10669 = null;
        Object size2 = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = cnt;
        cnt = null;
        Object object4 = inc_cnt;
        inc_cnt = null;
        Object object5 = size2;
        size2 = null;
        Object object6 = inc_size;
        inc_size = null;
        return Tuple.create(Numbers.add(object3, object4), Numbers.add(object5, object6));
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return win$stats_PLUS__EQ_.invokeStatic(object4, object5, object6);
    }
}

