/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class core$update_pnil
extends RestFn {
    public static final Var const__0 = RT.var("ginga.core", "assoc-pnil");
    public static final Var const__1 = RT.var("clojure.core", "apply");

    public static Object invokeStatic(Object m4, Object key2, Object f, ISeq args) {
        Object object = m4;
        Object object2 = key2;
        Object object3 = f;
        f = null;
        Object object4 = m4;
        m4 = null;
        Object object5 = key2;
        key2 = null;
        ISeq iSeq = args;
        args = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, ((IFn)const__1.getRawRoot()).invoke(object3, RT.get(object4, object5), iSeq));
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
        return core$update_pnil.invokeStatic(object5, object6, object7, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 3;
    }
}

