/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import ginga.tree$lazy_precat_STAR_$iter__20720__20724;

public final class tree$lazy_precat_STAR_
extends AFunction {
    public static Object invokeStatic(Object children2, Object with_children, Object pre, Object node2) {
        tree$lazy_precat_STAR_$iter__20720__20724 iter__6373__auto__20738;
        Object object = with_children;
        with_children = null;
        Object object2 = children2;
        children2 = null;
        tree$lazy_precat_STAR_$iter__20720__20724 tree$lazy_precat_STAR_$iter__20720__20724 = iter__6373__auto__20738 = new tree$lazy_precat_STAR_$iter__20720__20724(object, object2, pre);
        iter__6373__auto__20738 = null;
        Object object3 = pre;
        pre = null;
        Object object4 = node2;
        node2 = null;
        return ((IFn)tree$lazy_precat_STAR_$iter__20720__20724).invoke(((IFn)object3).invoke(object4));
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
        return tree$lazy_precat_STAR_.invokeStatic(object5, object6, object7, object8);
    }
}

