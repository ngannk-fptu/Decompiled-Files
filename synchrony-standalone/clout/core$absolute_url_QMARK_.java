/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import java.util.regex.Pattern;

public final class core$absolute_url_QMARK_
extends AFunction {
    public static final Var const__1 = RT.var("clojure.core", "re-matches");
    public static final Object const__2 = Pattern.compile("(https?:)?//.*");

    public static Object invokeStatic(Object path2) {
        Object object = path2;
        path2 = null;
        return RT.booleanCast(((IFn)const__1.getRawRoot()).invoke(const__2, object)) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return core$absolute_url_QMARK_.invokeStatic(object2);
    }
}

