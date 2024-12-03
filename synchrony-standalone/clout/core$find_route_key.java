/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class core$find_route_key
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "first");
    public static final Keyword const__1 = RT.keyword(null, "param");
    public static final Var const__2 = RT.var("clojure.core", "keyword");
    public static final Var const__3 = RT.var("clojure.core", "second");
    public static final Keyword const__4 = RT.keyword(null, "wildcard");
    public static final Keyword const__5 = RT.keyword(null, "*");
    public static final Var const__6 = RT.var("clojure.core", "str");

    /*
     * Enabled aggressive block sorting
     */
    public static Object invokeStatic(Object form2) {
        Object G__34725 = ((IFn)const__0.getRawRoot()).invoke(form2);
        switch (Util.hash(G__34725) >> 2 & 1) {
            case 0: {
                if (G__34725 != const__1) break;
                Object object = form2;
                form2 = null;
                Object object2 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object)));
                return object2;
            }
            case 1: {
                if (G__34725 != const__4) break;
                Object object2 = const__5;
                return object2;
            }
        }
        Object object = G__34725;
        G__34725 = null;
        throw (Throwable)new IllegalArgumentException((String)((IFn)const__6.getRawRoot()).invoke("No matching clause: ", object));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$find_route_key.invokeStatic(object2);
    }
}

