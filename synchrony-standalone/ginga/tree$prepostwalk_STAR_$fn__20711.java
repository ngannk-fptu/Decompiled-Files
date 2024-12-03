/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class tree$prepostwalk_STAR_$fn__20711
extends AFunction {
    Object post;
    Object with_children;
    public static final Var const__0 = RT.var("clojure.core", "persistent!");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "cons");
    public static final Var const__4 = RT.var("clojure.core", "conj!");

    public tree$prepostwalk_STAR_$fn__20711(Object object, Object object2) {
        this.post = object;
        this.with_children = object2;
    }

    @Override
    public Object invoke(Object stack2, Object node2) {
        Object object = node2;
        node2 = null;
        Object node_SINGLEQUOTE_ = ((IFn)this_.post).invoke(((IFn)this_.with_children).invoke(object, ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(stack2))));
        Object object2 = stack2;
        stack2 = null;
        Object up_stack = ((IFn)const__2.getRawRoot()).invoke(object2);
        Object object3 = node_SINGLEQUOTE_;
        node_SINGLEQUOTE_ = null;
        Object object4 = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(up_stack), object3);
        Object object5 = up_stack;
        up_stack = null;
        tree$prepostwalk_STAR_$fn__20711 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object4, ((IFn)const__2.getRawRoot()).invoke(object5));
    }
}

