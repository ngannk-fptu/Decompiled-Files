/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFn;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.IObj;
import clojure.lang.IPersistentMap;
import clojure.lang.LockingTransaction;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import ginga.chash$add_node;
import ginga.chash$b64_hash;
import ginga.chash$find_replica;
import ginga.chash$fn__14641;
import ginga.chash$get_node;
import ginga.chash$loading__6789__auto____14602;
import ginga.chash$remove_node;
import ginga.chash$replica_ring;
import ginga.chash$replication_keys;
import java.util.Arrays;

public class chash__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__9;
    public static final Var const__10;
    public static final Var const__11;
    public static final AFn const__15;
    public static final Var const__16;
    public static final AFn const__19;
    public static final Var const__20;
    public static final AFn const__23;
    public static final Var const__24;
    public static final AFn const__27;
    public static final Var const__28;
    public static final AFn const__31;
    public static final Var const__32;
    public static final AFn const__35;
    public static final Var const__36;
    public static final AFn const__39;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new chash$loading__6789__auto____14602()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new chash$fn__14641());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__9));
        Var var2 = var;
        var.bindRoot(const__10.getRawRoot());
        Var var3 = const__11;
        var3.setMeta((IPersistentMap)((Object)const__15));
        Var var4 = var3;
        var3.bindRoot(new chash$b64_hash());
        Var var5 = const__16;
        var5.setMeta((IPersistentMap)((Object)const__19));
        Var var6 = var5;
        var5.bindRoot(new chash$replication_keys());
        Var var7 = const__20;
        var7.setMeta((IPersistentMap)((Object)const__23));
        Var var8 = var7;
        var7.bindRoot(new chash$find_replica());
        Var var9 = const__24;
        var9.setMeta((IPersistentMap)((Object)const__27));
        Var var10 = var9;
        var9.bindRoot(new chash$replica_ring());
        Var var11 = const__28;
        var11.setMeta((IPersistentMap)((Object)const__31));
        Var var12 = var11;
        var11.bindRoot(new chash$add_node());
        Var var13 = const__32;
        var13.setMeta((IPersistentMap)((Object)const__35));
        Var var14 = var13;
        var13.bindRoot(new chash$remove_node());
        Var var15 = const__36;
        var15.setMeta((IPersistentMap)((Object)const__39));
        Var var16 = var15;
        var15.bindRoot(new chash$get_node());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "ginga.chash");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("ginga.chash", "ring");
        const__9 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 7, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
        const__10 = RT.var("clojure.core", "sorted-map");
        const__11 = RT.var("ginga.chash", "b64-hash");
        const__15 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(((IObj)Symbol.intern(null, "key")).withMeta(RT.map(RT.keyword(null, "tag"), Symbol.intern(null, "String")))))), RT.keyword(null, "line"), 9, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
        const__16 = RT.var("ginga.chash", "replication-keys");
        const__19 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "n"), ((IObj)Symbol.intern(null, "key")).withMeta(RT.map(RT.keyword(null, "tag"), Symbol.intern(null, "String")))))), RT.keyword(null, "line"), 17, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
        const__20 = RT.var("ginga.chash", "find-replica");
        const__23 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "ring"), Symbol.intern(null, "hash")))), RT.keyword(null, "line"), 21, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
        const__24 = RT.var("ginga.chash", "replica-ring");
        const__27 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "replication-factor")))), RT.keyword(null, "line"), 25, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
        const__28 = RT.var("ginga.chash", "add-node");
        const__31 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "rring"), Symbol.intern(null, "key"), Symbol.intern(null, "node")))), RT.keyword(null, "line"), 29, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
        const__32 = RT.var("ginga.chash", "remove-node");
        const__35 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "rring"), Symbol.intern(null, "key")))), RT.keyword(null, "line"), 33, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
        const__36 = RT.var("ginga.chash", "get-node");
        const__39 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "rring"), Symbol.intern(null, "topic")))), RT.keyword(null, "line"), 36, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/chash.clj"));
    }

    static {
        chash__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("ginga.chash__init").getClassLoader());
        try {
            chash__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

