/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.base.AbstractInstant;

public final class core$floor
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.core", "year");
    public static final Var const__1 = RT.var("clj-time.core", "month");
    public static final Var const__2 = RT.var("clj-time.core", "day");
    public static final Var const__3 = RT.var("clj-time.core", "hour");
    public static final Var const__4 = RT.var("clj-time.core", "minute");
    public static final Var const__5 = RT.var("clj-time.core", "second");
    public static final Var const__6 = RT.var("clj-time.core", "milli");
    public static final Var const__7 = RT.var("clojure.core", "apply");
    public static final Var const__8 = RT.var("clj-time.core", "date-time");
    public static final Var const__9 = RT.var("clojure.core", "map");
    public static final Var const__10 = RT.var("clojure.core", "concat");
    public static final Var const__11 = RT.var("clojure.core", "take-while");
    public static final Var const__12 = RT.var("clojure.core", "partial");
    public static final Var const__13 = RT.var("clojure.core", "not=");
    public static final Var const__14 = RT.var("clojure.core", "repeat");

    public static Object invokeStatic(Object dt2, Object dt_fn) {
        IPersistentVector dt_fns = RT.vector(const__0.getRawRoot(), const__1.getRawRoot(), const__2.getRawRoot(), const__3.getRawRoot(), const__4.getRawRoot(), const__5.getRawRoot(), const__6.getRawRoot());
        DateTimeZone tz = ((AbstractInstant)dt2).getZone();
        IPersistentVector iPersistentVector = dt_fns;
        dt_fns = null;
        Object object = ((IFn)const__11.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(const__13.getRawRoot(), dt_fn), iPersistentVector);
        Object object2 = dt_fn;
        dt_fn = null;
        Object object3 = dt2;
        dt2 = null;
        DateTimeZone dateTimeZone = tz;
        tz = null;
        return ((DateTime)((IFn)const__7.getRawRoot()).invoke(const__8.getRawRoot(), ((IFn)const__9.getRawRoot()).invoke(const__7.getRawRoot(), ((IFn)const__10.getRawRoot()).invoke(object, Tuple.create(object2)), ((IFn)const__14.getRawRoot()).invoke(Tuple.create(object3))))).withZoneRetainFields(dateTimeZone);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$floor.invokeStatic(object3, object4);
    }
}

