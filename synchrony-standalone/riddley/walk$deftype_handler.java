/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import riddley.walk$deftype_handler$fn__14774;

public final class walk$deftype_handler
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "seq");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "with-bindings*");
    public static final Var const__4 = RT.var("riddley.compiler", "locals");

    public static Object invokeStatic(Object f, Object x) {
        Object vec__14771;
        Object object = x;
        x = null;
        Object object2 = vec__14771 = object;
        vec__14771 = null;
        Object seq__14772 = ((IFn)const__0.getRawRoot()).invoke(object2);
        Object first__14773 = ((IFn)const__1.getRawRoot()).invoke(seq__14772);
        Object object3 = seq__14772;
        seq__14772 = null;
        Object seq__147722 = ((IFn)const__2.getRawRoot()).invoke(object3);
        first__14773 = null;
        Object first__147732 = ((IFn)const__1.getRawRoot()).invoke(seq__147722);
        Object object4 = seq__147722;
        seq__147722 = null;
        Object seq__147723 = ((IFn)const__2.getRawRoot()).invoke(object4);
        Object object5 = first__147732;
        first__147732 = null;
        Object type2 = object5;
        Object first__147733 = ((IFn)const__1.getRawRoot()).invoke(seq__147723);
        Object object6 = seq__147723;
        seq__147723 = null;
        Object seq__147724 = ((IFn)const__2.getRawRoot()).invoke(object6);
        Object object7 = first__147733;
        first__147733 = null;
        Object resolved_type = object7;
        Object first__147734 = ((IFn)const__1.getRawRoot()).invoke(seq__147724);
        Object object8 = seq__147724;
        seq__147724 = null;
        Object seq__147725 = ((IFn)const__2.getRawRoot()).invoke(object8);
        Object object9 = first__147734;
        first__147734 = null;
        Object args = object9;
        Object first__147735 = ((IFn)const__1.getRawRoot()).invoke(seq__147725);
        Object object10 = seq__147725;
        seq__147725 = null;
        Object seq__147726 = ((IFn)const__2.getRawRoot()).invoke(object10);
        first__147735 = null;
        Object first__147736 = ((IFn)const__1.getRawRoot()).invoke(seq__147726);
        Object object11 = seq__147726;
        seq__147726 = null;
        Object seq__147727 = ((IFn)const__2.getRawRoot()).invoke(object11);
        Object object12 = first__147736;
        first__147736 = null;
        Object interfaces = object12;
        Object object13 = seq__147727;
        seq__147727 = null;
        Object fns = object13;
        Object object14 = resolved_type;
        resolved_type = null;
        Object object15 = type2;
        type2 = null;
        Object object16 = fns;
        fns = null;
        Object object17 = args;
        args = null;
        Object object18 = interfaces;
        interfaces = null;
        Object object19 = f;
        f = null;
        return ((IFn)const__3.getRawRoot()).invoke(RT.mapUniqueKeys(Compiler.LOCAL_ENV, ((IFn)const__4.getRawRoot()).invoke()), new walk$deftype_handler$fn__14774(object14, object15, object16, object17, object18, object19));
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return walk$deftype_handler.invokeStatic(object3, object4);
    }
}

