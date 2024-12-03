/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;

public final class marshal$marshal_vector
extends AFunction {
    public static final Var const__0 = RT.var("ginga.marshal", "marshal-from-seq");
    public static final Object const__1 = 2L;
    public static final Var const__2 = RT.var("clojure.core", "seq");
    public static final Object const__3 = 1L;
    public static final Var const__4 = RT.var("ginga.marshal", "marshal-vector");
    public static final Var const__6 = RT.var("clojure.core", "meta");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword("ginga.marshal", "tag"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public static Object invokeStatic(Object v, Object meta_tag, Object marshaller) {
        Object object;
        Object object2 = meta_tag;
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = meta_tag;
            meta_tag = null;
            Object object4 = v;
            v = null;
            Object object5 = marshaller;
            marshaller = null;
            object = ((IFn)const__0.getRawRoot()).invoke(const__1, object3, object4, object5);
        } else {
            Object object6 = ((IFn)const__2.getRawRoot()).invoke(v);
            if (object6 != null && object6 != Boolean.FALSE) {
                Object object7 = v;
                v = null;
                Object object8 = marshaller;
                marshaller = null;
                object = ((IFn)const__0.getRawRoot()).invoke(const__3, object7, object8);
            } else {
                object = PersistentVector.EMPTY;
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return marshal$marshal_vector.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object v, Object marshaller) {
        IFn iFn = (IFn)const__4.getRawRoot();
        Object object = v;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object2 = v;
        v = null;
        Object object3 = ((IFn)const__6.getRawRoot()).invoke(object2);
        Object object4 = iLookupThunk.get(object3);
        if (iLookupThunk == object4) {
            __thunk__0__ = __site__0__.fault(object3);
            object4 = __thunk__0__.get(object3);
        }
        Object object5 = marshaller;
        marshaller = null;
        return iFn.invoke(object, object4, object5);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return marshal$marshal_vector.invokeStatic(object3, object4);
    }
}

