/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$deftype_STAR___GT_fn_map$fn__GT_key__26162$fn__26163
extends AFunction {
    public static final Var const__1 = RT.var("clojure.core", "meta");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "tag"));
    static ILookupThunk __thunk__0__ = __site__0__;

    @Override
    public Object invoke(Object p1__26161_SHARP_) {
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object = p1__26161_SHARP_;
        p1__26161_SHARP_ = null;
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(object);
        Object object3 = iLookupThunk.get(object2);
        if (iLookupThunk == object3) {
            __thunk__0__ = __site__0__.fault(object2);
            object3 = __thunk__0__.get(object2);
        }
        return object3;
    }
}

