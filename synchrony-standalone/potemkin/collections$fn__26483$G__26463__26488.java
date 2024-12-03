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

public final class collections$fn__26483$G__26463__26488
extends AFunction {
    Object G__26464;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMeta");

    public collections$fn__26483$G__26463__26488(Object object) {
        this.G__26464 = object;
    }

    @Override
    public Object invoke(Object gf_____26487) {
        Object object;
        collections$fn__26483$G__26463__26488 this_;
        IFn f__8035__auto__26491;
        MethodImplCache cache__8034__auto__26490;
        MethodImplCache methodImplCache = cache__8034__auto__26490 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26490 = null;
        IFn iFn = f__8035__auto__26491 = methodImplCache.fnFor(Util.classOf(gf_____26487));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26491;
            f__8035__auto__26491 = null;
            Object object2 = gf_____26487;
            gf_____26487 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf_____26487, const__1, this_.G__26464);
            Object object3 = gf_____26487;
            gf_____26487 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

