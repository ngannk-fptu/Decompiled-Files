/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Var;

public final class core$cond__GT__PERCENT_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "concat");
    public static final Var const__2 = RT.var("clojure.core", "list");
    public static final AFn const__3 = Symbol.intern("clojure.core", "as->");
    public static final AFn const__4 = Symbol.intern(null, "%");
    public static final AFn const__5 = Symbol.intern("clojure.core", "cond->");
    public static final AFn const__6 = Symbol.intern(null, "%");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, Object expr, ISeq forms) {
        Object object = expr;
        expr = null;
        ISeq iSeq = forms;
        forms = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3), ((IFn)const__2.getRawRoot()).invoke(object), ((IFn)const__2.getRawRoot()).invoke(const__4), ((IFn)const__2.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__5), ((IFn)const__2.getRawRoot()).invoke(const__6), iSeq)))));
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
        return core$cond__GT__PERCENT_.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

