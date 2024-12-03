/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;
import potemkin.types$merge_deftypes_STAR_$fn__26168;

public final class types$merge_deftypes_STAR_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "vals");
    public static final Var const__1 = RT.var("clojure.core", "merge");
    public static final Var const__2 = RT.var("potemkin.types", "deftype*->fn-map");
    public static final Var const__5 = RT.var("potemkin.types", "transform-deftype*");
    public static final Var const__6 = RT.var("clojure.core", "not");
    public static final Var const__7 = RT.var("clojure.core", "empty?");
    public static final Var const__8 = RT.var("clojure.core", "apply");
    public static final Var const__9 = RT.var("potemkin.types", "merge-deftypes*");

    public static Object invokeStatic(Object a, Object b, ISeq rest) {
        Object object;
        Object fns = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(a), ((IFn)const__2.getRawRoot()).invoke(b)));
        Object object2 = a;
        a = null;
        Object a_implements = RT.nth(object2, RT.intCast(5L));
        Object object3 = fns;
        fns = null;
        Object object4 = a_implements;
        a_implements = null;
        Object object5 = b;
        b = null;
        Object merged = ((IFn)const__5.getRawRoot()).invoke(new types$merge_deftypes_STAR_$fn__26168(object3, object4), object5);
        Object object6 = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(rest));
        if (object6 != null && object6 != Boolean.FALSE) {
            Object object7 = merged;
            merged = null;
            ISeq iSeq = rest;
            rest = null;
            object = ((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot(), object7, iSeq);
        } else {
            object = merged;
            merged = null;
        }
        return object;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return types$merge_deftypes_STAR_.invokeStatic(object4, object5, iSeq);
    }

    public static Object invokeStatic(Object a) {
        Object object = null;
        return a;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$merge_deftypes_STAR_.invokeStatic(object2);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

