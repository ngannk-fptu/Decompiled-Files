/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;

public final class core$parse
extends AFunction {
    public static final Var const__0 = RT.var("instaparse.core", "parse");
    public static final Var const__1 = RT.var("instaparse.core", "failure?");
    public static final Var const__2 = RT.var("clojure.core", "ex-info");
    public static final Keyword const__3 = RT.keyword(null, "failure");

    public static Object invokeStatic(Object parser2, Object text2) {
        Object object = parser2;
        parser2 = null;
        Object object2 = text2;
        text2 = null;
        Object result = ((IFn)const__0.getRawRoot()).invoke(object, object2);
        Object object3 = ((IFn)const__1.getRawRoot()).invoke(result);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object[] objectArray = new Object[2];
            objectArray[0] = const__3;
            Object object4 = result;
            result = null;
            objectArray[1] = object4;
            throw (Throwable)((IFn)const__2.getRawRoot()).invoke("Parse error in route string", RT.mapUniqueKeys(objectArray));
        }
        Object var2_2 = null;
        return result;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$parse.invokeStatic(object3, object4);
    }
}

