/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import org.joda.time.ReadableInterval;
import org.joda.time.base.AbstractInterval;

public final class core$overlap
extends AFunction {
    public static final Var const__1 = RT.var("clj-time.core", "now");
    public static final Var const__2 = RT.var("clj-time.core", "overlap");
    public static final Var const__3 = RT.var("clj-time.core", "interval");
    public static final Var const__4 = RT.var("clj-time.core", "latest");
    public static final Var const__5 = RT.var("clj-time.core", "start");
    public static final Var const__6 = RT.var("clj-time.core", "earliest");
    public static final Var const__7 = RT.var("clj-time.core", "end");
    public static final Keyword const__8 = RT.keyword(null, "else");

    public static Object invokeStatic(Object i_a, Object i_b) {
        Object object;
        if (Util.identical(i_b, null)) {
            Object n = ((IFn)const__1.getRawRoot()).invoke();
            Object object2 = i_a;
            i_a = null;
            Object object3 = n;
            Object object4 = n;
            n = null;
            object = ((IFn)const__2.getRawRoot()).invoke(object2, ((IFn)const__3.getRawRoot()).invoke(object3, object4));
        } else if (((AbstractInterval)i_a).overlaps((ReadableInterval)i_b)) {
            Object object5 = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(i_a), ((IFn)const__5.getRawRoot()).invoke(i_b));
            Object object6 = i_a;
            i_a = null;
            Object object7 = i_b;
            i_b = null;
            object = ((IFn)const__3.getRawRoot()).invoke(object5, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(object6), ((IFn)const__7.getRawRoot()).invoke(object7)));
        } else {
            Keyword keyword2 = const__8;
            object = keyword2 != null && keyword2 != Boolean.FALSE ? null : null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$overlap.invokeStatic(object3, object4);
    }
}

