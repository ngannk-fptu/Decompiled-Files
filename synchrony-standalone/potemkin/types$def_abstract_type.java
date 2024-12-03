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

public final class types$def_abstract_type
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern(null, "def");
    public static final Var const__4 = RT.var("clojure.core", "with-meta");
    public static final AFn const__7 = (AFn)((Object)RT.map(RT.keyword(null, "tag"), RT.keyword("potemkin", "abstract-type")));
    public static final AFn const__8 = Symbol.intern(null, "quote");
    public static final AFn const__9 = Symbol.intern("clojure.core", "deftype");
    public static final Var const__10 = RT.var("clojure.core", "apply");
    public static final Var const__11 = RT.var("clojure.core", "vector");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object name2, ISeq body) {
        Object object = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(name2, const__7));
        Object object2 = name2;
        name2 = null;
        ISeq iSeq = body;
        body = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), object, ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__8), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__9), ((IFn)const__2.getRawRoot()).invoke(object2), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(const__11.getRawRoot(), ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke()))), iSeq))))))));
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
        return types$def_abstract_type.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

