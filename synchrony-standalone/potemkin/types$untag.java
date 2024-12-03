/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$untag
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "with-meta");
    public static final Var const__1 = RT.var("clojure.core", "dissoc");
    public static final Var const__2 = RT.var("clojure.core", "meta");
    public static final Keyword const__3 = RT.keyword(null, "tag");

    public static Object invokeStatic(Object n) {
        Object object = n;
        Object object2 = n;
        n = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(object2), const__3));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$untag.invokeStatic(object2);
    }
}

