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
import clojure.lang.Util;
import clojure.lang.Var;

public final class types$abstract_type_QMARK_
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "symbol?");
    public static final Keyword const__2 = RT.keyword("potemkin", "abstract-type");
    public static final Var const__4 = RT.var("clojure.core", "meta");
    public static final Var const__5 = RT.var("potemkin.macros", "safe-resolve");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "tag"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public static Object invokeStatic(Object x) {
        Object object;
        Object and__5579__auto__26143;
        Object object2 = and__5579__auto__26143 = ((IFn)const__0.getRawRoot()).invoke(x);
        if (object2 != null && object2 != Boolean.FALSE) {
            ILookupThunk iLookupThunk = __thunk__0__;
            Object object3 = x;
            x = null;
            Object object4 = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(object3));
            Object object5 = iLookupThunk.get(object4);
            if (iLookupThunk == object5) {
                __thunk__0__ = __site__0__.fault(object4);
                object5 = __thunk__0__.get(object4);
            }
            object = Util.equiv((Object)const__2, object5) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            object = and__5579__auto__26143;
            Object var1_1 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$abstract_type_QMARK_.invokeStatic(object2);
    }
}

