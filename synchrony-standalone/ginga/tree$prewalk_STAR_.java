/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class tree$prewalk_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.tree", "prepostwalk*");
    public static final Var const__1 = RT.var("clojure.core", "identity");

    public static Object invokeStatic(Object children2, Object with_children, Object pre, Object node2) {
        Object object = children2;
        children2 = null;
        Object object2 = with_children;
        with_children = null;
        Object object3 = pre;
        pre = null;
        Object object4 = node2;
        node2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, object2, object3, const__1.getRawRoot(), object4);
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
        return tree$prewalk_STAR_.invokeStatic(object5, object6, object7, object8);
    }
}

