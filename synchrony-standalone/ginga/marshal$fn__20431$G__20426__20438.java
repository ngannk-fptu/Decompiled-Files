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

public final class marshal$fn__20431$G__20426__20438
extends AFunction {
    Object G__20427;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("ginga.marshal.Marshal");

    public marshal$fn__20431$G__20426__20438(Object object) {
        this.G__20427 = object;
    }

    @Override
    public Object invoke(Object gf__value__20436, Object gf__marshaller__20437) {
        Object object;
        marshal$fn__20431$G__20426__20438 this_;
        IFn f__8035__auto__20441;
        MethodImplCache cache__8034__auto__20440;
        MethodImplCache methodImplCache = cache__8034__auto__20440 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__20440 = null;
        IFn iFn = f__8035__auto__20441 = methodImplCache.fnFor(Util.classOf(gf__value__20436));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__20441;
            f__8035__auto__20441 = null;
            Object object2 = gf__value__20436;
            gf__value__20436 = null;
            Object object3 = gf__marshaller__20437;
            gf__marshaller__20437 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__value__20436, const__1, this_.G__20427);
            Object object4 = gf__value__20436;
            gf__value__20436 = null;
            Object object5 = gf__marshaller__20437;
            gf__marshaller__20437 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

