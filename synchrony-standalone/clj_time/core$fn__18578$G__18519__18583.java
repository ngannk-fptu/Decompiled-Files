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

public final class core$fn__18578$G__18519__18583
extends AFunction {
    Object G__18520;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("clj_time.core.DateTimeProtocol");

    public core$fn__18578$G__18519__18583(Object object) {
        this.G__18520 = object;
    }

    @Override
    public Object invoke(Object gf__this__18582) {
        Object object;
        core$fn__18578$G__18519__18583 this_;
        IFn f__8035__auto__18586;
        MethodImplCache cache__8034__auto__18585;
        MethodImplCache methodImplCache = cache__8034__auto__18585 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__18585 = null;
        IFn iFn = f__8035__auto__18586 = methodImplCache.fnFor(Util.classOf(gf__this__18582));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__18586;
            f__8035__auto__18586 = null;
            Object object2 = gf__this__18582;
            gf__this__18582 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__this__18582, const__1, this_.G__18520);
            Object object3 = gf__this__18582;
            gf__this__18582 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

