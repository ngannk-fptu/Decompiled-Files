/*
 * Decompiled with CFR 0.152.
 */
package clj_time;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class core$when_available
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "resolve");
    public static final Var const__1 = RT.var("clojure.core", "seq");
    public static final Var const__2 = RT.var("clojure.core", "concat");
    public static final Var const__3 = RT.var("clojure.core", "list");
    public static final AFn const__4 = Symbol.intern(null, "do");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object sym, ISeq body) {
        Object object;
        Object object2 = sym;
        sym = null;
        Object object3 = ((IFn)const__0.getRawRoot()).invoke(object2);
        if (object3 != null && object3 != Boolean.FALSE) {
            ISeq iSeq = body;
            body = null;
            object = ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(const__4), iSeq));
        } else {
            object = null;
        }
        return object;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        ISeq iSeq = (ISeq)object4;
        object4 = null;
        return core$when_available.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

