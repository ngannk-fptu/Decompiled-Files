/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.LockingTransaction;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import ginga.hash$fn__14606;
import ginga.hash$hash_bytes;
import ginga.hash$loading__6789__auto____14604;
import java.util.Arrays;

public class hash__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__11;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new hash$loading__6789__auto____14604()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new hash$fn__14606());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__11));
        Var var2 = var;
        var.bindRoot(new hash$hash_bytes());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "ginga.hash");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("ginga.hash", "hash-bytes");
        const__11 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "bytes")))), RT.keyword(null, "line"), 3, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/hash.clj"));
    }

    static {
        hash__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("ginga.hash__init").getClassLoader());
        try {
            hash__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

