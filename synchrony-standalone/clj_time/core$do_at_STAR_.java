/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import org.joda.time.DateTimeUtils;
import org.joda.time.base.BaseDateTime;

public final class core$do_at_STAR_
extends AFunction {
    public static Object invokeStatic(Object base_date_time, Object body_fn) {
        Object object;
        Object object2 = base_date_time;
        base_date_time = null;
        DateTimeUtils.setCurrentMillisFixed(((BaseDateTime)object2).getMillis());
        try {
            Object object3 = body_fn;
            body_fn = null;
            object = ((IFn)object3).invoke();
        }
        finally {
            DateTimeUtils.setCurrentMillisSystem();
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$do_at_STAR_.invokeStatic(object3, object4);
    }
}

