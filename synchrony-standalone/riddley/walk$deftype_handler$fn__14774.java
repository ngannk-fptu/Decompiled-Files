/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Indexed;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import riddley.walk$deftype_handler$fn__14774$fn__14780;

public final class walk$deftype_handler$fn__14774
extends AFunction {
    Object resolved_type;
    Object type;
    Object fns;
    Object args;
    Object interfaces;
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__3 = RT.var("riddley.compiler", "register-arg");
    public static final Var const__5 = RT.var("clojure.core", "chunked-seq?");
    public static final Var const__6 = RT.var("clojure.core", "chunk-first");
    public static final Var const__7 = RT.var("clojure.core", "chunk-rest");
    public static final Var const__10 = RT.var("clojure.core", "first");
    public static final Var const__11 = RT.var("clojure.core", "next");
    public static final Var const__12 = RT.var("clojure.core", "list*");
    public static final AFn const__13 = Symbol.intern(null, "deftype*");
    public static final Keyword const__14 = RT.keyword(null, "implements");
    public static final Var const__15 = RT.var("clojure.core", "doall");
    public static final Var const__16 = RT.var("clojure.core", "map");

    public walk$deftype_handler$fn__14774(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        this.resolved_type = object;
        this.type = object2;
        this.fns = object3;
        this.args = object4;
        this.interfaces = object5;
        this.f = object6;
    }

    @Override
    public Object invoke() {
        Object seq_14775 = ((IFn)const__0.getRawRoot()).invoke(this_.args);
        Object chunk_14776 = null;
        long count_14777 = 0L;
        long i_14778 = 0L;
        while (true) {
            Object arg;
            Object temp__5804__auto__14795;
            if (i_14778 < count_14777) {
                Object arg2;
                Object object = arg2 = ((Indexed)chunk_14776).nth(RT.intCast(i_14778));
                arg2 = null;
                ((IFn)const__3.getRawRoot()).invoke(object);
                Object object2 = seq_14775;
                seq_14775 = null;
                Object object3 = chunk_14776;
                chunk_14776 = null;
                ++i_14778;
                chunk_14776 = object3;
                seq_14775 = object2;
                continue;
            }
            Object object = seq_14775;
            seq_14775 = null;
            Object object4 = temp__5804__auto__14795 = ((IFn)const__0.getRawRoot()).invoke(object);
            if (object4 == null || object4 == Boolean.FALSE) break;
            Object object5 = temp__5804__auto__14795;
            temp__5804__auto__14795 = null;
            Object seq_147752 = object5;
            Object object6 = ((IFn)const__5.getRawRoot()).invoke(seq_147752);
            if (object6 != null && object6 != Boolean.FALSE) {
                Object c__6065__auto__14794 = ((IFn)const__6.getRawRoot()).invoke(seq_147752);
                Object object7 = seq_147752;
                seq_147752 = null;
                Object object8 = c__6065__auto__14794;
                Object object9 = c__6065__auto__14794;
                c__6065__auto__14794 = null;
                i_14778 = RT.intCast(0L);
                count_14777 = RT.intCast(RT.count(object9));
                chunk_14776 = object8;
                seq_14775 = ((IFn)const__7.getRawRoot()).invoke(object7);
                continue;
            }
            Object object10 = arg = ((IFn)const__10.getRawRoot()).invoke(seq_147752);
            arg = null;
            ((IFn)const__3.getRawRoot()).invoke(object10);
            Object object11 = seq_147752;
            seq_147752 = null;
            i_14778 = 0L;
            count_14777 = 0L;
            chunk_14776 = null;
            seq_14775 = ((IFn)const__11.getRawRoot()).invoke(object11);
        }
        walk$deftype_handler$fn__14774 this_ = null;
        return ((IFn)const__12.getRawRoot()).invoke(const__13, this_.type, this_.resolved_type, this_.args, const__14, this_.interfaces, ((IFn)const__15.getRawRoot()).invoke(((IFn)const__16.getRawRoot()).invoke(new walk$deftype_handler$fn__14774$fn__14780(this_.f), this_.fns)));
    }
}

