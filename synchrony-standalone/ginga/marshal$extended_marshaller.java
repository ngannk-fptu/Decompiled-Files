/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Var;

public final class marshal$extended_marshaller
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "sequential?");
    public static final Var const__1 = RT.var("ginga.marshal", "marshal-vector");
    public static final Var const__2 = RT.var("clojure.core", "map?");
    public static final Var const__3 = RT.var("ginga.marshal", "marshal-map");
    public static final Var const__4 = RT.var("clojure.core", "set?");
    public static final Var const__5 = RT.var("ginga.marshal", "marshal-from-seq");
    public static final Object const__6 = 5L;
    public static final Keyword const__7 = RT.keyword(null, "else");

    public static Object invokeStatic(Object v, Object marshaller) {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(v);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3 = v;
            v = null;
            Object object4 = marshaller;
            marshaller = null;
            object = ((IFn)const__1.getRawRoot()).invoke(object3, object4);
        } else {
            Object object5 = ((IFn)const__2.getRawRoot()).invoke(v);
            if (object5 != null && object5 != Boolean.FALSE) {
                Object object6 = v;
                v = null;
                Object object7 = marshaller;
                marshaller = null;
                object = ((IFn)const__3.getRawRoot()).invoke(object6, object7);
            } else {
                Object object8 = ((IFn)const__4.getRawRoot()).invoke(v);
                if (object8 != null && object8 != Boolean.FALSE) {
                    Object object9 = v;
                    v = null;
                    Object object10 = marshaller;
                    marshaller = null;
                    object = ((IFn)const__5.getRawRoot()).invoke(const__6, object9, object10);
                } else {
                    Keyword keyword2 = const__7;
                    object = keyword2 != null && keyword2 != Boolean.FALSE ? null : null;
                }
            }
        }
        return object;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return marshal$extended_marshaller.invokeStatic(object3, object4);
    }
}

