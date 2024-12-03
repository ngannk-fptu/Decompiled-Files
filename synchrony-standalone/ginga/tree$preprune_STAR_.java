/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import ginga.tree$preprune_STAR_$fn__20742;

public final class tree$preprune_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.tree", "postwalk*");
    public static final Var const__1 = RT.var("clojure.core", "identity");

    public static Object invokeStatic(Object children2, Object with_children, Object pred2, Object node2) {
        IPersistentVector iPersistentVector;
        Object object = ((IFn)pred2).invoke(node2);
        if (object != null && object != Boolean.FALSE) {
            Object object2 = pred2;
            pred2 = null;
            Object object3 = children2;
            children2 = null;
            Object object4 = with_children;
            with_children = null;
            Object object5 = node2;
            node2 = null;
            iPersistentVector = Tuple.create(((IFn)const__0.getRawRoot()).invoke(new tree$preprune_STAR_$fn__20742(object2, object3), object4, const__1.getRawRoot(), object5));
        } else {
            iPersistentVector = null;
        }
        return iPersistentVector;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return tree$preprune_STAR_.invokeStatic(object5, object6, object7, object8);
    }
}

