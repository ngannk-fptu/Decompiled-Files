/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;
import ginga.async$cond_alt_BANG_$fn__8780;
import ginga.async$cond_alt_BANG_$fn__8783;
import ginga.async$cond_alt_BANG_$fn__8786;
import ginga.async$cond_alt_BANG_$fn__8791;
import ginga.async$cond_alt_BANG_$fn__8794;

public final class async$cond_alt_BANG_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "partition-all");
    public static final Object const__1 = 3L;
    public static final Var const__2 = RT.var("ginga.core", "mapcat-pairs");
    public static final Var const__3 = RT.var("clojure.core", "vec");
    public static final Var const__4 = RT.var("clojure.core", "repeatedly");
    public static final Var const__6 = RT.var("clojure.core", "gensym");
    public static final Var const__7 = RT.var("clojure.core", "seq");
    public static final Var const__8 = RT.var("clojure.core", "concat");
    public static final Var const__9 = RT.var("clojure.core", "list");
    public static final AFn const__10 = Symbol.intern("clojure.core", "let");
    public static final Var const__11 = RT.var("clojure.core", "apply");
    public static final Var const__12 = RT.var("clojure.core", "vector");
    public static final Var const__13 = RT.var("clojure.core", "interleave");
    public static final Var const__14 = RT.var("clojure.core", "map");
    public static final AFn const__15 = Symbol.intern(null, "rch__8779__auto__");
    public static final AFn const__16 = Symbol.intern("clojure.core.async", "alts!");
    public static final AFn const__17 = Symbol.intern("clojure.core", "cond->");
    public static final Var const__18 = RT.var("clojure.core", "mapcat");
    public static final AFn const__19 = Symbol.intern("clojure.core", "condp");
    public static final AFn const__20 = Symbol.intern("clojure.core", "=");
    public static final Var const__21 = RT.var("clojure.core", "range");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq forms) {
        ISeq iSeq = forms;
        forms = null;
        Object specs2 = ((IFn)const__0.getRawRoot()).invoke(const__1, ((IFn)const__2.getRawRoot()).invoke(new async$cond_alt_BANG_$fn__8780(), iSeq));
        Object ch_syms = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(RT.count(specs2), new async$cond_alt_BANG_$fn__8783()));
        Object val_sym = ((IFn)const__6.getRawRoot()).invoke("val");
        Object object = ((IFn)const__9.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(const__12.getRawRoot(), ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__13.getRawRoot()).invoke(ch_syms, ((IFn)const__14.getRawRoot()).invoke(new async$cond_alt_BANG_$fn__8786(), specs2)), ((IFn)const__9.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(const__12.getRawRoot(), ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(val_sym), ((IFn)const__9.getRawRoot()).invoke(const__15))))), ((IFn)const__9.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__16), ((IFn)const__9.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__17), ((IFn)const__9.getRawRoot()).invoke(((IFn)const__11.getRawRoot()).invoke(const__12.getRawRoot(), ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke()))), ((IFn)const__18.getRawRoot()).invoke(new async$cond_alt_BANG_$fn__8791(), ch_syms)))))))))));
        Object object2 = ch_syms;
        ch_syms = null;
        Object object3 = val_sym;
        val_sym = null;
        Object object4 = specs2;
        specs2 = null;
        return ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__10), object, ((IFn)const__9.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(((IFn)const__9.getRawRoot()).invoke(const__19), ((IFn)const__9.getRawRoot()).invoke(const__20), ((IFn)const__9.getRawRoot()).invoke(const__15), ((IFn)const__18.getRawRoot()).invoke(new async$cond_alt_BANG_$fn__8794(object2, object3), ((IFn)const__21.getRawRoot()).invoke(), object4))))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return async$cond_alt_BANG_.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

