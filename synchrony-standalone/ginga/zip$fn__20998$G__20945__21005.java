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

public final class zip$fn__20998$G__20945__21005
extends AFunction {
    Object G__20946;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.zip.Edit");

    public zip$fn__20998$G__20945__21005(Object object) {
        this.G__20946 = object;
    }

    @Override
    public Object invoke(Object gf__z__21003, Object gf__nodes__21004) {
        Object object;
        zip$fn__20998$G__20945__21005 this_;
        IFn f__8035__auto__21008;
        MethodImplCache cache__8034__auto__21007;
        MethodImplCache methodImplCache = cache__8034__auto__21007 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__21007 = null;
        IFn iFn = f__8035__auto__21008 = methodImplCache.fnFor(Util.classOf(gf__z__21003));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__21008;
            f__8035__auto__21008 = null;
            Object object2 = gf__z__21003;
            gf__z__21003 = null;
            Object object3 = gf__nodes__21004;
            gf__nodes__21004 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__z__21003, const__1, this_.G__20946);
            Object object4 = gf__z__21003;
            gf__z__21003 = null;
            Object object5 = gf__nodes__21004;
            gf__nodes__21004 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

