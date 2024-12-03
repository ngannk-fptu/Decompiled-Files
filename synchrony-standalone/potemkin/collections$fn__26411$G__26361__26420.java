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

public final class collections$fn__26411$G__26361__26420
extends AFunction {
    Object G__26362;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("potemkin.collections.PotemkinMap");

    public collections$fn__26411$G__26361__26420(Object object) {
        this.G__26362 = object;
    }

    @Override
    public Object invoke(Object gf__m__26417, Object gf__k__26418, Object gf__v__26419) {
        Object object;
        collections$fn__26411$G__26361__26420 this_;
        IFn f__8035__auto__26423;
        MethodImplCache cache__8034__auto__26422;
        MethodImplCache methodImplCache = cache__8034__auto__26422 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__26422 = null;
        IFn iFn = f__8035__auto__26423 = methodImplCache.fnFor(Util.classOf(gf__m__26417));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__26423;
            f__8035__auto__26423 = null;
            Object object2 = gf__m__26417;
            gf__m__26417 = null;
            Object object3 = gf__k__26418;
            gf__k__26418 = null;
            Object object4 = gf__v__26419;
            gf__v__26419 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3, object4);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__m__26417, const__1, this_.G__26362);
            Object object5 = gf__m__26417;
            gf__m__26417 = null;
            Object object6 = gf__k__26418;
            gf__k__26418 = null;
            Object object7 = gf__v__26419;
            gf__v__26419 = null;
            this_ = null;
            object = iFn3.invoke(object5, object6, object7);
        }
        return object;
    }
}

