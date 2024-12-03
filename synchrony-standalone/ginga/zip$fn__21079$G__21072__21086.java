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

public final class zip$fn__21079$G__21072__21086
extends AFunction {
    Object G__21073;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.zip.Nth");

    public zip$fn__21079$G__21072__21086(Object object) {
        this.G__21073 = object;
    }

    @Override
    public Object invoke(Object gf__z__21084, Object gf__i__21085) {
        Object object;
        zip$fn__21079$G__21072__21086 this_;
        IFn f__8035__auto__21089;
        MethodImplCache cache__8034__auto__21088;
        MethodImplCache methodImplCache = cache__8034__auto__21088 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__21088 = null;
        IFn iFn = f__8035__auto__21089 = methodImplCache.fnFor(Util.classOf(gf__z__21084));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__21089;
            f__8035__auto__21089 = null;
            Object object2 = gf__z__21084;
            gf__z__21084 = null;
            Object object3 = gf__i__21085;
            gf__i__21085 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__z__21084, const__1, this_.G__21073);
            Object object4 = gf__z__21084;
            gf__z__21084 = null;
            Object object5 = gf__i__21085;
            gf__i__21085 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

