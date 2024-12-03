/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Var;
import potemkin.types$expand_deftype$fn__26144;

public final class types$expand_deftype
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "set");
    public static final Var const__1 = RT.var("clojure.core", "remove");
    public static final Var const__2 = RT.var("potemkin.types", "*expanded-types*");
    public static final Var const__3 = RT.var("clojure.core", "map");
    public static final Var const__4 = RT.var("clojure.core", "resolve");
    public static final Var const__5 = RT.var("clojure.core", "filter");
    public static final Var const__6 = RT.var("potemkin.types", "abstract-type?");
    public static final Var const__7 = RT.var("clojure.core", "push-thread-bindings");
    public static final Var const__8 = RT.var("clojure.core", "hash-map");
    public static final Var const__9 = RT.var("clojure.set", "union");
    public static final Var const__10 = RT.var("clojure.core", "apply");
    public static final Var const__11 = RT.var("potemkin.types", "merge-deftypes*");
    public static final Var const__12 = RT.var("clojure.core", "concat");
    public static final Var const__13 = RT.var("potemkin.types", "deftype->deftype*");
    public static final Var const__14 = RT.var("clojure.core", "second");

    public static Object invokeStatic(Object x) {
        Object object;
        Object abstract_types = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2.get(), ((IFn)const__3.getRawRoot()).invoke(const__4.getRawRoot(), ((IFn)const__5.getRawRoot()).invoke(const__6.getRawRoot(), x))));
        ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__2, ((IFn)const__9.getRawRoot()).invoke(const__2.get(), abstract_types)));
        Object object2 = abstract_types;
        abstract_types = null;
        Object abstract_type_bodies = ((IFn)new types$expand_deftype$fn__26144(object2)).invoke();
        IFn iFn = (IFn)const__10.getRawRoot();
        Object object3 = const__11.getRawRoot();
        IFn iFn2 = (IFn)const__12.getRawRoot();
        Object object4 = abstract_type_bodies;
        abstract_type_bodies = null;
        IFn iFn3 = (IFn)const__13.getRawRoot();
        Object object5 = ((IFn)const__6.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(x));
        if (object5 != null && object5 != Boolean.FALSE) {
            object = x;
            x = null;
        } else {
            Object object6 = x;
            x = null;
            object = ((IFn)const__1.getRawRoot()).invoke(const__6.getRawRoot(), object6);
        }
        return iFn.invoke(object3, iFn2.invoke(object4, Tuple.create(iFn3.invoke(object))));
    }

    @Override
    public Object invoke(Object object) {
        Object object2 = object;
        object = null;
        return types$expand_deftype.invokeStatic(object2);
    }
}

