/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import riddley.walk$case_handler$fn__14801;

public final class walk$case_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "butlast");
    public static final Var const__1 = RT.var("clojure.core", "take-while");
    public static final Var const__2 = RT.var("clojure.core", "complement");
    public static final Var const__3 = RT.var("clojure.core", "map?");
    public static final Var const__4 = RT.var("clojure.core", "last");
    public static final Var const__5 = RT.var("clojure.core", "first");
    public static final Var const__6 = RT.var("clojure.core", "drop-while");
    public static final Var const__7 = RT.var("clojure.core", "rest");
    public static final Var const__8 = RT.var("clojure.core", "concat");
    public static final Var const__9 = RT.var("clojure.core", "into");
    public static final Var const__10 = RT.var("clojure.core", "map");
    public static final Var const__11 = RT.var("clojure.core", "every?");
    public static final Var const__12 = RT.var("clojure.core", "number?");
    public static final Var const__13 = RT.var("clojure.core", "keys");
    public static final Var const__14 = RT.var("clojure.core", "sorted-map");

    public static Object invokeStatic(Object f, Object x) {
        Object object;
        Object prefix = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot()), x));
        Object object2 = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot()), x));
        Object body = ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot()), x));
        Object object3 = x;
        x = null;
        Object suffix = ((IFn)const__7.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot()), object3));
        IFn iFn = (IFn)const__8.getRawRoot();
        Object object4 = prefix;
        prefix = null;
        Object object5 = object2;
        object2 = null;
        IPersistentVector iPersistentVector = Tuple.create(((IFn)f).invoke(object5));
        Object object6 = f;
        f = null;
        Object object7 = body;
        body = null;
        Object m4 = ((IFn)const__9.getRawRoot()).invoke(PersistentArrayMap.EMPTY, ((IFn)const__10.getRawRoot()).invoke(new walk$case_handler$fn__14801(object6), object7));
        Object object8 = ((IFn)const__11.getRawRoot()).invoke(const__12.getRawRoot(), ((IFn)const__13.getRawRoot()).invoke(m4));
        if (object8 != null && object8 != Boolean.FALSE) {
            Object object9 = m4;
            m4 = null;
            object = ((IFn)const__9.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(), object9);
        } else {
            object = m4;
            m4 = null;
        }
        Object object10 = suffix;
        suffix = null;
        return iFn.invoke(object4, iPersistentVector, Tuple.create(object), object10);
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$case_handler.invokeStatic(object3, object4);
    }
}

