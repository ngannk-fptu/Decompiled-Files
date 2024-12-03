/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import riddley.Util;

public final class compiler$register_local$fn__14694$fn__14695
extends AFunction {
    Object v;
    Object x;
    public static final Var const__0 = RT.var("riddley.compiler", "local-id");
    public static final Var const__1 = RT.var("riddley.compiler", "tag-of");
    public static final Keyword const__2 = RT.keyword("riddley.compiler", "analyze-failure");

    public compiler$register_local$fn__14694$fn__14695(Object object, Object object2) {
        this.v = object;
        this.x = object2;
    }

    @Override
    public Object invoke() {
        Object object;
        try {
            this.v = null;
            this.x = null;
            object = Util.localBinding(RT.intCast((Number)((IFn)const__0.getRawRoot()).invoke()), (Symbol)this.v, (Symbol)((IFn)const__1.getRawRoot()).invoke(this.v), this.x);
        }
        catch (Exception _2) {
            object = const__2;
        }
        return object;
    }
}

