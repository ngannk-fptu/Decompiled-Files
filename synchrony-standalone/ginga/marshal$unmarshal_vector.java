/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.marshal$unmarshal_vector$fn__20494;

public final class marshal$unmarshal_vector
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "into");
    public static final Var const__1 = RT.var("clojure.core", "map");
    public static final Var const__2 = RT.var("clojure.core", "next");

    public static Object invokeStatic(Object v, Object unmarshaller) {
        Object object = unmarshaller;
        unmarshaller = null;
        Object object2 = v;
        v = null;
        return ((IFn)const__0.getRawRoot()).invoke(PersistentVector.EMPTY, ((IFn)const__1.getRawRoot()).invoke(new marshal$unmarshal_vector$fn__20494(object)), ((IFn)const__2.getRawRoot()).invoke(object2));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return marshal$unmarshal_vector.invokeStatic(object3, object4);
    }
}

