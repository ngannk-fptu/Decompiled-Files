/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;

public final class compiler$tag_of
extends AFunction {
    public static final Var const__1 = RT.var("clojure.core", "meta");
    public static final Var const__2 = RT.var("clojure.core", "symbol");
    public static final Var const__5 = RT.var("clojure.core", "name");
    public static final AFn const__7 = Symbol.intern(null, "java.lang.Object");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "tag"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public static Object invokeStatic(Object x) {
        Object object;
        Object temp__5804__auto__14685;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object2 = x;
        x = null;
        Object object3 = ((IFn)const__1.getRawRoot()).invoke(object2);
        Object object4 = iLookupThunk.get(object3);
        if (iLookupThunk == object4) {
            __thunk__0__ = __site__0__.fault(object3);
            object4 = __thunk__0__.get(object3);
        }
        Object object5 = temp__5804__auto__14685 = object4;
        if (object5 != null && object5 != Boolean.FALSE) {
            Object object6;
            Object object7 = temp__5804__auto__14685;
            temp__5804__auto__14685 = null;
            Object tag2 = object7;
            IFn iFn = (IFn)const__2.getRawRoot();
            if (tag2 instanceof Class) {
                Object object8 = tag2;
                tag2 = null;
                object6 = ((Class)object8).getName();
            } else {
                Object object9 = tag2;
                tag2 = null;
                object6 = ((IFn)const__5.getRawRoot()).invoke(object9);
            }
            Object sym = iFn.invoke(object6);
            if (Util.equiv((Object)const__7, sym)) {
                object = null;
            } else {
                object = sym;
                Object var3_3 = null;
            }
        } else {
            object = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return compiler$tag_of.invokeStatic(object2);
    }
}

