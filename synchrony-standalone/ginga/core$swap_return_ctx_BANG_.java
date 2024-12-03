/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class core$swap_return_ctx_BANG_
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "deref");
    public static final Var const__1 = RT.var("clojure.core", "apply");
    public static final Var const__5 = RT.var("clojure.core", "compare-and-set!");

    public static Object invokeStatic(Object atom2, Object f, ISeq args) {
        Object ctx2;
        while (true) {
            Object old_val = ((IFn)const__0.getRawRoot()).invoke(atom2);
            Object vec__8421 = ((IFn)const__1.getRawRoot()).invoke(f, old_val, args);
            ctx2 = RT.nth(vec__8421, RT.intCast(0L), null);
            Object object = vec__8421;
            vec__8421 = null;
            Object new_val = RT.nth(object, RT.intCast(1L), null);
            Object object2 = old_val;
            old_val = null;
            Object object3 = new_val;
            new_val = null;
            Object object4 = ((IFn)const__5.getRawRoot()).invoke(atom2, object2, object3);
            if (object4 == null) continue;
            if (object4 == Boolean.FALSE) continue;
            break;
        }
        Object object = ctx2;
        ctx2 = null;
        return object;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return core$swap_return_ctx_BANG_.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

