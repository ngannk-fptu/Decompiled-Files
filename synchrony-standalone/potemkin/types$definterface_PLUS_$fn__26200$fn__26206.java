/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

public final class types$definterface_PLUS_$fn__26200$fn__26206
extends AFunction {
    Object f;
    public static final Var const__0 = RT.var("clojure.core", "list");
    public static final Var const__1 = RT.var("potemkin.types", "resolve-tag");
    public static final Var const__2 = RT.var("clojure.core", "apply");
    public static final Var const__3 = RT.var("clojure.core", "map");
    public static final Var const__4 = RT.var("potemkin.types", "untag");

    public types$definterface_PLUS_$fn__26200$fn__26206(Object object) {
        this.f = object;
    }

    @Override
    public Object invoke(Object p1__26187_SHARP_) {
        Object object = ((IFn)const__1.getRawRoot()).invoke(p1__26187_SHARP_);
        Object object2 = p1__26187_SHARP_;
        p1__26187_SHARP_ = null;
        types$definterface_PLUS_$fn__26200$fn__26206 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(object, ((IFn)const__2.getRawRoot()).invoke(this_.f, ((IFn)const__3.getRawRoot()).invoke(const__4.getRawRoot(), object2)));
    }
}

