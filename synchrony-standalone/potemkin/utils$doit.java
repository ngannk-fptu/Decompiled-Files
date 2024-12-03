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

public final class utils$doit
extends RestFn {
    public static final Var const__3 = RT.var("clojure.core", "gensym");
    public static final Var const__4 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "concat");
    public static final Var const__6 = RT.var("clojure.core", "list");
    public static final AFn const__7 = Symbol.intern("clojure.core", "let");
    public static final Var const__8 = RT.var("clojure.core", "apply");
    public static final Var const__9 = RT.var("clojure.core", "vector");
    public static final AFn const__10 = Symbol.intern(null, "it__26343__auto__");
    public static final AFn const__11 = Symbol.intern(null, ".iterator");
    public static final Var const__12 = RT.var("clojure.core", "with-meta");
    public static final AFn const__14 = (AFn)((Object)RT.map(RT.keyword(null, "tag"), "Iterable"));
    public static final AFn const__15 = Symbol.intern("clojure.core", "loop");
    public static final AFn const__16 = Symbol.intern("clojure.core", "when");
    public static final AFn const__17 = Symbol.intern(null, ".hasNext");
    public static final AFn const__18 = Symbol.intern("clojure.core", "let");
    public static final AFn const__19 = Symbol.intern(null, ".next");
    public static final AFn const__20 = Symbol.intern(null, "recur");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object p__26344, ISeq body) {
        Object object = p__26344;
        p__26344 = null;
        Object vec__26345 = object;
        Object x = RT.nth(vec__26345, RT.intCast(0L), null);
        Object object2 = vec__26345;
        vec__26345 = null;
        Object it = RT.nth(object2, RT.intCast(1L), null);
        Object it_sym = ((IFn)const__3.getRawRoot()).invoke("iterable");
        Object object3 = ((IFn)const__6.getRawRoot()).invoke(it_sym);
        Object object4 = it;
        it = null;
        Object object5 = it_sym;
        it_sym = null;
        Object object6 = x;
        x = null;
        ISeq iSeq = body;
        body = null;
        return ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__7), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(object3, ((IFn)const__6.getRawRoot()).invoke(object4), ((IFn)const__6.getRawRoot()).invoke(const__10), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__11), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(object5, const__14))))))))), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__15), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke()))), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__16), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__17), ((IFn)const__6.getRawRoot()).invoke(const__10)))), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__18), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(object6), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__19), ((IFn)const__6.getRawRoot()).invoke(const__10)))))))), iSeq))), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__20))))))))))));
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
        return utils$doit.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

