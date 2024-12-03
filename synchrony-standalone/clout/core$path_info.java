/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;

public final class core$path_info
extends AFunction {
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "path-info"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "uri"));
    static ILookupThunk __thunk__1__ = __site__1__;

    public static Object invokeStatic(Object request2) {
        Object object;
        Object or__5581__auto__34673;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object2 = request2;
        Object object3 = iLookupThunk.get(object2);
        if (iLookupThunk == object3) {
            __thunk__0__ = __site__0__.fault(object2);
            object3 = __thunk__0__.get(object2);
        }
        Object object4 = or__5581__auto__34673 = object3;
        if (object4 != null && object4 != Boolean.FALSE) {
            object = or__5581__auto__34673;
            or__5581__auto__34673 = null;
        } else {
            ILookupThunk iLookupThunk2 = __thunk__1__;
            Object object5 = request2;
            request2 = null;
            object = iLookupThunk2.get(object5);
            if (iLookupThunk2 == object) {
                __thunk__1__ = __site__1__.fault(object5);
                object = __thunk__1__.get(object5);
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$path_info.invokeStatic(object2);
    }
}

