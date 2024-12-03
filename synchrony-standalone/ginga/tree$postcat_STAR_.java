/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.tree$postcat_STAR_$fn__20716;

public final class tree$postcat_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.tree", "postwalk*");

    public static Object invokeStatic(Object children2, Object with_children, Object post, Object node2) {
        Object object = children2;
        children2 = null;
        Object object2 = with_children;
        with_children = null;
        Object object3 = post;
        post = null;
        Object object4 = node2;
        node2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, new tree$postcat_STAR_$fn__20716(object2), object3, object4);
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
        return tree$postcat_STAR_.invokeStatic(object5, object6, object7, object8);
    }
}

