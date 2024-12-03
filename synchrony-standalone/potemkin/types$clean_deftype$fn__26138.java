/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.Numbers;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$clean_deftype$fn__26138
extends AFunction {
    Object version;
    public static final Var const__1 = RT.var("clojure.core", "meta");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "min-version"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public types$clean_deftype$fn__26138(Object object) {
        this.version = object;
    }

    @Override
    public Object invoke(Object p1__26136_SHARP_) {
        Boolean bl;
        Object temp__5804__auto__26140;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object = p1__26136_SHARP_;
        p1__26136_SHARP_ = null;
        Object object2 = ((IFn)const__1.getRawRoot()).invoke(object);
        Object object3 = iLookupThunk.get(object2);
        if (iLookupThunk == object3) {
            __thunk__0__ = __site__0__.fault(object2);
            object3 = __thunk__0__.get(object2);
        }
        Object object4 = temp__5804__auto__26140 = object3;
        if (object4 != null && object4 != Boolean.FALSE) {
            Object min_version;
            Object object5 = temp__5804__auto__26140;
            temp__5804__auto__26140 = null;
            Object object6 = min_version = object5;
            min_version = null;
            types$clean_deftype$fn__26138 this_ = null;
            bl = Numbers.isNeg(((String)this_.version).compareTo((String)object6)) ? Boolean.TRUE : Boolean.FALSE;
        } else {
            bl = null;
        }
        return bl;
    }
}

