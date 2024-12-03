/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public final class types$extend_protocol_PLUS_$this__26103$fn__26107
extends AFunction {
    Object rest;
    Object this;
    Object sym;
    public static final Var const__0 = RT.var("clojure.core", "cons");
    public static final Var const__1 = RT.var("clojure.core", "take-while");
    public static final Var const__2 = RT.var("clojure.core", "complement");
    public static final Var const__3 = RT.var("clojure.core", "symbol?");
    public static final Var const__4 = RT.var("clojure.core", "drop-while");

    public types$extend_protocol_PLUS_$this__26103$fn__26107(Object object, Object object2, Object object3) {
        this.rest = object;
        this.this = object2;
        this.sym = object3;
    }

    @Override
    public Object invoke() {
        types$extend_protocol_PLUS_$this__26103$fn__26107 this_ = null;
        return ((IFn)const__0.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this_.sym, ((IFn)const__1.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot()), this_.rest)), ((IFn)this_.this).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__2.getRawRoot()).invoke(const__3.getRawRoot()), this_.rest)));
    }
}

