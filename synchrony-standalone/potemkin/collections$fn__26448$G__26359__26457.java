/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.MethodImplCache;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class collections$fn__26448$G__26359__26457
extends AFunction {
    Object G__26360;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMap");

    public collections$fn__26448$G__26359__26457(Object object) {
        this.G__26360 = object;
    }

    @Override
    public Object invoke(Object gf__m__26454, Object gf__k__26455, Object gf__default__26456) {
        Object object;
        collections$fn__26448$G__26359__26457 this_;
        IFn f__8035__auto__26460;
        MethodImplCache cache__8034__auto__26459;
        MethodImplCache methodImplCache = cache__8034__auto__26459 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26459 = null;
        IFn iFn = f__8035__auto__26460 = methodImplCache.fnFor(Util.classOf(gf__m__26454));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26460;
            f__8035__auto__26460 = null;
            Object object2 = gf__m__26454;
            gf__m__26454 = null;
            Object object3 = gf__k__26455;
            gf__k__26455 = null;
            Object object4 = gf__default__26456;
            gf__default__26456 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3, object4);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__m__26454, const__1, this_.G__26360);
            Object object5 = gf__m__26454;
            gf__m__26454 = null;
            Object object6 = gf__k__26455;
            gf__k__26455 = null;
            Object object7 = gf__default__26456;
            gf__default__26456 = null;
            this_ = null;
            object = iFn3.invoke(object5, object6, object7);
        }
        return object;
    }
}

