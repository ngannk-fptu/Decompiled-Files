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

public final class zip$fn__20985$G__20941__20992
extends AFunction {
    Object G__20942;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.zip.Edit");

    public zip$fn__20985$G__20941__20992(Object object) {
        this.G__20942 = object;
    }

    @Override
    public Object invoke(Object gf__z__20990, Object gf__node__20991) {
        Object object;
        zip$fn__20985$G__20941__20992 this_;
        IFn f__8035__auto__20995;
        MethodImplCache cache__8034__auto__20994;
        MethodImplCache methodImplCache = cache__8034__auto__20994 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__20994 = null;
        IFn iFn = f__8035__auto__20995 = methodImplCache.fnFor(Util.classOf(gf__z__20990));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__20995;
            f__8035__auto__20995 = null;
            Object object2 = gf__z__20990;
            gf__z__20990 = null;
            Object object3 = gf__node__20991;
            gf__node__20991 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__z__20990, const__1, this_.G__20942);
            Object object4 = gf__z__20990;
            gf__z__20990 = null;
            Object object5 = gf__node__20991;
            gf__node__20991 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

