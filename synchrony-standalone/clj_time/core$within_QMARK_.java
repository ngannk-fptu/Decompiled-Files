/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core.DateTimeProtocol;
import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import org.joda.time.ReadableInstant;
import org.joda.time.base.AbstractInterval;

public final class core$within_QMARK_
extends AFunction {
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    private static Class __cached_class__2;
    private static Class __cached_class__3;
    public static final Var const__0;
    public static final Var const__1;
    public static final Var const__2;

    /*
     * Unable to fully structure code
     */
    public static Object invokeStatic(Object start, Object end, Object test) {
        block9: {
            block11: {
                block10: {
                    block8: {
                        v0 = start;
                        if (Util.classOf(v0) == core$within_QMARK_.__cached_class__0) ** GOTO lbl6
                        if (!(v0 instanceof DateTimeProtocol)) {
                            v0 = v0;
                            core$within_QMARK_.__cached_class__0 = Util.classOf(v0);
lbl6:
                            // 2 sources

                            v1 = core$within_QMARK_.const__0.getRawRoot().invoke(v0, test);
                        } else {
                            v1 = ((DateTimeProtocol)v0).equal_QMARK_(test);
                        }
                        v2 = or__5581__auto__19129 = v1;
                        if (v2 == null || v2 == Boolean.FALSE) break block8;
                        v3 = or__5581__auto__19129;
                        or__5581__auto__19129 = null;
                        break block9;
                    }
                    v4 = end;
                    if (Util.classOf(v4) == core$within_QMARK_.__cached_class__1) ** GOTO lbl20
                    if (!(v4 instanceof DateTimeProtocol)) {
                        v4 = v4;
                        core$within_QMARK_.__cached_class__1 = Util.classOf(v4);
lbl20:
                        // 2 sources

                        v5 = core$within_QMARK_.const__0.getRawRoot().invoke(v4, test);
                    } else {
                        v5 = ((DateTimeProtocol)v4).equal_QMARK_(test);
                    }
                    v6 = or__5581__auto__19128 = v5;
                    if (v6 == null || v6 == Boolean.FALSE) break block10;
                    v3 = or__5581__auto__19128;
                    or__5581__auto__19128 = null;
                    break block9;
                }
                v7 = start;
                start = null;
                v8 = v7;
                if (Util.classOf(v7) == core$within_QMARK_.__cached_class__2) ** GOTO lbl36
                if (!(v8 instanceof DateTimeProtocol)) {
                    v8 = v8;
                    core$within_QMARK_.__cached_class__2 = Util.classOf(v8);
lbl36:
                    // 2 sources

                    v9 = core$within_QMARK_.const__1.getRawRoot().invoke(v8, test);
                } else {
                    v9 = ((DateTimeProtocol)v8).before_QMARK_(test);
                }
                v10 = and__5579__auto__19127 = v9;
                if (v10 == null || v10 == Boolean.FALSE) break block11;
                v11 = end;
                end = null;
                v12 = v11;
                if (Util.classOf(v11) == core$within_QMARK_.__cached_class__3) ** GOTO lbl48
                if (!(v12 instanceof DateTimeProtocol)) {
                    v12 = v12;
                    core$within_QMARK_.__cached_class__3 = Util.classOf(v12);
lbl48:
                    // 2 sources

                    v13 = test;
                    test = null;
                    v3 = core$within_QMARK_.const__2.getRawRoot().invoke(v12, v13);
                } else {
                    v14 = test;
                    test = null;
                    v3 = ((DateTimeProtocol)v12).after_QMARK_(v14);
                }
                break block9;
            }
            v3 = and__5579__auto__19127;
            and__5579__auto__19127 = null;
        }
        return v3;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$within_QMARK_.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object i, Object dt2) {
        Object object = i;
        i = null;
        Object object2 = dt2;
        dt2 = null;
        return ((AbstractInterval)object).contains((ReadableInstant)object2) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$within_QMARK_.invokeStatic(object3, object4);
    }

    static {
        const__0 = RT.var("clj-time.core", "equal?");
        const__1 = RT.var("clj-time.core", "before?");
        const__2 = RT.var("clj-time.core", "after?");
    }
}

