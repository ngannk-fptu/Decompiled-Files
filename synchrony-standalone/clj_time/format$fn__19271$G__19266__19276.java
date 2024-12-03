/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.MethodImplCache;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class format$fn__19271$G__19266__19276
extends AFunction {
    Object G__19267;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("clj_time.format.Mappable");

    public format$fn__19271$G__19266__19276(Object object) {
        this.G__19267 = object;
    }

    @Override
    public Object invoke(Object gf__instant__19275) {
        Object object;
        format$fn__19271$G__19266__19276 this_;
        IFn f__8035__auto__19279;
        MethodImplCache cache__8034__auto__19278;
        MethodImplCache methodImplCache = cache__8034__auto__19278 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__19278 = null;
        IFn iFn = f__8035__auto__19279 = methodImplCache.fnFor(Util.classOf(gf__instant__19275));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__19279;
            f__8035__auto__19279 = null;
            Object object2 = gf__instant__19275;
            gf__instant__19275 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__instant__19275, const__1, this_.G__19267);
            Object object3 = gf__instant__19275;
            gf__instant__19275 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

