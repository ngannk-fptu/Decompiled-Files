/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class utils$try_STAR_$ignore_retry__26248
extends AFunction {
    public static final Var const__2 = RT.var("clojure.core", "seq");
    public static final Var const__3 = RT.var("clojure.core", "concat");
    public static final Var const__4 = RT.var("clojure.core", "take");
    public static final Object const__5 = 3L;
    public static final Var const__6 = RT.var("clojure.core", "list");
    public static final AFn const__7 = Symbol.intern(null, "if");
    public static final AFn const__8 = Symbol.intern("potemkin.utils", "retry-exception?");
    public static final AFn const__9 = Symbol.intern(null, "throw");
    public static final AFn const__10 = Symbol.intern(null, "do");
    public static final Var const__11 = RT.var("clojure.core", "drop");

    @Override
    public Object invoke(Object x) {
        Object object;
        Object object2 = x;
        if (object2 != null && object2 != Boolean.FALSE) {
            Object ex = RT.nth(x, RT.intCast(2L));
            Object object3 = ((IFn)const__4.getRawRoot()).invoke(const__5, x);
            Object object4 = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__8), ((IFn)const__6.getRawRoot()).invoke(ex))));
            Object object5 = ex;
            ex = null;
            Object object6 = x;
            x = null;
            utils$try_STAR_$ignore_retry__26248 this_ = null;
            object = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(object3, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__7), object4, ((IFn)const__6.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__9), ((IFn)const__6.getRawRoot()).invoke(object5)))), ((IFn)const__6.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(const__10), ((IFn)const__11.getRawRoot()).invoke(const__5, object6)))))))));
        } else {
            object = null;
        }
        return object;
    }
}

