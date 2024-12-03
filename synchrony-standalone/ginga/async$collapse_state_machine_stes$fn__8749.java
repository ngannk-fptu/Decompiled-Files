/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;

public final class async$collapse_state_machine_stes$fn__8749
extends AFunction {
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "class"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "method"));
    static ILookupThunk __thunk__1__ = __site__1__;
    static final KeywordLookupSite __site__2__ = new KeywordLookupSite(RT.keyword(null, "file"));
    static ILookupThunk __thunk__2__ = __site__2__;
    static final KeywordLookupSite __site__3__ = new KeywordLookupSite(RT.keyword(null, "line"));
    static ILookupThunk __thunk__3__ = __site__3__;

    @Override
    public Object invoke(Object p1__8743_SHARP_) {
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object = p1__8743_SHARP_;
        Object object2 = iLookupThunk.get(object);
        if (iLookupThunk == object2) {
            __thunk__0__ = __site__0__.fault(object);
            object2 = __thunk__0__.get(object);
        }
        String string2 = (String)object2;
        ILookupThunk iLookupThunk2 = __thunk__1__;
        Object object3 = p1__8743_SHARP_;
        Object object4 = iLookupThunk2.get(object3);
        if (iLookupThunk2 == object4) {
            __thunk__1__ = __site__1__.fault(object3);
            object4 = __thunk__1__.get(object3);
        }
        String string3 = (String)object4;
        ILookupThunk iLookupThunk3 = __thunk__2__;
        Object object5 = p1__8743_SHARP_;
        Object object6 = iLookupThunk3.get(object5);
        if (iLookupThunk3 == object6) {
            __thunk__2__ = __site__2__.fault(object5);
            object6 = __thunk__2__.get(object5);
        }
        String string4 = (String)object6;
        ILookupThunk iLookupThunk4 = __thunk__3__;
        Object object7 = p1__8743_SHARP_;
        p1__8743_SHARP_ = null;
        Object object8 = iLookupThunk4.get(object7);
        if (iLookupThunk4 == object8) {
            __thunk__3__ = __site__3__.fault(object7);
            object8 = __thunk__3__.get(object7);
        }
        return new StackTraceElement(string2, string3, string4, RT.intCast((Number)object8));
    }
}

