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

public final class zip$fn__21035$G__20927__21040
extends AFunction {
    Object G__20928;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.zip.Edit");

    public zip$fn__21035$G__20927__21040(Object object) {
        this.G__20928 = object;
    }

    @Override
    public Object invoke(Object gf__z__21039) {
        Object object;
        zip$fn__21035$G__20927__21040 this_;
        IFn f__8035__auto__21043;
        MethodImplCache cache__8034__auto__21042;
        MethodImplCache methodImplCache = cache__8034__auto__21042 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__21042 = null;
        IFn iFn = f__8035__auto__21043 = methodImplCache.fnFor(Util.classOf(gf__z__21039));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__21043;
            f__8035__auto__21043 = null;
            Object object2 = gf__z__21039;
            gf__z__21039 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__z__21039, const__1, this_.G__20928);
            Object object3 = gf__z__21039;
            gf__z__21039 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

