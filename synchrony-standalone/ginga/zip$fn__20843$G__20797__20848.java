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

public final class zip$fn__20843$G__20797__20848
extends AFunction {
    Object G__20798;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.zip.Traverse");

    public zip$fn__20843$G__20797__20848(Object object) {
        this.G__20798 = object;
    }

    @Override
    public Object invoke(Object gf__z__20847) {
        Object object;
        zip$fn__20843$G__20797__20848 this_;
        IFn f__8035__auto__20851;
        MethodImplCache cache__8034__auto__20850;
        MethodImplCache methodImplCache = cache__8034__auto__20850 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__20850 = null;
        IFn iFn = f__8035__auto__20851 = methodImplCache.fnFor(Util.classOf(gf__z__20847));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__20851;
            f__8035__auto__20851 = null;
            Object object2 = gf__z__20847;
            gf__z__20847 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__z__20847, const__1, this_.G__20798);
            Object object3 = gf__z__20847;
            gf__z__20847 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

