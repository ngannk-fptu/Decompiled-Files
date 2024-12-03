/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;

public final class core$extend
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "apply");
    public static final Var const__1 = RT.var("clj-time.core", "plus");
    public static final Var const__2 = RT.var("clj-time.core", "end");

    public static Object invokeStatic(Object in2, ISeq by) {
        Interval interval2 = (Interval)in2;
        Object object = in2;
        in2 = null;
        ISeq iSeq = by;
        by = null;
        return interval2.withEnd((ReadableInstant)((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), ((IFn)const__2.getRawRoot()).invoke(object), iSeq));
    }

    @Override
    public Object doInvoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        ISeq iSeq = (ISeq)object2;
        object2 = null;
        return core$extend.invokeStatic(object3, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 1;
    }
}

