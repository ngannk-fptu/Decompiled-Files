/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Var;

public final class multiplex$open_outc
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "next");
    public static final Var const__2 = RT.var("clojure.core", "to-array");
    public static final Var const__3 = RT.var("clojure.core", "seq");
    public static final Var const__4 = RT.var("clojure.core", "first");
    public static final Keyword const__6 = RT.keyword(null, "opts");
    public static final Keyword const__7 = RT.keyword(null, "msg-size");
    public static final Keyword const__8 = RT.keyword(null, "max-out-connections");
    public static final Keyword const__9 = RT.keyword(null, "outc-by-id");
    public static final Keyword const__10 = RT.keyword(null, "task-ch");
    public static final Keyword const__11 = RT.keyword(null, "upstream");
    public static final Var const__12 = RT.var("ginga.async.multiplex", "out-connection");
    public static final Keyword const__13 = RT.keyword(null, "id");
    public static final Keyword const__14 = RT.keyword(null, "ch");
    public static final Keyword const__15 = RT.keyword(null, "win-stats");
    public static final Keyword const__16 = RT.keyword(null, "close-promise");
    public static final Var const__19 = RT.var("clojure.core", "deref");
    public static final Var const__20 = RT.var("ginga.core", "raise");
    public static final Var const__21 = RT.var("clojure.core", "swap!");
    public static final Var const__22 = RT.var("ginga.async.multiplex", "assoc-new-conn");
    public static final Var const__23 = RT.var("ginga.async.multiplex", "schedule-put");
    public static final Var const__24 = RT.var("ginga.async.multiplex", "open-msg");
    public static final Keyword const__25 = RT.keyword(null, "init");
    public static final Var const__26 = RT.var("ginga.async.win", "stats+=");
    public static final Object const__27 = 1L;
    public static final Keyword const__28 = RT.keyword(null, "buf-cnt");
    public static final Keyword const__29 = RT.keyword(null, "buf-size");
    public static final Var const__30 = RT.var("ginga.async.win", "open?");
    public static final Var const__31 = RT.var("ginga.async.multiplex", "schedule-outc-put");

    public static Object invokeStatic(Object m4, Object ch, Object init2, Object id2, Object new_QMARK_, Object upstream_buf_opts) {
        Object map__13056;
        Object object;
        Object object2;
        Object object3;
        Object object4 = m4;
        m4 = null;
        Object map__13054 = object4;
        Object object5 = ((IFn)const__0.getRawRoot()).invoke(map__13054);
        if (object5 != null && object5 != Boolean.FALSE) {
            Object object6 = ((IFn)const__1.getRawRoot()).invoke(map__13054);
            if (object6 != null && object6 != Boolean.FALSE) {
                Object object7 = map__13054;
                map__13054 = null;
                object3 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object7));
            } else {
                Object object8 = ((IFn)const__3.getRawRoot()).invoke(map__13054);
                if (object8 != null && object8 != Boolean.FALSE) {
                    Object object9 = map__13054;
                    map__13054 = null;
                    object3 = ((IFn)const__4.getRawRoot()).invoke(object9);
                } else {
                    object3 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object3 = map__13054;
            map__13054 = null;
        }
        Object map__130542 = object3;
        Object map__13055 = RT.get(map__130542, const__6);
        Object object10 = ((IFn)const__0.getRawRoot()).invoke(map__13055);
        if (object10 != null && object10 != Boolean.FALSE) {
            Object object11 = ((IFn)const__1.getRawRoot()).invoke(map__13055);
            if (object11 != null && object11 != Boolean.FALSE) {
                Object object12 = map__13055;
                map__13055 = null;
                object2 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object12));
            } else {
                Object object13 = ((IFn)const__3.getRawRoot()).invoke(map__13055);
                if (object13 != null && object13 != Boolean.FALSE) {
                    Object object14 = map__13055;
                    map__13055 = null;
                    object2 = ((IFn)const__4.getRawRoot()).invoke(object14);
                } else {
                    object2 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object2 = map__13055;
            map__13055 = null;
        }
        Object map__130552 = object2;
        Object msg_size = RT.get(map__130552, const__7);
        Object object15 = map__130552;
        map__130552 = null;
        Object max_out_connections = RT.get(object15, const__8);
        Object outc_by_id = RT.get(map__130542, const__9);
        Object task_ch = RT.get(map__130542, const__10);
        Object object16 = map__130542;
        map__130542 = null;
        Object upstream = RT.get(object16, const__11);
        Object[] objectArray = new Object[4];
        objectArray[0] = const__13;
        objectArray[1] = id2;
        objectArray[2] = const__14;
        Object object17 = ch;
        ch = null;
        objectArray[3] = object17;
        Object map__130562 = ((IFn)const__12.getRawRoot()).invoke(RT.mapUniqueKeys(objectArray));
        Object object18 = ((IFn)const__0.getRawRoot()).invoke(map__130562);
        if (object18 != null && object18 != Boolean.FALSE) {
            Object object19 = ((IFn)const__1.getRawRoot()).invoke(map__130562);
            if (object19 != null && object19 != Boolean.FALSE) {
                Object object20 = map__130562;
                map__130562 = null;
                object = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object20));
            } else {
                Object object21 = ((IFn)const__3.getRawRoot()).invoke(map__130562);
                if (object21 != null && object21 != Boolean.FALSE) {
                    Object object22 = map__130562;
                    map__130562 = null;
                    object = ((IFn)const__4.getRawRoot()).invoke(object22);
                } else {
                    object = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object = map__130562;
            map__130562 = null;
        }
        Object outc = map__13056 = object;
        Object win_stats = RT.get(map__13056, const__15);
        Object object23 = map__13056;
        map__13056 = null;
        Object close_promise = RT.get(object23, const__16);
        Object object24 = max_out_connections;
        max_out_connections = null;
        if (Numbers.lte(object24, (long)RT.count(((IFn)const__19.getRawRoot()).invoke(outc_by_id)))) {
            ((IFn)const__20.getRawRoot()).invoke("too many out-connections");
        }
        ((IFn)const__21.getRawRoot()).invoke(outc_by_id, const__22.getRawRoot(), id2, outc);
        Object object25 = new_QMARK_;
        new_QMARK_ = null;
        if (object25 != null && object25 != Boolean.FALSE) {
            Object[] objectArray2 = new Object[4];
            objectArray2[0] = const__13;
            Object object26 = id2;
            id2 = null;
            objectArray2[1] = object26;
            objectArray2[2] = const__25;
            Object object27 = init2;
            init2 = null;
            objectArray2[3] = object27;
            ((IFn)const__23.getRawRoot()).invoke(task_ch, upstream, ((IFn)const__24.getRawRoot()).invoke(RT.mapUniqueKeys(objectArray2)));
            ((IFn)const__21.getRawRoot()).invoke(win_stats, const__26.getRawRoot(), const__27, const__27);
        } else {
            Object object28;
            Object object29 = upstream_buf_opts;
            upstream_buf_opts = null;
            Object map__13057 = object29;
            Object object30 = ((IFn)const__0.getRawRoot()).invoke(map__13057);
            if (object30 != null && object30 != Boolean.FALSE) {
                Object object31 = ((IFn)const__1.getRawRoot()).invoke(map__13057);
                if (object31 != null && object31 != Boolean.FALSE) {
                    Object object32 = map__13057;
                    map__13057 = null;
                    object28 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__2.getRawRoot()).invoke(object32));
                } else {
                    Object object33 = ((IFn)const__3.getRawRoot()).invoke(map__13057);
                    if (object33 != null && object33 != Boolean.FALSE) {
                        Object object34 = map__13057;
                        map__13057 = null;
                        object28 = ((IFn)const__4.getRawRoot()).invoke(object34);
                    } else {
                        object28 = PersistentArrayMap.EMPTY;
                    }
                }
            } else {
                object28 = map__13057;
                map__13057 = null;
            }
            Object map__130572 = object28;
            Object buf_cnt = RT.get(map__130572, const__28);
            Object object35 = map__130572;
            map__130572 = null;
            Object buf_size2 = RT.get(object35, const__29);
            Object object36 = buf_cnt;
            buf_cnt = null;
            Object object37 = buf_size2;
            buf_size2 = null;
            ((IFn)const__21.getRawRoot()).invoke(win_stats, const__26.getRawRoot(), object36, object37);
        }
        Object object38 = win_stats;
        win_stats = null;
        Object object39 = ((IFn)const__30.getRawRoot()).invoke(((IFn)const__19.getRawRoot()).invoke(object38));
        if (object39 != null && object39 != Boolean.FALSE) {
            Object object40 = msg_size;
            msg_size = null;
            Object object41 = outc_by_id;
            outc_by_id = null;
            Object object42 = task_ch;
            task_ch = null;
            Object object43 = outc;
            outc = null;
            Object object44 = upstream;
            upstream = null;
            ((IFn)const__31.getRawRoot()).invoke(object40, object41, object42, object43, object44);
        }
        Object object45 = close_promise;
        close_promise = null;
        return object45;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        Object object7 = object;
        object = null;
        Object object8 = object2;
        object2 = null;
        Object object9 = object3;
        object3 = null;
        Object object10 = object4;
        object4 = null;
        Object object11 = object5;
        object5 = null;
        Object object12 = object6;
        object6 = null;
        return multiplex$open_outc.invokeStatic(object7, object8, object9, object10, object11, object12);
    }
}

