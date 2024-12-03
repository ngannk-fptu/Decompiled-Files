/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;

public final class utils$de_nil
extends AFunction {
    public static final Keyword const__1 = RT.keyword("potemkin.utils", "nil");

    public static Object invokeStatic(Object x) {
        Object object;
        Object object2 = x;
        x = null;
        Object x__26276__auto__26282 = object2;
        if (Util.identical(x__26276__auto__26282, null)) {
            object = const__1;
        } else {
            object = x__26276__auto__26282;
            Object var1_1 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return utils$de_nil.invokeStatic(object2);
    }
}

