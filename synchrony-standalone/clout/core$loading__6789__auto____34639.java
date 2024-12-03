/*
 * Decompiled with CFR 0.152.
 */
package clout;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class core$loading__6789__auto____34639
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Var const__2 = RT.var("clojure.core", "require");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clojure.string"), RT.keyword(null, "as"), Symbol.intern(null, "string")));
    public static final AFn const__4 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clojure.set"), RT.keyword(null, "as"), Symbol.intern(null, "set")));
    public static final AFn const__5 = (AFn)((Object)Tuple.create(Symbol.intern(null, "instaparse.core"), RT.keyword(null, "as"), Symbol.intern(null, "insta")));

    @Override
    public Object invoke() {
        Object object;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1);
            object = ((IFn)const__2.getRawRoot()).invoke(const__3, const__4, const__5);
        }
        finally {
            Var.popThreadBindings();
        }
        return object;
    }
}

