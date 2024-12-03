/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.MethodImplCache;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$fn__18635$G__18515__18642
extends AFunction {
    Object G__18516;
    public static final Var const__0 = RT.var("clojure.core", "-cache-protocol-fn");
    public static final Object const__1 = RT.classForName("clj_time.core.DateTimeProtocol");

    public core$fn__18635$G__18515__18642(Object object) {
        this.G__18516 = object;
    }

    @Override
    public Object invoke(Object gf__this__18640, Object gf__that__18641) {
        Object object;
        core$fn__18635$G__18515__18642 this_;
        IFn f__8035__auto__18645;
        MethodImplCache cache__8034__auto__18644;
        MethodImplCache methodImplCache = cache__8034__auto__18644 = ((AFunction)this_).__methodImplCache;
        cache__8034__auto__18644 = null;
        IFn iFn = f__8035__auto__18645 = methodImplCache.fnFor(Util.classOf(gf__this__18640));
        if (iFn != null && iFn != Boolean.FALSE) {
            IFn iFn2 = f__8035__auto__18645;
            f__8035__auto__18645 = null;
            Object object2 = gf__this__18640;
            gf__this__18640 = null;
            Object object3 = gf__that__18641;
            gf__that__18641 = null;
            this_ = null;
            object = iFn2.invoke(object2, object3);
        } else {
            IFn iFn3 = (IFn)((IFn)const__0.getRawRoot()).invoke(this_, gf__this__18640, const__1, this_.G__18516);
            Object object4 = gf__this__18640;
            gf__this__18640 = null;
            Object object5 = gf__that__18641;
            gf__that__18641 = null;
            this_ = null;
            object = iFn3.invoke(object4, object5);
        }
        return object;
    }
}

