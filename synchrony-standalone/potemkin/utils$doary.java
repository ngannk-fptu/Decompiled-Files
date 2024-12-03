/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class utils$doary
extends RestFn {
    public static final Var const__3 = RT.var("clojure.core", "gensym");
    public static final Var const__4 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "concat");
    public static final Var const__6 = RT.var("clojure.core", "list");
    public static final AFn const__7 = Symbol.intern("clojure.core", "let");
    public static final Var const__8 = RT.var("clojure.core", "apply");
    public static final Var const__9 = RT.var("clojure.core", "vector");
    public static final Var const__10 = RT.var("clojure.core", "with-meta");
    public static final AFn const__12 = (AFn)((Object)RT.map(RT.keyword(null, "tag"), "objects"));
    public static final AFn const__13 = Symbol.intern("clojure.core", "dotimes");
    public static final AFn const__14 = Symbol.intern(null, "idx__26349__auto__");
    public static final AFn const__15 = Symbol.intern("clojure.core", "alength");
    public static final AFn const__16 = Symbol.intern("clojure.core", "let");
    public static final AFn const__17 = Symbol.intern("clojure.core", "aget");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object p__26350, ISeq body) {
        Object object = p__26350;
        p__26350 = null;
        Object vec__26351 = object;
        Object x = RT.nth(vec__26351, RT.intCast(0L), null);
        Object object2 = vec__26351;
        vec__26351 = null;
        Object ary = RT.nth(object2, RT.intCast(1L), null);
        Object ary_sym = ((IFn)const__3.getRawRoot()).invoke("ary");
        Object object3 = ary;
        ary = null;
        Object object4 = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(ary_sym, const__12)), ((IFn)const__6.getRawRoot()).invoke(object3)))));
        Object object5 = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__14), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__15), ((IFn)const__6.getRawRoot()).invoke(ary_sym))))))));
        Object object6 = x;
        x = null;
        Object object7 = ary_sym;
        ary_sym = null;
        ISeq iSeq = body;
        body = null;
        return ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__7), object4, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__13), object5, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__16), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(object6), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__17), ((IFn)const__6.getRawRoot()).invoke(object7), ((IFn)const__6.getRawRoot()).invoke(const__14)))))))), iSeq))))))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        ISeq iSeq = (ISeq)object4;
        object4 = null;
        return utils$doary.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

