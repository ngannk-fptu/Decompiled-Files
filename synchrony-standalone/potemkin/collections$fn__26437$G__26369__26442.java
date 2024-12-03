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

public final class collections$fn__26437$G__26369__26442
extends AFunction {
    Object G__26370;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMap");

    public collections$fn__26437$G__26369__26442(Object object) {
        this.G__26370 = object;
    }

    @Override
    public Object invoke(Object gf__o__26441) {
        Object object;
        collections$fn__26437$G__26369__26442 this_;
        IFn f__8035__auto__26445;
        MethodImplCache cache__8034__auto__26444;
        MethodImplCache methodImplCache = cache__8034__auto__26444 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26444 = null;
        IFn iFn = f__8035__auto__26445 = methodImplCache.fnFor(Util.classOf(gf__o__26441));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26445;
            f__8035__auto__26445 = null;
            Object object2 = gf__o__26441;
            gf__o__26441 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__o__26441, const__1, this_.G__26370);
            Object object3 = gf__o__26441;
            gf__o__26441 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

