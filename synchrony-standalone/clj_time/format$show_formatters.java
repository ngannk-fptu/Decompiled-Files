/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class format$show_formatters
extends AFunction {
    public static final Var const__0 = RT.var("clj-time.format", "show-formatters");
    public static final Var const__1 = RT.var("clj-time.core", "now");
    public static final Var const__2 = RT.var("clojure.core", "seq");
    public static final Var const__3 = RT.var("clojure.core", "sort");
    public static final Var const__4 = RT.var("clj-time.format", "printers");
    public static final Var const__7 = RT.var("clj-time.format", "formatters");
    public static final Var const__8 = RT.var("clojure.core", "printf");
    public static final Var const__9 = RT.var("clj-time.format", "unparse");
    public static final Var const__11 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__12 = RT.var("clojure.core", "chunk-first");
    public static final Var const__13 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__16 = RT.var("clojure.core", "first");
    public static final Var const__17 = RT.var("clojure.core", "next");

    public static Object invokeStatic(Object dt2) {
        Object seq_19259 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__4.getRawRoot()));
        Object chunk_19260 = null;
        long count_19261 = 0L;
        long i_19262 = 0L;
        while (true) {
            Object temp__5804__auto__19265;
            if (i_19262 < count_19261) {
                Object p = ((Indexed)chunk_19260).nth(RT.intCast(i_19262));
                Object fmt = ((IFn)const__7.getRawRoot()).invoke(p);
                Object object = p;
                p = null;
                Object object2 = fmt;
                fmt = null;
                ((IFn)const__8.getRawRoot()).invoke("%-40s%s\n", object, ((IFn)const__9.getRawRoot()).invoke(object2, dt2));
                Object object3 = seq_19259;
                seq_19259 = null;
                Object object4 = chunk_19260;
                chunk_19260 = null;
                ++i_19262;
                chunk_19260 = object4;
                seq_19259 = object3;
                continue;
            }
            Object object = seq_19259;
            seq_19259 = null;
            Object object5 = temp__5804__auto__19265 = ((IFn)const__2.getRawRoot()).invoke(object);
            if (object5 == null || object5 == Boolean.FALSE) break;
            Object object6 = temp__5804__auto__19265;
            temp__5804__auto__19265 = null;
            Object seq_192592 = object6;
            Object object7 = ((IFn)const__11.getRawRoot()).invoke(seq_192592);
            if (object7 != null && object7 != Boolean.FALSE) {
                Object c__6065__auto__19264 = ((IFn)const__12.getRawRoot()).invoke(seq_192592);
                Object object8 = seq_192592;
                seq_192592 = null;
                Object object9 = c__6065__auto__19264;
                Object object10 = c__6065__auto__19264;
                c__6065__auto__19264 = null;
                i_19262 = RT.intCast(0L);
                count_19261 = RT.intCast(RT.count(object10));
                chunk_19260 = object9;
                seq_19259 = ((IFn)const__13.getRawRoot()).invoke(object8);
                continue;
            }
            Object p = ((IFn)const__16.getRawRoot()).invoke(seq_192592);
            Object fmt = ((IFn)const__7.getRawRoot()).invoke(p);
            Object object11 = p;
            p = null;
            Object object12 = fmt;
            fmt = null;
            ((IFn)const__8.getRawRoot()).invoke("%-40s%s\n", object11, ((IFn)const__9.getRawRoot()).invoke(object12, dt2));
            Object object13 = seq_192592;
            seq_192592 = null;
            i_19262 = 0L;
            count_19261 = 0L;
            chunk_19260 = null;
            seq_19259 = ((IFn)const__17.getRawRoot()).invoke(object13);
        }
        return null;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return format$show_formatters.invokeStatic(object2);
    }

    public static Object invokeStatic() {
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke());
    }

    @Override
    public Object invoke() {
        return format$show_formatters.invokeStatic();
    }
}

