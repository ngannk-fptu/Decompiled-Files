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

public final class collections$fn__26426$G__26365__26431
extends AFunction {
    Object G__26366;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMap");

    public collections$fn__26426$G__26365__26431(Object object) {
        this.G__26366 = object;
    }

    @Override
    public Object invoke(Object gf__m__26430) {
        Object object;
        collections$fn__26426$G__26365__26431 this_;
        IFn f__8035__auto__26434;
        MethodImplCache cache__8034__auto__26433;
        MethodImplCache methodImplCache = cache__8034__auto__26433 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26433 = null;
        IFn iFn = f__8035__auto__26434 = methodImplCache.fnFor(Util.classOf(gf__m__26430));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26434;
            f__8035__auto__26434 = null;
            Object object2 = gf__m__26430;
            gf__m__26430 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__m__26430, const__1, this_.G__26366);
            Object object3 = gf__m__26430;
            gf__m__26430 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

