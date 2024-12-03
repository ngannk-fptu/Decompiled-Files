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

public final class zip$fn__20887$G__20801__20892
extends AFunction {
    Object G__20802;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.zip.Traverse");

    public zip$fn__20887$G__20801__20892(Object object) {
        this.G__20802 = object;
    }

    @Override
    public Object invoke(Object gf__z__20891) {
        Object object;
        zip$fn__20887$G__20801__20892 this_;
        IFn f__8035__auto__20895;
        MethodImplCache cache__8034__auto__20894;
        MethodImplCache methodImplCache = cache__8034__auto__20894 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__20894 = null;
        IFn iFn = f__8035__auto__20895 = methodImplCache.fnFor(Util.classOf(gf__z__20891));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__20895;
            f__8035__auto__20895 = null;
            Object object2 = gf__z__20891;
            gf__z__20891 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__z__20891, const__1, this_.G__20802);
            Object object3 = gf__z__20891;
            gf__z__20891 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

