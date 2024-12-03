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

public final class types$definterface_PLUS_$fn__26190$fn__26194
extends AFunction {
    Object fn_name;
    public static final Var const__0 = RT.var("clojure.core", "list");
    public static final Var const__1 = RT.var("clojure.core", "with-meta");
    public static final Var const__2 = RT.var("potemkin.types", "munge-fn-name");
    public static final Keyword const__3 = RT.keyword(null, "tag");
    public static final Var const__4 = RT.var("clojure.core", "meta");
    public static final Var const__5 = RT.var("potemkin.types", "resolve-tag");
    public static final Var const__6 = RT.var("clojure.core", "vec");
    public static final Var const__7 = RT.var("clojure.core", "map");
    public static final Var const__8 = RT.var("clojure.core", "rest");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "tag"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public types$definterface_PLUS_$fn__26190$fn__26194(Object object) {
        this.fn_name = object;
    }

    @Override
    public Object invoke(Object p1__26186_SHARP_) {
        IFn iFn = (IFn)const__0.getRawRoot();
        IFn iFn2 = (IFn)const__1.getRawRoot();
        Object object = ((IFn)const__2.getRawRoot()).invoke(this_.fn_name);
        Object[] objectArray = new Object[2];
        objectArray[0] = const__3;
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object2 = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__5.getRawRoot()).invoke(p1__26186_SHARP_));
        Object object3 = iLookupThunk.get(object2);
        if (iLookupThunk == object3) {
            __thunk__0__ = __site__0__.fault(object2);
            object3 = __thunk__0__.get(object2);
        }
        objectArray[1] = object3;
        Object object4 = p1__26186_SHARP_;
        p1__26186_SHARP_ = null;
        types$definterface_PLUS_$fn__26190$fn__26194 this_ = null;
        return iFn.invoke(iFn2.invoke(object, RT.mapUniqueKeys(objectArray)), ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__7.getRawRoot()).invoke(const__5.getRawRoot(), ((IFn)const__8.getRawRoot()).invoke(object4)))));
    }
}

