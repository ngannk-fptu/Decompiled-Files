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

public final class core$fn__18803$G__18762__18808
extends AFunction {
    Object G__18763;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("clj_time.core.InTimeUnitProtocol");

    public core$fn__18803$G__18762__18808(Object object) {
        this.G__18763 = object;
    }

    @Override
    public Object invoke(Object gf__this__18807) {
        Object object;
        core$fn__18803$G__18762__18808 this_;
        IFn f__8035__auto__18811;
        MethodImplCache cache__8034__auto__18810;
        MethodImplCache methodImplCache = cache__8034__auto__18810 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__18810 = null;
        IFn iFn = f__8035__auto__18811 = methodImplCache.fnFor(Util.classOf(gf__this__18807));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__18811;
            f__8035__auto__18811 = null;
            Object object2 = gf__this__18807;
            gf__this__18807 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__this__18807, const__1, this_.G__18763);
            Object object3 = gf__this__18807;
            gf__this__18807 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

