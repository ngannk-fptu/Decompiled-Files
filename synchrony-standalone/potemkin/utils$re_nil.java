/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;

public final class utils$re_nil
extends AFunction {
    public static final Keyword const__1 = RT.keyword("potemkin.utils", "nil");

    public static Object invokeStatic(Object x) {
        Object object;
        Object object2 = x;
        x = null;
        Object x__26267__auto__26273 = object2;
        if (Util.identical(const__1, x__26267__auto__26273)) {
            object = null;
        } else {
            object = x__26267__auto__26273;
            Object var1_1 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return utils$re_nil.invokeStatic(object2);
    }
}

