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

public final class collections$fn__26470$G__26465__26477
extends AFunction {
    Object G__26466;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMeta");

    public collections$fn__26470$G__26465__26477(Object object) {
        this.G__26466 = object;
    }

    @Override
    public Object invoke(Object gf_____26475, Object gf__x__26476) {
        Object object;
        collections$fn__26470$G__26465__26477 this_;
        IFn f__8035__auto__26480;
        MethodImplCache cache__8034__auto__26479;
        MethodImplCache methodImplCache = cache__8034__auto__26479 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26479 = null;
        IFn iFn = f__8035__auto__26480 = methodImplCache.fnFor(Util.classOf(gf_____26475));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26480;
            f__8035__auto__26480 = null;
            Object object2 = gf_____26475;
            gf_____26475 = null;
            Object object3 = gf__x__26476;
            gf__x__26476 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf_____26475, const__1, this_.G__26466);
            Object object4 = gf_____26475;
            gf_____26475 = null;
            Object object5 = gf__x__26476;
            gf__x__26476 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

