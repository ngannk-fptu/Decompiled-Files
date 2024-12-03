/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;

public final class core$if_cljs
extends AFunction {
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "ns"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object then, Object object) {
        Object object2;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object3 = _AMPERSAND_env;
        _AMPERSAND_env = null;
        Object object4 = iLookupThunk.get(object3);
        if (iLookupThunk == object4) {
            __thunk__0__ = __site__0__.fault(object3);
            object4 = __thunk__0__.get(object3);
        }
        if (RT.booleanCast(object4)) {
            object2 = then;
            then = null;
        } else {
            object2 = object;
            Object var3_3 = null;
        }
        return object2;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return core$if_cljs.invokeStatic(object5, object6, object7, object8);
    }
}

