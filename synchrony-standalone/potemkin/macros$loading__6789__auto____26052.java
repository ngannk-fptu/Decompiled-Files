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

public final class macros$loading__6789__auto____26052
extends AFunction {
    public static final Var const__0 = RT.var("clojure.core", "refer");
    public static final AFn const__1 = Symbol.intern(null, "clojure.core");
    public static final Var const__2 = RT.var("clojure.core", "require");
    public static final AFn const__3 = (AFn)((Object)Tuple.create(Symbol.intern(null, "potemkin.walk"), RT.keyword(null, "refer"), ((IObj)((Object)PersistentList.create(Arrays.asList(Symbol.intern(null, "postwalk"))))).withMeta(RT.map(RT.keyword(null, "line"), 3, RT.keyword(null, "column"), 27))));
    public static final AFn const__4 = (AFn)((Object)Tuple.create(Symbol.intern(null, "riddley.walk"), RT.keyword(null, "as"), Symbol.intern(null, "r")));

    @Override
    public Object invoke() {
        Object object;
        Var.pushThreadBindings(RT.mapUniqueKeys(Compiler.LOADER, this.getClass().getClassLoader()));
        try {
            ((IFn)const__0.getRawRoot()).invoke(const__1);
            object = ((IFn)const__2.getRawRoot()).invoke(const__3, const__4);
        }
        finally {
            Var.popThreadBindings();
        }
        return object;
    }
}

