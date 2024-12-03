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

public final class zip$fn__20865$G__20803__20870
extends AFunction {
    Object G__20804;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.zip.Traverse");

    public zip$fn__20865$G__20803__20870(Object object) {
        this.G__20804 = object;
    }

    @Override
    public Object invoke(Object gf__z__20869) {
        Object object;
        zip$fn__20865$G__20803__20870 this_;
        IFn f__8035__auto__20873;
        MethodImplCache cache__8034__auto__20872;
        MethodImplCache methodImplCache = cache__8034__auto__20872 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__20872 = null;
        IFn iFn = f__8035__auto__20873 = methodImplCache.fnFor(Util.classOf(gf__z__20869));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__20873;
            f__8035__auto__20873 = null;
            Object object2 = gf__z__20869;
            gf__z__20869 = null;
            this_ = null;
            object = iFn2.invoke(object2);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__z__20869, const__1, this_.G__20804);
            Object object3 = gf__z__20869;
            gf__z__20869 = null;
            this_ = null;
            object = iFn3.invoke(object3);
        }
        return object;
    }
}

