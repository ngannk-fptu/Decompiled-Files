/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$deftype_handler$fn__14774$fn__14780$fn__14784
extends AFunction {
    Object args;
    Object nm;
    Object body;
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__3 = RT.var("riddley.compiler", "register-arg");
    public static final Var const__5 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__6 = RT.var("clojure.core", "chunk-first");
    public static final Var const__7 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__10 = RT.var("clojure.core", "first");
    public static final Var const__11 = RT.var("clojure.core", "next");
    public static final Var const__12 = RT.var("clojure.core", "list*");
    public static final Var const__13 = RT.var("clojure.core", "doall");
    public static final Var const__14 = RT.var("clojure.core", "map");

    public walk$deftype_handler$fn__14774$fn__14780$fn__14784(Object object, Object object2, Object object3, Object object4) {
        this.args = object;
        this.nm = object2;
        this.body = object3;
        this.f = object4;
    }

    @Override
    public Object invoke() {
        Object seq_14785 = ((IFn)const__0.getRawRoot()).invoke(this_.args);
        Object chunk_14786 = null;
        long count_14787 = 0L;
        long i_14788 = 0L;
        while (true) {
            Object arg;
            Object temp__5804__auto__14791;
            if (i_14788 < count_14787) {
                Object arg2;
                Object object = arg2 = ((Indexed)chunk_14786).nth(RT.intCast(i_14788));
                arg2 = null;
                ((IFn)const__3.getRawRoot()).invoke(object);
                Object object2 = seq_14785;
                seq_14785 = null;
                Object object3 = chunk_14786;
                chunk_14786 = null;
                ++i_14788;
                chunk_14786 = object3;
                seq_14785 = object2;
                continue;
            }
            Object object = seq_14785;
            seq_14785 = null;
            Object object4 = temp__5804__auto__14791 = ((IFn)const__0.getRawRoot()).invoke(object);
            if (object4 == null || object4 == Boolean.FALSE) break;
            Object object5 = temp__5804__auto__14791;
            temp__5804__auto__14791 = null;
            Object seq_147852 = object5;
            Object object6 = ((IFn)const__5.getRawRoot()).invoke(seq_147852);
            if (object6 != null && object6 != Boolean.FALSE) {
                Object c__6065__auto__14790 = ((IFn)const__6.getRawRoot()).invoke(seq_147852);
                Object object7 = seq_147852;
                seq_147852 = null;
                Object object8 = c__6065__auto__14790;
                Object object9 = c__6065__auto__14790;
                c__6065__auto__14790 = null;
                i_14788 = RT.intCast(0L);
                count_14787 = RT.intCast(RT.count(object9));
                chunk_14786 = object8;
                seq_14785 = ((IFn)const__7.getRawRoot()).invoke(object7);
                continue;
            }
            Object object10 = arg = ((IFn)const__10.getRawRoot()).invoke(seq_147852);
            arg = null;
            ((IFn)const__3.getRawRoot()).invoke(object10);
            Object object11 = seq_147852;
            seq_147852 = null;
            i_14788 = 0L;
            count_14787 = 0L;
            chunk_14786 = null;
            seq_14785 = ((IFn)const__11.getRawRoot()).invoke(object11);
        }
        walk$deftype_handler$fn__14774$fn__14780$fn__14784 this_ = null;
        return ((IFn)const__12.getRawRoot()).invoke(this_.nm, this_.args, ((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(this_.f, this_.body)));
    }
}

