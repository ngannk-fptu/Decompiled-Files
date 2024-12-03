/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.MethodImplCache;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$fn__8248$G__8243__8253
extends AFunction {
    Object G__8244;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.core.KwType");

    public core$fn__8248$G__8243__8253(Object object) {
        this.G__8244 = object;
    }

    @Override
    public Object invoke(Object gf__obj__8252) {
        Object object;
        core$fn__8248$G__8243__8253 this_;
        IFn f__8035__auto__8256;
        MethodImplCache cache__8034__auto__8255;
        MethodImplCache methodImplCache = cache__8034__auto__8255 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__8255 = null;
        IFn iFn = f__8035__auto__8256 = methodImplCache.fnFor(Util.classOf(gf__obj__8252));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__8256;
            f__8035__auto__8256 = null;
            Object object2 = gf__obj__8252;
            gf__obj__8252 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__obj__8252, const__1, this_.G__8244);
            Object object3 = gf__obj__8252;
            gf__obj__8252 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

