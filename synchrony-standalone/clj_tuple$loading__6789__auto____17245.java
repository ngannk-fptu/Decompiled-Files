/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class clj_tuple$loading__6789__auto____17245
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Keyword const__2 = RT.keyword(null, "exclude");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "vector"), Symbol.intern(null, "hash-map")));

    @Override
    public Object invoke() {
        Object object;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("clojure.lang.PersistentUnrolledVector"));
            ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("clojure.lang.PersistentUnrolledMap"));
            object = ((IFn)const__0.getRawRoot()).invoke(const__1, const__2, const__3);
        }
        finally {
            Var.popThreadBindings();
        }
        return object;
    }
}

