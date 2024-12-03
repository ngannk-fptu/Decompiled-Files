/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.Numbers;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import java.util.Arrays;

public final class core$sliding_resize
extends AFunction {
    public static final Var const__0 = RT.var("ginga.core", "sliding-resize");
    public static final Var const__3 = RT.var("clojure.core", "str");
    public static final Var const__4 = RT.var("clojure.core", "pr-str");
    public static final Object const__5 = ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "<="), Symbol.intern(null, "maintain-cnt"), Symbol.intern(null, "resize-cnt"))))).withMeta(RT.map(RT.keyword(null, "line"), 860, RT.keyword(null, "column"), 11));
    public static final Var const__7 = RT.var("clojure.core", "into");
    public static final Var const__8 = RT.var("clojure.core", "empty");
    public static final Var const__9 = RT.var("clojure.core", "subvec");

    public static Object invokeStatic(Object maintain_cnt, Object resize_cnt, Object v) {
        Object object;
        if (!Numbers.lte(maintain_cnt, resize_cnt)) {
            throw (Throwable)((Object)new AssertionError(((IFn)const__3.getRawRoot()).invoke("Assert failed: ", ((IFn)const__4.getRawRoot()).invoke(const__5))));
        }
        Object G__8509 = v;
        Object object2 = resize_cnt;
        resize_cnt = null;
        if (Numbers.lte(object2, (long)RT.count(v))) {
            Object object3 = G__8509;
            G__8509 = null;
            Object object4 = v;
            Object object5 = v;
            v = null;
            Object object6 = maintain_cnt;
            maintain_cnt = null;
            object = ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(object3), ((IFn)const__9.getRawRoot()).invoke(object4, Numbers.minus((long)RT.count(object5), object6)));
        } else {
            object = G__8509;
            Object var3_3 = null;
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        Object object6 = object3;
        object3 = null;
        return core$sliding_resize.invokeStatic(object4, object5, object6);
    }

    public static Object invokeStatic(Object cnt, Object v) {
        Object object = cnt;
        Object object2 = cnt;
        cnt = null;
        Object object3 = v;
        v = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, Numbers.inc(object2), object3);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return core$sliding_resize.invokeStatic(object3, object4);
    }
}

