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
import ginga.debug$debug;
import ginga.debug$fn__19303;
import ginga.debug$loading__6789__auto____19301;
import java.util.Arrays;

public class debug__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__10;
    public static final Var const__11;
    public static final AFn const__13;
    public static final Var const__14;
    public static final AFn const__18;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new debug$loading__6789__auto____19301()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new debug$fn__19303());
            v2 = null;
        }
        Var var = const__3.setDynamic(true);
        var.setMeta((IPersistentMap)((Object)const__10));
        Var var2 = var;
        var.bindRoot(null);
        Var var3 = const__11.setDynamic(true);
        var3.setMeta((IPersistentMap)((Object)const__13));
        Var var4 = var3;
        var3.bindRoot(Boolean.FALSE);
        Var var5 = const__14;
        var5.setMeta((IPersistentMap)((Object)const__18));
        Var var6 = var5;
        var5.bindRoot(new debug$debug());
        const__14.setMacro();
        Object v9 = null;
        Var var7 = const__14;
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "ginga.debug");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("ginga.debug", "*out*");
        const__10 = (AFn)((Object)RT.map(RT.keyword(null, "dynamic"), Boolean.TRUE, RT.keyword(null, "line"), 5, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/debug.clj"));
        const__11 = RT.var("ginga.debug", "*prn*");
        const__13 = (AFn)((Object)RT.map(RT.keyword(null, "dynamic"), Boolean.TRUE, RT.keyword(null, "line"), 6, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/debug.clj"));
        const__14 = RT.var("ginga.debug", "debug");
        const__18 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "line"), 8, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/debug.clj"));
    }

    static {
        debug__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("ginga.debug__init").getClassLoader());
        try {
            debug__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

