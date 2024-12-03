/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.string$camel_case_to_dashes$fn__8196;
import java.util.regex.Pattern;

public final class string$camel_case_to_dashes
extends AFunction {
    public static final Var const__0 = RT.var("clojure.string", "lower-case");
    public static final Var const__1 = RT.var("clojure.string", "replace");
    public static final Object const__2 = Pattern.compile("([a-z]+)([A-Z]+)");

    public static Object invokeStatic(Object camel_cased) {
        Object object = camel_cased;
        camel_cased = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(object, const__2, new string$camel_case_to_dashes$fn__8196()));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return string$camel_case_to_dashes.invokeStatic(object2);
    }
}

