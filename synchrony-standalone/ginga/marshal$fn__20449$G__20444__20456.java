/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.MethodImplCache;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class marshal$fn__20449$G__20444__20456
extends AFunction {
    Object G__20445;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.marshal.Unmarshal");

    public marshal$fn__20449$G__20444__20456(Object object) {
        this.G__20445 = object;
    }

    @Override
    public Object invoke(Object gf__value__20454, Object gf__unmarshaller__20455) {
        Object object;
        marshal$fn__20449$G__20444__20456 this_;
        IFn f__8035__auto__20459;
        MethodImplCache cache__8034__auto__20458;
        MethodImplCache methodImplCache = cache__8034__auto__20458 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__20458 = null;
        IFn iFn = f__8035__auto__20459 = methodImplCache.fnFor(Util.classOf(gf__value__20454));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__20459;
            f__8035__auto__20459 = null;
            Object object2 = gf__value__20454;
            gf__value__20454 = null;
            Object object3 = gf__unmarshaller__20455;
            gf__unmarshaller__20455 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__value__20454, const__1, this_.G__20445);
            Object object4 = gf__value__20454;
            gf__value__20454 = null;
            Object object5 = gf__unmarshaller__20455;
            gf__unmarshaller__20455 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

