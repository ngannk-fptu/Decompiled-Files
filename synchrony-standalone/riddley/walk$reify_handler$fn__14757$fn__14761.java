/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.RT;
import clojure.lang.Var;

public final class walk$reify_handler$fn__14757$fn__14761
extends AFunction {
    Object nm;
    Object args;
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

    public walk$reify_handler$fn__14757$fn__14761(Object object, Object object2, Object object3, Object object4) {
        this.nm = object;
        this.args = object2;
        this.body = object3;
        this.f = object4;
    }

    @Override
    public Object invoke() {
        Object seq_14762 = ((IFn)const__0.getRawRoot()).invoke(this_.args);
        Object chunk_14763 = null;
        long count_14764 = 0L;
        long i_14765 = 0L;
        while (true) {
            Object arg;
            Object temp__5804__auto__14768;
            if (i_14765 < count_14764) {
                Object arg2;
                Object object = arg2 = ((Indexed)chunk_14763).nth(RT.intCast(i_14765));
                arg2 = null;
                ((IFn)const__3.getRawRoot()).invoke(object);
                Object object2 = seq_14762;
                seq_14762 = null;
                Object object3 = chunk_14763;
                chunk_14763 = null;
                ++i_14765;
                chunk_14763 = object3;
                seq_14762 = object2;
                continue;
            }
            Object object = seq_14762;
            seq_14762 = null;
            Object object4 = temp__5804__auto__14768 = ((IFn)const__0.getRawRoot()).invoke(object);
            if (object4 == null || object4 == Boolean.FALSE) break;
            Object object5 = temp__5804__auto__14768;
            temp__5804__auto__14768 = null;
            Object seq_147622 = object5;
            Object object6 = ((IFn)const__5.getRawRoot()).invoke(seq_147622);
            if (object6 != null && object6 != Boolean.FALSE) {
                Object c__6065__auto__14767 = ((IFn)const__6.getRawRoot()).invoke(seq_147622);
                Object object7 = seq_147622;
                seq_147622 = null;
                Object object8 = c__6065__auto__14767;
                Object object9 = c__6065__auto__14767;
                c__6065__auto__14767 = null;
                i_14765 = RT.intCast(0L);
                count_14764 = RT.intCast(RT.count(object9));
                chunk_14763 = object8;
                seq_14762 = ((IFn)const__7.getRawRoot()).invoke(object7);
                continue;
            }
            Object object10 = arg = ((IFn)const__10.getRawRoot()).invoke(seq_147622);
            arg = null;
            ((IFn)const__3.getRawRoot()).invoke(object10);
            Object object11 = seq_147622;
            seq_147622 = null;
            i_14765 = 0L;
            count_14764 = 0L;
            chunk_14763 = null;
            seq_14762 = ((IFn)const__11.getRawRoot()).invoke(object11);
        }
        walk$reify_handler$fn__14757$fn__14761 this_ = null;
        return ((IFn)const__12.getRawRoot()).invoke(this_.nm, this_.args, ((IFn)const__13.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(this_.f, this_.body)));
    }
}

