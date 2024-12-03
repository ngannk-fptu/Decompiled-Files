/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$fn_handler$body_handler__14721$fn__14722
extends AFunction {
    Object x;
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__4 = RT.var("riddley.compiler", "register-arg");
    public static final Var const__6 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__7 = RT.var("clojure.core", "chunk-first");
    public static final Var const__8 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__11 = RT.var("clojure.core", "next");
    public static final Var const__12 = RT.var("clojure.core", "doall");
    public static final Var const__13 = RT.var("clojure.core", "list*");
    public static final Var const__14 = RT.var("clojure.core", "map");
    public static final Var const__15 = RT.var("clojure.core", "rest");

    public walk$fn_handler$body_handler__14721$fn__14722(Object object, Object object2) {
        this.x = object;
        this.f = object2;
    }

    @Override
    public Object invoke() {
        Object seq_14723 = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(this_.x));
        Object chunk_14724 = null;
        long count_14725 = 0L;
        long i_14726 = 0L;
        while (true) {
            Object arg;
            Object temp__5804__auto__14729;
            if (i_14726 < count_14725) {
                Object arg2;
                Object object = arg2 = ((Indexed)chunk_14724).nth(RT.intCast(i_14726));
                arg2 = null;
                ((IFn)const__4.getRawRoot()).invoke(object);
                Object object2 = seq_14723;
                seq_14723 = null;
                Object object3 = chunk_14724;
                chunk_14724 = null;
                ++i_14726;
                chunk_14724 = object3;
                seq_14723 = object2;
                continue;
            }
            Object object = seq_14723;
            seq_14723 = null;
            Object object4 = temp__5804__auto__14729 = ((IFn)const__0.getRawRoot()).invoke(object);
            if (object4 == null || object4 == Boolean.FALSE) break;
            Object object5 = temp__5804__auto__14729;
            temp__5804__auto__14729 = null;
            Object seq_147232 = object5;
            Object object6 = ((IFn)const__6.getRawRoot()).invoke(seq_147232);
            if (object6 != null && object6 != Boolean.FALSE) {
                Object c__6065__auto__14728 = ((IFn)const__7.getRawRoot()).invoke(seq_147232);
                Object object7 = seq_147232;
                seq_147232 = null;
                Object object8 = c__6065__auto__14728;
                Object object9 = c__6065__auto__14728;
                c__6065__auto__14728 = null;
                i_14726 = RT.intCast(0L);
                count_14725 = RT.intCast(RT.count(object9));
                chunk_14724 = object8;
                seq_14723 = ((IFn)const__8.getRawRoot()).invoke(object7);
                continue;
            }
            Object object10 = arg = ((IFn)const__1.getRawRoot()).invoke(seq_147232);
            arg = null;
            ((IFn)const__4.getRawRoot()).invoke(object10);
            Object object11 = seq_147232;
            seq_147232 = null;
            i_14726 = 0L;
            count_14725 = 0L;
            chunk_14724 = null;
            seq_14723 = ((IFn)const__11.getRawRoot()).invoke(object11);
        }
        walk$fn_handler$body_handler__14721$fn__14722 this_ = null;
        return ((IFn)const__12.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(this_.x), ((IFn)const__14.getRawRoot()).invoke(this_.f, ((IFn)const__15.getRawRoot()).invoke(this_.x))));
    }
}

