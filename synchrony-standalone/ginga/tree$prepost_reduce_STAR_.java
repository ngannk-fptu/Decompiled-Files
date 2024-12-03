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

public final class tree$prepost_reduce_STAR_
extends AFunction {
    public static final Var const__0 = RT.var("ginga.tree", "prepost-reduce*");
    public static final Var const__1 = RT.var("clojure.core", "identity");
    public static final Var const__2 = RT.var("ginga.tree", "return-2");
    public static final Var const__3 = RT.var("clojure.core", "first");
    public static final Var const__4 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "cons");
    public static final Var const__6 = RT.var("clojure.core", "next");

    public static Object invokeStatic(Object children2, Object pre, Object pre_replace_first, Object enter, Object leave, Object init2, Object node2) {
        Object object = node2;
        node2 = null;
        Object nodes2 = Tuple.create(object);
        Object stack2 = null;
        Object object2 = init2;
        init2 = null;
        Object result = object2;
        while (true) {
            Object temp__5802__auto__20685;
            IPersistentVector iPersistentVector = nodes2;
            if (iPersistentVector != null && iPersistentVector != Boolean.FALSE) {
                Object node3 = ((IFn)pre).invoke(((IFn)const__3.getRawRoot()).invoke(nodes2));
                Object object3 = ((IFn)const__4.getRawRoot()).invoke(((IFn)children2).invoke(node3));
                IPersistentVector iPersistentVector2 = nodes2;
                nodes2 = null;
                Object object4 = stack2;
                stack2 = null;
                Object object5 = ((IFn)const__5.getRawRoot()).invoke(((IFn)pre_replace_first).invoke(node3, iPersistentVector2), object4);
                Object object6 = result;
                result = null;
                Object object7 = node3;
                node3 = null;
                result = ((IFn)enter).invoke(object6, object7);
                stack2 = object5;
                nodes2 = object3;
                continue;
            }
            Object object8 = temp__5802__auto__20685 = ((IFn)const__3.getRawRoot()).invoke(stack2);
            if (object8 == null || object8 == Boolean.FALSE) break;
            Object object9 = temp__5802__auto__20685;
            temp__5802__auto__20685 = null;
            Object parent_nodes = object9;
            Object object10 = ((IFn)const__6.getRawRoot()).invoke(parent_nodes);
            Object object11 = stack2;
            stack2 = null;
            Object object12 = result;
            result = null;
            Object object13 = parent_nodes;
            parent_nodes = null;
            result = ((IFn)leave).invoke(object12, ((IFn)const__3.getRawRoot()).invoke(object13));
            stack2 = ((IFn)const__6.getRawRoot()).invoke(object11);
            nodes2 = object10;
        }
        Object object14 = result;
        result = null;
        return object14;
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
        Object object8 = object;
        object = null;
        Object object9 = object2;
        object2 = null;
        Object object10 = object3;
        object3 = null;
        Object object11 = object4;
        object4 = null;
        Object object12 = object5;
        object5 = null;
        Object object13 = object6;
        object6 = null;
        Object object14 = object7;
        object7 = null;
        return tree$prepost_reduce_STAR_.invokeStatic(object8, object9, object10, object11, object12, object13, object14);
    }

    public static Object invokeStatic(Object children2, Object enter, Object leave, Object init2, Object node2) {
        Object object = children2;
        children2 = null;
        Object object2 = enter;
        enter = null;
        Object object3 = leave;
        leave = null;
        Object object4 = init2;
        init2 = null;
        Object object5 = node2;
        node2 = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, const__1.getRawRoot(), const__2.getRawRoot(), object2, object3, object4, object5);
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4, Object object5) {
        Object object6 = object;
        object = null;
        Object object7 = object2;
        object2 = null;
        Object object8 = object3;
        object3 = null;
        Object object9 = object4;
        object4 = null;
        Object object10 = object5;
        object5 = null;
        return tree$prepost_reduce_STAR_.invokeStatic(object6, object7, object8, object9, object10);
    }
}

