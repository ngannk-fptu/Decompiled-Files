/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import java.util.Arrays;

public final class types$loading__6789__auto____26050
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Var const__2 = RT.var("clojure.core", "use");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clojure"), Tuple.create(Symbol.intern(null, "set"), RT.keyword(null, "only"), ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "union"))))).withMeta(RT.map(RT.keyword(null, "line"), 3, RT.keyword(null, "column"), 25)))));
    public static final AFn const__4 = (AFn)((Object)Tuple.create(Symbol.intern(null, "potemkin.macros"), RT.keyword(null, "only"), ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "equivalent?"), Symbol.intern(null, "normalize-gensyms"), Symbol.intern(null, "safe-resolve"), Symbol.intern(null, "unify-gensyms"))))).withMeta(RT.map(RT.keyword(null, "line"), 4, RT.keyword(null, "column"), 28))));
    public static final Var const__5 = RT.var("clojure.core", "require");
    public static final AFn const__6 = (AFn)((Object)Tuple.create(Symbol.intern(null, "riddley.walk"), RT.keyword(null, "as"), Symbol.intern(null, "r")));
    public static final AFn const__7 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clojure.set"), RT.keyword(null, "as"), Symbol.intern(null, "set")));
    public static final AFn const__8 = (AFn)((Object)Tuple.create(Symbol.intern(null, "clojure.string"), RT.keyword(null, "as"), Symbol.intern(null, "str")));

    @Override
    public Object invoke() {
        Object object;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1);
            ((IFn)const__2.getRawRoot()).invoke(const__3, const__4);
            object = ((IFn)const__5.getRawRoot()).invoke(const__6, const__7, const__8);
        }
        finally {
            Var.popThreadBindings();
        }
        return object;
    }
}

