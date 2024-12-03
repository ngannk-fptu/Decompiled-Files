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

public final class core$fn__18554$G__18541__18561
extends AFunction {
    Object G__18542;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("clj_time.core.DateTimeProtocol");

    public core$fn__18554$G__18541__18561(Object object) {
        this.G__18542 = object;
    }

    @Override
    public Object invoke(Object gf__this__18559, Object gf__period__18560) {
        Object object;
        core$fn__18554$G__18541__18561 this_;
        IFn f__8035__auto__18564;
        MethodImplCache cache__8034__auto__18563;
        MethodImplCache methodImplCache = cache__8034__auto__18563 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__18563 = null;
        IFn iFn = f__8035__auto__18564 = methodImplCache.fnFor(Util.classOf(gf__this__18559));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__18564;
            f__8035__auto__18564 = null;
            Object object2 = gf__this__18559;
            gf__this__18559 = null;
            Object object3 = gf__period__18560;
            gf__period__18560 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__this__18559, const__1, this_.G__18542);
            Object object4 = gf__this__18559;
            gf__this__18559 = null;
            Object object5 = gf__period__18560;
            gf__period__18560 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

