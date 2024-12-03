/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.RT;

public final class core$last_index_of
extends AFunction {
    public static final Keyword const__4 = RT.keyword(null, "else");

    public static Object invokeStatic(Object pred2, Object v) {
        Number number;
        block3: {
            long i = Numbers.dec(RT.count(v));
            while (true) {
                if (i < 0L) {
                    number = Numbers.num(i);
                    break block3;
                }
                Object object = ((IFn)pred2).invoke(RT.nth(v, RT.intCast(i)));
                if (object != null && object != Boolean.FALSE) {
                    number = Numbers.num(i);
                    break block3;
                }
                Keyword keyword2 = const__4;
                if (keyword2 == null || keyword2 == Boolean.FALSE) break;
                i = Numbers.dec(i);
            }
            number = null;
        }
        return number;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$last_index_of.invokeStatic(object3, object4);
    }
}

