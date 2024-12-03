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

public final class core$fn__18727$G__18533__18734
extends AFunction {
    Object G__18534;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("clj_time.core.DateTimeProtocol");

    public core$fn__18727$G__18533__18734(Object object) {
        this.G__18534 = object;
    }

    @Override
    public Object invoke(Object gf__this__18732, Object gf__that__18733) {
        Object object;
        core$fn__18727$G__18533__18734 this_;
        IFn f__8035__auto__18737;
        MethodImplCache cache__8034__auto__18736;
        MethodImplCache methodImplCache = cache__8034__auto__18736 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__18736 = null;
        IFn iFn = f__8035__auto__18737 = methodImplCache.fnFor(Util.classOf(gf__this__18732));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__18737;
            f__8035__auto__18737 = null;
            Object object2 = gf__this__18732;
            gf__this__18732 = null;
            Object object3 = gf__that__18733;
            gf__that__18733 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__this__18732, const__1, this_.G__18534);
            Object object4 = gf__this__18732;
            gf__this__18732 = null;
            Object object5 = gf__that__18733;
            gf__that__18733 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

