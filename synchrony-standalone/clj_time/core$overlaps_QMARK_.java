/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clj_time.core.DateTimeProtocol;
import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import org.joda.time.ReadableInterval;
import org.joda.time.base.AbstractInterval;

public final class core$overlaps_QMARK_
extends AFunction {
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    private static Class __cached_class__2;
    private static Class __cached_class__3;
    private static Class __cached_class__4;
    private static Class __cached_class__5;
    public static final Var const__0;
    public static final Var const__1;
    public static final Var const__2;

    /*
     * Unable to fully structure code
     */
    public static Object invokeStatic(Object start_a, Object end_a, Object start_b, Object end_b) {
        block15: {
            block19: {
                block18: {
                    block17: {
                        block16: {
                            block14: {
                                block13: {
                                    block12: {
                                        v0 = start_b;
                                        if (Util.classOf(v0) == core$overlaps_QMARK_.__cached_class__0) ** GOTO lbl6
                                        if (!(v0 instanceof DateTimeProtocol)) {
                                            v0 = v0;
                                            core$overlaps_QMARK_.__cached_class__0 = Util.classOf(v0);
lbl6:
                                            // 2 sources

                                            v1 = core$overlaps_QMARK_.const__0.getRawRoot().invoke(v0, end_a);
                                        } else {
                                            v1 = ((DateTimeProtocol)v0).before_QMARK_(end_a);
                                        }
                                        v2 = and__5579__auto__19131 = v1;
                                        if (v2 == null || v2 == Boolean.FALSE) break block12;
                                        v3 = end_b;
                                        if (Util.classOf(v3) == core$overlaps_QMARK_.__cached_class__1) ** GOTO lbl16
                                        if (!(v3 instanceof DateTimeProtocol)) {
                                            v3 = v3;
                                            core$overlaps_QMARK_.__cached_class__1 = Util.classOf(v3);
lbl16:
                                            // 2 sources

                                            v4 = core$overlaps_QMARK_.const__1.getRawRoot().invoke(v3, start_a);
                                        } else {
                                            v4 = ((DateTimeProtocol)v3).after_QMARK_(start_a);
                                        }
                                        break block13;
                                    }
                                    v4 = and__5579__auto__19131;
                                    and__5579__auto__19131 = null;
                                }
                                v5 = or__5581__auto__19135 = v4;
                                if (v5 == null || v5 == Boolean.FALSE) break block14;
                                v6 = or__5581__auto__19135;
                                or__5581__auto__19135 = null;
                                break block15;
                            }
                            v7 = end_b;
                            if (Util.classOf(v7) == core$overlaps_QMARK_.__cached_class__2) ** GOTO lbl35
                            if (!(v7 instanceof DateTimeProtocol)) {
                                v7 = v7;
                                core$overlaps_QMARK_.__cached_class__2 = Util.classOf(v7);
lbl35:
                                // 2 sources

                                v8 = core$overlaps_QMARK_.const__1.getRawRoot().invoke(v7, start_a);
                            } else {
                                v8 = ((DateTimeProtocol)v7).after_QMARK_(start_a);
                            }
                            v9 = and__5579__auto__19132 = v8;
                            if (v9 == null || v9 == Boolean.FALSE) break block16;
                            v10 = start_b;
                            if (Util.classOf(v10) == core$overlaps_QMARK_.__cached_class__3) ** GOTO lbl45
                            if (!(v10 instanceof DateTimeProtocol)) {
                                v10 = v10;
                                core$overlaps_QMARK_.__cached_class__3 = Util.classOf(v10);
lbl45:
                                // 2 sources

                                v11 = core$overlaps_QMARK_.const__0.getRawRoot().invoke(v10, end_a);
                            } else {
                                v11 = ((DateTimeProtocol)v10).before_QMARK_(end_a);
                            }
                            break block17;
                        }
                        v11 = and__5579__auto__19132;
                        and__5579__auto__19132 = null;
                    }
                    v12 = or__5581__auto__19134 = v11;
                    if (v12 == null || v12 == Boolean.FALSE) break block18;
                    v6 = or__5581__auto__19134;
                    or__5581__auto__19134 = null;
                    break block15;
                }
                v13 = start_a;
                start_a = null;
                v14 = v13;
                if (Util.classOf(v13) == core$overlaps_QMARK_.__cached_class__4) ** GOTO lbl66
                if (!(v14 instanceof DateTimeProtocol)) {
                    v14 = v14;
                    core$overlaps_QMARK_.__cached_class__4 = Util.classOf(v14);
lbl66:
                    // 2 sources

                    v15 = end_b;
                    end_b = null;
                    v16 = core$overlaps_QMARK_.const__2.getRawRoot().invoke(v14, v15);
                } else {
                    v17 = end_b;
                    end_b = null;
                    v16 = ((DateTimeProtocol)v14).equal_QMARK_(v17);
                }
                v18 = or__5581__auto__19133 = v16;
                if (v18 == null || v18 == Boolean.FALSE) break block19;
                v6 = or__5581__auto__19133;
                or__5581__auto__19133 = null;
                break block15;
            }
            v19 = start_b;
            start_b = null;
            v20 = v19;
            if (Util.classOf(v19) == core$overlaps_QMARK_.__cached_class__5) ** GOTO lbl86
            if (!(v20 instanceof DateTimeProtocol)) {
                v20 = v20;
                core$overlaps_QMARK_.__cached_class__5 = Util.classOf(v20);
lbl86:
                // 2 sources

                v21 = end_a;
                end_a = null;
                v6 = core$overlaps_QMARK_.const__2.getRawRoot().invoke(v20, v21);
            } else {
                v22 = end_a;
                end_a = null;
                v6 = ((DateTimeProtocol)v20).equal_QMARK_(v22);
            }
        }
        return v6;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return core$overlaps_QMARK_.invokeStatic(object5, object6, object7, object8);
    }

    public static Object invokeStatic(Object i_a, Object i_b) {
        Object object = i_a;
        i_a = null;
        Object object2 = i_b;
        i_b = null;
        return ((AbstractInterval)object).overlaps((ReadableInterval)object2) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$overlaps_QMARK_.invokeStatic(object3, object4);
    }

    static {
        const__0 = RT.var("clj-time.core", "before?");
        const__1 = RT.var("clj-time.core", "after?");
        const__2 = RT.var("clj-time.core", "equal?");
    }
}

