/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class types$extend_implementations$fn__26091
extends AFunction {
    Object proto_val;
    public static final Var const__2 = RT.var("clojure.core", "contains?");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "on-interface"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "impls"));
    static ILookupThunk __thunk__1__ = __site__1__;

    public types$extend_implementations$fn__26091(Object object) {
        this.proto_val = object;
    }

    @Override
    public Object invoke(Object p1__26090_SHARP_) {
        Object object;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object2 = this_.proto_val;
        Object object3 = iLookupThunk.get(object2);
        if (iLookupThunk == object3) {
            __thunk__0__ = __site__0__.fault(object2);
            object3 = __thunk__0__.get(object2);
        }
        boolean or__5581__auto__26093 = Util.equiv(object3, p1__26090_SHARP_);
        if (or__5581__auto__26093) {
            object = or__5581__auto__26093 ? Boolean.TRUE : Boolean.FALSE;
        } else {
            IFn iFn = (IFn)const__2.getRawRoot();
            ILookupThunk iLookupThunk2 = __thunk__1__;
            Object object4 = this_.proto_val;
            Object object5 = iLookupThunk2.get(object4);
            if (iLookupThunk2 == object5) {
                __thunk__1__ = __site__1__.fault(object4);
                object5 = __thunk__1__.get(object4);
            }
            Object object6 = p1__26090_SHARP_;
            p1__26090_SHARP_ = null;
            types$extend_implementations$fn__26091 this_ = null;
            object = iFn.invoke(object5, object6);
        }
        return object;
    }
}

