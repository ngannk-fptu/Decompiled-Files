/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class collections$loading__6789__auto____26218
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Var const__2 = RT.var("clojure.core", "use");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "potemkin"), Symbol.intern(null, "types"), Symbol.intern(null, "macros"), Symbol.intern(null, "utils")));

    @Override
    public Object invoke() {
        Object object;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1);
            object = ((IFn)const__2.getRawRoot()).invoke(const__3);
        }
        finally {
            Var.popThreadBindings();
        }
        return object;
    }
}

