/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.Keyword;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$protocol_QMARK_
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "contains?");
    public static final Keyword const__1 = RT.keyword(null, "on-interface");
    public static final Var const__2 = RT.var("clojure.core", "class?");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "on-interface"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public static Object invokeStatic(Object x) {
        Object object;
        Object and__5579__auto__26089;
        Object object2 = and__5579__auto__26089 = ((IFn)const__0.getRawRoot()).invoke(x, const__1);
        if (object2 != null && object2 != Boolean.FALSE) {
            IFn iFn = (IFn)const__2.getRawRoot();
            ILookupThunk iLookupThunk = __thunk__0__;
            Object object3 = x;
            x = null;
            Object object4 = iLookupThunk.get(object3);
            if (iLookupThunk == object4) {
                __thunk__0__ = __site__0__.fault(object3);
                object4 = __thunk__0__.get(object3);
            }
            object = iFn.invoke(object4);
        } else {
            object = and__5579__auto__26089;
            Object var1_1 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$protocol_QMARK_.invokeStatic(object2);
    }
}

