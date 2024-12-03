/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.ISeq;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class core$defnap
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "not");
    public static final Var const__4 = RT.var("clojure.core", "string?");
    public static final Var const__5 = RT.var("clojure.core", "cons");
    public static final Var const__6 = RT.var("clojure.core", "concat");
    public static final Var const__7 = RT.var("clojure.core", "list");
    public static final AFn const__8 = Symbol.intern(null, "def");
    public static final AFn const__9 = Symbol.intern("ginga.core", "auto-partial");
    public static final AFn const__10 = Symbol.intern("clojure.core", "fn");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq decl) {
        IPersistentVector iPersistentVector;
        Object vec__8344;
        Object etc;
        Object vec__8341;
        Object object;
        Object etc2;
        ISeq vec__8338;
        ISeq iSeq = decl;
        decl = null;
        ISeq iSeq2 = vec__8338 = iSeq;
        vec__8338 = null;
        Object seq__8339 = ((IFn)const__0.getRawRoot()).invoke(iSeq2);
        Object first__8340 = ((IFn)const__1.getRawRoot()).invoke(seq__8339);
        Object object2 = seq__8339;
        seq__8339 = null;
        Object seq__83392 = ((IFn)const__2.getRawRoot()).invoke(object2);
        Object object3 = first__8340;
        first__8340 = null;
        Object fn_name2 = object3;
        Object object4 = seq__83392;
        seq__83392 = null;
        Object G__8347 = etc2 = object4;
        Object object5 = etc2;
        etc2 = null;
        Object object6 = ((IFn)const__3.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(object5)));
        if (object6 != null && object6 != Boolean.FALSE) {
            Object object7 = G__8347;
            G__8347 = null;
            object = ((IFn)const__5.getRawRoot()).invoke(null, object7);
        } else {
            object = G__8347;
            G__8347 = null;
        }
        Object object8 = vec__8341 = object;
        vec__8341 = null;
        Object seq__8342 = ((IFn)const__0.getRawRoot()).invoke(object8);
        Object first__8343 = ((IFn)const__1.getRawRoot()).invoke(seq__8342);
        Object object9 = seq__8342;
        seq__8342 = null;
        Object seq__83422 = ((IFn)const__2.getRawRoot()).invoke(object9);
        Object object10 = first__8343;
        first__8343 = null;
        Object fn_doc = object10;
        Object object11 = seq__83422;
        seq__83422 = null;
        Object object12 = etc = object11;
        etc = null;
        Object object13 = vec__8344 = object12;
        vec__8344 = null;
        Object seq__8345 = ((IFn)const__0.getRawRoot()).invoke(object13);
        Object first__8346 = ((IFn)const__1.getRawRoot()).invoke(seq__8345);
        Object object14 = seq__8345;
        seq__8345 = null;
        Object seq__83452 = ((IFn)const__2.getRawRoot()).invoke(object14);
        Object object15 = first__8346;
        first__8346 = null;
        Object args = object15;
        Object object16 = seq__83452;
        seq__83452 = null;
        Object body = object16;
        IFn iFn = (IFn)const__0.getRawRoot();
        IFn iFn2 = (IFn)const__6.getRawRoot();
        Object object17 = ((IFn)const__7.getRawRoot()).invoke(const__8);
        Object object18 = fn_name2;
        fn_name2 = null;
        Object object19 = ((IFn)const__7.getRawRoot()).invoke(object18);
        Object object20 = fn_doc;
        if (object20 != null && object20 != Boolean.FALSE) {
            Object object21 = fn_doc;
            fn_doc = null;
            iPersistentVector = Tuple.create(object21);
        } else {
            iPersistentVector = PersistentVector.EMPTY;
        }
        Object object22 = args;
        args = null;
        Object object23 = body;
        body = null;
        return iFn.invoke(iFn2.invoke(object17, object19, iPersistentVector, ((IFn)const__7.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__9), ((IFn)const__7.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__10), ((IFn)const__7.getRawRoot()).invoke(object22), object23))))))));
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return core$defnap.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

