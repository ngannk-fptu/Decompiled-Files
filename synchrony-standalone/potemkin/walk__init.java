/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

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
import java.util.Arrays;
import potemkin.walk$fn__26056;
import potemkin.walk$loading__6789__auto____26054;
import potemkin.walk$postwalk;
import potemkin.walk$prewalk;
import potemkin.walk$walk;

public class walk__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__12;
    public static final Var const__13;
    public static final AFn const__16;
    public static final Var const__17;
    public static final AFn const__20;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new walk$loading__6789__auto____26054()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new walk$fn__26056());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__12));
        Var var2 = var;
        var.bindRoot(new walk$walk());
        Var var3 = const__13;
        var3.setMeta((IPersistentMap)((Object)const__16));
        Var var4 = var3;
        var3.bindRoot(new walk$postwalk());
        Var var5 = const__17;
        var5.setMeta((IPersistentMap)((Object)const__20));
        Var var6 = var5;
        var5.bindRoot(new walk$prewalk());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "potemkin.walk");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("potemkin.walk", "walk");
        const__12 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "inner"), Symbol.intern(null, "outer"), Symbol.intern(null, "form")))), RT.keyword(null, "doc"), "Like `clojure.walk/walk`, but preserves metadata.", RT.keyword(null, "line"), 5, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/walk.clj"));
        const__13 = RT.var("potemkin.walk", "postwalk");
        const__16 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "form")))), RT.keyword(null, "doc"), "Like `clojure.walk/postwalk`, but preserves metadata.", RT.keyword(null, "line"), 20, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/walk.clj"));
        const__17 = RT.var("potemkin.walk", "prewalk");
        const__20 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "form")))), RT.keyword(null, "doc"), "Like `clojure.walk/prewalk`, but preserves metadata.", RT.keyword(null, "line"), 25, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/walk.clj"));
    }

    static {
        walk__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("potemkin.walk__init").getClassLoader());
        try {
            walk__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

