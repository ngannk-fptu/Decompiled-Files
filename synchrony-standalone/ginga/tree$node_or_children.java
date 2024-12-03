/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Tuple;

public final class tree$node_or_children
extends AFunction {
    public static Object invokeStatic(Object children2, Object pred2, Object node2) {
        Object object;
        Object object2 = pred2;
        pred2 = null;
        Object object3 = ((IFn)object2).invoke(node2);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = node2;
            node2 = null;
            object = Tuple.create(object4);
        } else {
            Object object5 = children2;
            children2 = null;
            Object object6 = node2;
            node2 = null;
            object = ((IFn)object5).invoke(object6);
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
        return tree$node_or_children.invokeStatic(object4, object5, object6);
    }
}

