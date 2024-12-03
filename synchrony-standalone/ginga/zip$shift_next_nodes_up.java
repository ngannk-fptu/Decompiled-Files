/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import ginga.zip.AsZipper;
import ginga.zip.Edit;

public final class zip$shift_next_nodes_up
extends AFunction {
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    private static Class __cached_class__2;
    public static final Var const__0;
    public static final Var const__1;
    public static final Var const__2;
    public static final Var const__3;
    public static final Var const__4;
    public static final Var const__5;
    public static final Keyword const__7;
    public static final Var const__8;
    public static final Var const__9;
    public static final Var const__10;
    public static final Keyword const__11;

    /*
     * Unable to fully structure code
     */
    public static Object invokeStatic(Object z) {
        v0 = z;
        z = null;
        v1 = v0;
        if (Util.classOf(v0) == zip$shift_next_nodes_up.__cached_class__0) ** GOTO lbl8
        if (!(v1 instanceof AsZipper)) {
            v1 = v1;
            zip$shift_next_nodes_up.__cached_class__0 = Util.classOf(v1);
lbl8:
            // 2 sources

            v2 = zip$shift_next_nodes_up.const__0.getRawRoot().invoke(v1);
        } else {
            v2 = ((AsZipper)v1).as_zipper();
        }
        map__21265 = v2;
        v3 = ((IFn)zip$shift_next_nodes_up.const__1.getRawRoot()).invoke(map__21265);
        if (v3 != null && v3 != Boolean.FALSE) {
            v4 = ((IFn)zip$shift_next_nodes_up.const__2.getRawRoot()).invoke(map__21265);
            if (v4 != null && v4 != Boolean.FALSE) {
                v5 = map__21265;
                map__21265 = null;
                v6 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)zip$shift_next_nodes_up.const__3.getRawRoot()).invoke(v5));
            } else {
                v7 = ((IFn)zip$shift_next_nodes_up.const__4.getRawRoot()).invoke(map__21265);
                if (v7 != null && v7 != Boolean.FALSE) {
                    v8 = map__21265;
                    map__21265 = null;
                    v6 = ((IFn)zip$shift_next_nodes_up.const__5.getRawRoot()).invoke(v8);
                } else {
                    v6 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            v6 = map__21265;
            map__21265 = null;
        }
        z = map__21265 = v6;
        v9 = map__21265;
        map__21265 = null;
        nexts = RT.get(v9, zip$shift_next_nodes_up.const__7);
        v10 = z;
        z = null;
        v11 = ((IFn)zip$shift_next_nodes_up.const__10.getRawRoot()).invoke(v10, zip$shift_next_nodes_up.const__7, null, zip$shift_next_nodes_up.const__11, Boolean.TRUE);
        if (Util.classOf(v11) == zip$shift_next_nodes_up.__cached_class__1) ** GOTO lbl41
        if (!(v11 instanceof Edit)) {
            v11 = v11;
            zip$shift_next_nodes_up.__cached_class__1 = Util.classOf(v11);
lbl41:
            // 2 sources

            v12 = zip$shift_next_nodes_up.const__9.getRawRoot().invoke(v11);
        } else {
            v12 = v13 = ((Edit)v11).up_next();
        }
        if (Util.classOf(v12) == zip$shift_next_nodes_up.__cached_class__2) ** GOTO lbl48
        if (!(v13 instanceof Edit)) {
            v13 = v13;
            zip$shift_next_nodes_up.__cached_class__2 = Util.classOf(v13);
lbl48:
            // 2 sources

            v14 = nexts;
            nexts = null;
            v15 = zip$shift_next_nodes_up.const__8.getRawRoot().invoke(v13, v14);
        } else {
            v16 = nexts;
            nexts = null;
            v15 = ((Edit)v13).insert_STAR_(v16);
        }
        return v15;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return zip$shift_next_nodes_up.invokeStatic(object2);
    }

    static {
        const__0 = RT.var("ginga.zip", "as-zipper");
        const__1 = RT.var("clojure.core", "seq?");
        const__2 = RT.var("clojure.core", "next");
        const__3 = RT.var("clojure.core", "to-array");
        const__4 = RT.var("clojure.core", "seq");
        const__5 = RT.var("clojure.core", "first");
        const__7 = RT.keyword(null, "nexts");
        const__8 = RT.var("ginga.zip", "insert*");
        const__9 = RT.var("ginga.zip", "up-next");
        const__10 = RT.var("clojure.core", "assoc");
        const__11 = RT.keyword(null, "m");
    }
}

