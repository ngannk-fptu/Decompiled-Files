/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Var;

public final class log$with_cljs_tag_log
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "apply");
    public static final Var const__1 = RT.var("ginga.log", "cljs-tag-log");

    public static Object invokeStatic(Object tag2, Object f, ISeq args) {
        Object object = f;
        f = null;
        ISeq iSeq = args;
        args = null;
        Object result = ((IFn)const__0.getRawRoot()).invoke(object, iSeq);
        Object object2 = tag2;
        tag2 = null;
        ((IFn)const__1.getRawRoot()).invoke(object2, result);
        Object var3_3 = null;
        return result;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return log$with_cljs_tag_log.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

