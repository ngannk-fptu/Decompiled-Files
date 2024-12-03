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

public final class collections$fn__26374$G__26367__26381
extends AFunction {
    Object G__26368;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMap");

    public collections$fn__26374$G__26367__26381(Object object) {
        this.G__26368 = object;
    }

    @Override
    public Object invoke(Object gf__o__26379, Object gf__mta__26380) {
        Object object;
        collections$fn__26374$G__26367__26381 this_;
        IFn f__8035__auto__26384;
        MethodImplCache cache__8034__auto__26383;
        MethodImplCache methodImplCache = cache__8034__auto__26383 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26383 = null;
        IFn iFn = f__8035__auto__26384 = methodImplCache.fnFor(Util.classOf(gf__o__26379));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26384;
            f__8035__auto__26384 = null;
            Object object2 = gf__o__26379;
            gf__o__26379 = null;
            Object object3 = gf__mta__26380;
            gf__mta__26380 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__o__26379, const__1, this_.G__26368);
            Object object4 = gf__o__26379;
            gf__o__26379 = null;
            Object object5 = gf__mta__26380;
            gf__mta__26380 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

