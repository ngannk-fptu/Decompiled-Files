/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.MethodImplCache;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$fn__34679$G__34674__34686
extends AFunction {
    Object G__34675;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("clout.core.Route");

    public core$fn__34679$G__34674__34686(Object object) {
        this.G__34675 = object;
    }

    @Override
    public Object invoke(Object gf__route__34684, Object gf__request__34685) {
        Object object;
        core$fn__34679$G__34674__34686 this_;
        IFn f__8035__auto__34689;
        MethodImplCache cache__8034__auto__34688;
        MethodImplCache methodImplCache = cache__8034__auto__34688 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__34688 = null;
        IFn iFn = f__8035__auto__34689 = methodImplCache.fnFor(Util.classOf(gf__route__34684));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__34689;
            f__8035__auto__34689 = null;
            Object object2 = gf__route__34684;
            gf__route__34684 = null;
            Object object3 = gf__request__34685;
            gf__request__34685 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__route__34684, const__1, this_.G__34675);
            Object object4 = gf__route__34684;
            gf__route__34684 = null;
            Object object5 = gf__request__34685;
            gf__request__34685 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

