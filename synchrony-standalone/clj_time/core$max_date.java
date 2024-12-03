/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core$max_date$fn__19048;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class core$max_date
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "reduce");

    public static Object invokeStatic(Object dt2, ISeq dts) {
        Object object = dt2;
        dt2 = null;
        ISeq iSeq = dts;
        dts = null;
        return ((IFn)const__0.getRawRoot()).invoke(new core$max_date$fn__19048(), object, iSeq);
    }

    @Override
    public Object doInvoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        ISeq iSeq = (ISeq)object2;
        object2 = null;
        return core$max_date.invokeStatic(object3, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 1;
    }
}

