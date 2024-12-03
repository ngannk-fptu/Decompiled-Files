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
import riddley.compiler$fn__14680;
import riddley.compiler$fn__14686;
import riddley.compiler$loading__6789__auto____14678;
import riddley.compiler$locals;
import riddley.compiler$register_arg;
import riddley.compiler$register_local;
import riddley.compiler$stub_method;
import riddley.compiler$tag_of;
import riddley.compiler$with_base_env;
import riddley.compiler$with_lexical_scoping;
import riddley.compiler$with_stub_vars;

public class compiler__init {
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

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new compiler$loading__6789__auto____14678()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new compiler$fn__14680());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__12));
        Var var2 = var;
        var.bindRoot(new compiler$stub_method());
        Var var3 = const__13;
        var3.setMeta((IPersistentMap)((Object)const__17));
        Var var4 = var3;
        var3.bindRoot(new compiler$tag_of());
        Object object3 = ((IFn)new compiler$fn__14686()).invoke();
        Var var5 = const__18;
        var5.setMeta((IPersistentMap)((Object)const__21));
        Var var6 = var5;
        var5.bindRoot(new compiler$locals());
        Var var7 = const__22;
        var7.setMeta((IPersistentMap)((Object)const__25));
        Var var8 = var7;
        var7.bindRoot(new compiler$with_base_env());
        const__22.setMacro();
        Object v12 = null;
        Var var9 = const__22;
        Var var10 = const__26;
        var10.setMeta((IPersistentMap)((Object)const__29));
        Var var11 = var10;
        var10.bindRoot(new compiler$with_lexical_scoping());
        const__26.setMacro();
        Object v16 = null;
        Var var12 = const__26;
        Var var13 = const__30;
        var13.setMeta((IPersistentMap)((Object)const__33));
        Var var14 = var13;
        var13.bindRoot(new compiler$with_stub_vars());
        const__30.setMacro();
        Object v20 = null;
        Var var15 = const__30;
        Var var16 = const__34;
        var16.setMeta((IPersistentMap)((Object)const__37));
        Var var17 = var16;
        var16.bindRoot(new compiler$register_local());
        Var var18 = const__38;
        var18.setMeta((IPersistentMap)((Object)const__41));
        Var var19 = var18;
        var18.bindRoot(new compiler$register_arg());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "riddley.compiler");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("riddley.compiler", "stub-method");
        const__12 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create())), RT.keyword(null, "line"), 11, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
        const__13 = RT.var("riddley.compiler", "tag-of");
        const__17 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "doc"), "Returns a symbol representing the tagged class of the symbol, or `nil` if none exists.", RT.keyword(null, "line"), 14, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
        const__18 = RT.var("riddley.compiler", "locals");
        const__21 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create())), RT.keyword(null, "doc"), "Returns the local binding map, equivalent to the value of `&env`.", RT.keyword(null, "line"), 29, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
        const__22 = RT.var("riddley.compiler", "with-base-env");
        const__25 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "&"), Symbol.intern(null, "body")))), RT.keyword(null, "line"), 35, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
        const__26 = RT.var("riddley.compiler", "with-lexical-scoping");
        const__29 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "&"), Symbol.intern(null, "body")))), RT.keyword(null, "doc"), "Defines a lexical scope where new locals may be registered.", RT.keyword(null, "line"), 42, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
        const__30 = RT.var("riddley.compiler", "with-stub-vars");
        const__33 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "&"), Symbol.intern(null, "body")))), RT.keyword(null, "line"), 48, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
        const__34 = RT.var("riddley.compiler", "register-local");
        const__37 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "v"), Symbol.intern(null, "x")))), RT.keyword(null, "doc"), "Registers a locally bound variable `v`, which is being set to form `x`.", RT.keyword(null, "line"), 55, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
        const__38 = RT.var("riddley.compiler", "register-arg");
        const__41 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "doc"), "Registers a function argument `x`.", RT.keyword(null, "line"), 69, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "riddley/compiler.clj"));
    }

    static {
        compiler__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("riddley.compiler__init").getClassLoader());
        try {
            compiler__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

