/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.Namespace;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;

public final class utils$loading__6789__auto____26220
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Var const__2 = RT.var("clojure.core", "require");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "potemkin.macros"), RT.keyword(null, "refer"), Tuple.create(Symbol.intern(null, "unify-gensyms"))));
    public static final AFn const__4 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clj-tuple"), RT.keyword(null, "as"), Symbol.intern(null, "t")));

    @Override
    public Object invoke() {
        Class clazz;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1);
            ((IFn)const__2.getRawRoot()).invoke(const__3, const__4);
            clazz = ((Namespace)RT.CURRENT_NS.deref()).importClass(RT.classForNameNonLoading("java.util.concurrent.ConcurrentHashMap"));
        }
        finally {
            Var.popThreadBindings();
        }
        return clazz;
    }
}

