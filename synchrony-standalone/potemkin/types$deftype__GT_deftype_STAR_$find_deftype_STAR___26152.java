/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;

public final class types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "sequential?");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final AFn const__3 = Symbol.intern(null, "deftype*");
    public static final Var const__4 = RT.var("clojure.core", "filter");

    @Override
    public Object invoke(Object x) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(x);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object f;
            Object object3 = f = ((IFn)const__1.getRawRoot()).invoke(x);
            f = null;
            if (Util.equiv((Object)const__3, object3)) {
                object = x;
                x = null;
            } else {
                Object object4 = x;
                x = null;
                Object object5 = ((IFn)const__4.getRawRoot()).invoke(this_, object4);
                types$deftype__GT_deftype_STAR_$find_deftype_STAR___26152 this_ = null;
                object = ((IFn)const__1.getRawRoot()).invoke(object5);
            }
        } else {
            object = null;
        }
        return object;
    }
}

