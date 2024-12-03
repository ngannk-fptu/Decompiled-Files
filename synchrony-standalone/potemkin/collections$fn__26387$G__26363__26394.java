/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.MethodImplCache;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class collections$fn__26387$G__26363__26394
extends AFunction {
    Object G__26364;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMap");

    public collections$fn__26387$G__26363__26394(Object object) {
        this.G__26364 = object;
    }

    @Override
    public Object invoke(Object gf__m__26392, Object gf__k__26393) {
        Object object;
        collections$fn__26387$G__26363__26394 this_;
        IFn f__8035__auto__26397;
        MethodImplCache cache__8034__auto__26396;
        MethodImplCache methodImplCache = cache__8034__auto__26396 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26396 = null;
        IFn iFn = f__8035__auto__26397 = methodImplCache.fnFor(Util.classOf(gf__m__26392));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26397;
            f__8035__auto__26397 = null;
            Object object2 = gf__m__26392;
            gf__m__26392 = null;
            Object object3 = gf__k__26393;
            gf__k__26393 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__m__26392, const__1, this_.G__26364);
            Object object4 = gf__m__26392;
            gf__m__26392 = null;
            Object object5 = gf__k__26393;
            gf__k__26393 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

