/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$expand_deftype$fn__26144
extends AFunction {
    Object abstract_types;
    public static final Var const__0 = RT.var("clojure.core", "doall");
    public static final Var const__1 = RT.var("clojure.core", "map");
    public static final Var const__2 = RT.var("potemkin.types", "deftype->deftype*");
    public static final Var const__3 = RT.var("potemkin.types", "expand-deftype");
    public static final Var const__4 = RT.var("potemkin.types", "clean-deftype");
    public static final Var const__5 = RT.var("clojure.core", "deref");
    public static final Var const__6 = RT.var("clojure.core", "pop-thread-bindings");

    public types$expand_deftype$fn__26144(Object object) {
        this.abstract_types = object;
    }

    @Override
    public Object invoke() {
        Object object;
        try {
            this.abstract_types = null;
            object = ((IFn)const__0.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(const__2.getRawRoot(), ((IFn)const__1.getRawRoot()).invoke(const__3.getRawRoot(), ((IFn)const__1.getRawRoot()).invoke(const__4.getRawRoot(), ((IFn)const__1.getRawRoot()).invoke(const__5.getRawRoot(), this.abstract_types)))));
        }
        finally {
            ((IFn)const__6.getRawRoot()).invoke();
        }
        return object;
    }
}

