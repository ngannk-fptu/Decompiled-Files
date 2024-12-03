/*
 * Decompiled with CFR 0.152.
 */
package riddley;

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
import riddley.walk$case_handler;
import riddley.walk$catch_handler;
import riddley.walk$def_handler;
import riddley.walk$deftype_handler;
import riddley.walk$do_handler;
import riddley.walk$dot_handler;
import riddley.walk$fn__14702;
import riddley.walk$fn_handler;
import riddley.walk$let_bindings;
import riddley.walk$let_handler;
import riddley.walk$loading__6789__auto____14676;
import riddley.walk$macroexpand;
import riddley.walk$macroexpand_all;
import riddley.walk$reify_handler;
import riddley.walk$walk_exprs;

public class walk__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__12;
    public static final Var const__13;
    public static final AFn const__17;
    public static final Var const__18;
    public static final AFn const__21;
    public static final Var const__22;
    public static final AFn const__25;
    public static final Var const__26;
    public static final AFn const__29;
    public static final Var const__30;
    public static final AFn const__33;
    public static final Var const__34;
    public static final AFn const__37;
    public static final Var const__38;
    public static final AFn const__41;
    public static final Var const__42;
    public static final AFn const__45;
    public static final Var const__46;
    public static final AFn const__49;
    public static final Var const__50;
    public static final AFn const__53;
    public static final Var const__54;
    public static final AFn const__57;
    public static final Var const__58;
    public static final AFn const__61;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new walk$loading__6789__auto____14676()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new walk$fn__14702());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__12));
        Var var2 = var;
        var.bindRoot(new walk$macroexpand());
        Var var3 = const__13;
        var3.setMeta((IPersistentMap)((Object)const__17));
        Var var4 = var3;
        var3.bindRoot(new walk$do_handler());
        Var var5 = const__18;
        var5.setMeta((IPersistentMap)((Object)const__21));
        Var var6 = var5;
        var5.bindRoot(new walk$fn_handler());
        Var var7 = const__22;
        var7.setMeta((IPersistentMap)((Object)const__25));
        Var var8 = var7;
        var7.bindRoot(new walk$def_handler());
        Var var9 = const__26;
        var9.setMeta((IPersistentMap)((Object)const__29));
        Var var10 = var9;
        var9.bindRoot(new walk$let_bindings());
        Var var11 = const__30;
        var11.setMeta((IPersistentMap)((Object)const__33));
        Var var12 = var11;
        var11.bindRoot(new walk$reify_handler());
        Var var13 = const__34;
        var13.setMeta((IPersistentMap)((Object)const__37));
        Var var14 = var13;
        var13.bindRoot(new walk$deftype_handler());
        Var var15 = const__38;
        var15.setMeta((IPersistentMap)((Object)const__41));
        Var var16 = var15;
        var15.bindRoot(new walk$let_handler());
        Var var17 = const__42;
        var17.setMeta((IPersistentMap)((Object)const__45));
        Var var18 = var17;
        var17.bindRoot(new walk$case_handler());
        Var var19 = const__46;
        var19.setMeta((IPersistentMap)((Object)const__49));
        Var var20 = var19;
        var19.bindRoot(new walk$catch_handler());
        Var var21 = const__50;
        var21.setMeta((IPersistentMap)((Object)const__53));
        Var var22 = var21;
        var21.bindRoot(new walk$dot_handler());
        Var var23 = const__54;
        var23.setMeta((IPersistentMap)((Object)const__57));
        Var var24 = var23;
        var23.bindRoot(new walk$walk_exprs());
        Var var25 = const__58;
        var25.setMeta((IPersistentMap)((Object)const__61));
        Var var26 = var25;
        var25.bindRoot(new walk$macroexpand_all());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "riddley.walk");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("riddley.walk", "macroexpand");
        const__12 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")), Tuple.create(Symbol.intern(null, "x"), Symbol.intern(null, "special-form?")))), RT.keyword(null, "doc"), "Expands both macros and inline functions. Optionally takes a `special-form?` predicate which\n   identifies first elements of expressions that shouldn't be macroexpanded, and honors local\n   bindings.", RT.keyword(null, "line"), 6, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__13 = RT.var("riddley.walk", "do-handler");
        const__17 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Tuple.create(Symbol.intern(null, "_"), Symbol.intern(null, "&"), Symbol.intern(null, "body"))))), RT.keyword(null, "line"), 58, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__18 = RT.var("riddley.walk", "fn-handler");
        const__21 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 63, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__22 = RT.var("riddley.walk", "def-handler");
        const__25 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 90, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__26 = RT.var("riddley.walk", "let-bindings");
        const__29 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 96, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__30 = RT.var("riddley.walk", "reify-handler");
        const__33 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 106, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__34 = RT.var("riddley.walk", "deftype-handler");
        const__37 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 118, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__38 = RT.var("riddley.walk", "let-handler");
        const__41 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 133, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__42 = RT.var("riddley.walk", "case-handler");
        const__45 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 141, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__46 = RT.var("riddley.walk", "catch-handler");
        const__49 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 159, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__50 = RT.var("riddley.walk", "dot-handler");
        const__53 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "x")))), RT.keyword(null, "line"), 167, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__54 = RT.var("riddley.walk", "walk-exprs");
        const__57 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "predicate"), Symbol.intern(null, "handler"), Symbol.intern(null, "x")), Tuple.create(Symbol.intern(null, "predicate"), Symbol.intern(null, "handler"), Symbol.intern(null, "special-form?"), Symbol.intern(null, "x")))), RT.keyword(null, "doc"), "A walk function which only traverses valid Clojure expressions.  The `predicate` describes\n   whether the sub-form should be transformed.  If it returns true, `handler` is invoked, and\n   returns a transformed form.\n\n   Unlike `clojure.walk`, if the handler is called, the rest of the sub-form is not walked.\n   The handler function is responsible for recursively calling `walk-exprs` on the form it is\n   given.\n\n   Macroexpansion can be halted by defining a set of `special-form?` which will be left alone.\n   Including `fn`, `let`, or other binding forms can break local variable analysis, so use\n   with caution.", RT.keyword(null, "line"), 177, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
        const__58 = RT.var("riddley.walk", "macroexpand-all");
        const__61 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "doc"), "Recursively macroexpands all forms, preserving the &env special variables.", RT.keyword(null, "line"), 253, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/walk.clj"));
    }

    static {
        walk__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("riddley.walk__init").getClassLoader());
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

