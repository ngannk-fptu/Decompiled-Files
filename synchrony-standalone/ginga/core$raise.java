/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class core$raise
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "ex-info");
    public static final Var const__1 = RT.var("clojure.core", "apply");
    public static final Var const__2 = RT.var("clojure.core", "print-str");

    public static Object invokeStatic(ISeq print_args) {
        ISeq iSeq = print_args;
        print_args = null;
        throw (Throwable)((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), iSeq), PersistentArrayMap.EMPTY);
    }

    @Override
    public Object doInvoke(Object object) {
        ISeq iSeq = (ISeq)object;
        object = null;
        return core$raise.invokeStatic(iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 0;
    }
}

