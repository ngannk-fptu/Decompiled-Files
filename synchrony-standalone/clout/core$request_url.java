/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class core$request_url
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "str");
    public static final Var const__1 = RT.var("clojure.core", "name");
    public static final Var const__3 = RT.var("clojure.core", "get-in");
    public static final AFn const__5 = (AFn)((Object)Tuple.create(RT.keyword(null, "headers"), "host"));
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "scheme"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "uri"));
    static ILookupThunk __thunk__1__ = __site__1__;

    public static Object invokeStatic(Object request2) {
        IFn iFn = (IFn)const__0.getRawRoot();
        IFn iFn2 = (IFn)const__1.getRawRoot();
        ILookupThunk iLookupThunk = __thunk__0__;
        Object object = request2;
        Object object2 = iLookupThunk.get(object);
        if (iLookupThunk == object2) {
            __thunk__0__ = __site__0__.fault(object);
            object2 = __thunk__0__.get(object);
        }
        Object object3 = iFn2.invoke(object2);
        Object object4 = ((IFn)const__3.getRawRoot()).invoke(request2, const__5);
        ILookupThunk iLookupThunk2 = __thunk__1__;
        Object object5 = request2;
        request2 = null;
        Object object6 = iLookupThunk2.get(object5);
        if (iLookupThunk2 == object6) {
            __thunk__1__ = __site__1__.fault(object5);
            object6 = __thunk__1__.get(object5);
        }
        return iFn.invoke(object3, "://", object4, object6);
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$request_url.invokeStatic(object2);
    }
}

