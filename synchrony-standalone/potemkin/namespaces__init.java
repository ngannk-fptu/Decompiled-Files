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
import potemkin.namespaces$fn__26030;
import potemkin.namespaces$import_def;
import potemkin.namespaces$import_fn;
import potemkin.namespaces$import_macro;
import potemkin.namespaces$import_vars;
import potemkin.namespaces$link_vars;
import potemkin.namespaces$loading__6789__auto____26028;

public class namespaces__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__12;
    public static final Var const__13;
    public static final AFn const__16;
    public static final Var const__17;
    public static final AFn const__20;
    public static final Var const__21;
    public static final AFn const__24;
    public static final Var const__25;
    public static final AFn const__28;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new namespaces$loading__6789__auto____26028()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new namespaces$fn__26030());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__12));
        Var var2 = var;
        var.bindRoot(new namespaces$link_vars());
        Var var3 = const__13;
        var3.setMeta((IPersistentMap)((Object)const__16));
        Var var4 = var3;
        var3.bindRoot(new namespaces$import_fn());
        const__13.setMacro();
        Object v7 = null;
        Var var5 = const__13;
        Var var6 = const__17;
        var6.setMeta((IPersistentMap)((Object)const__20));
        Var var7 = var6;
        var6.bindRoot(new namespaces$import_macro());
        const__17.setMacro();
        Object v11 = null;
        Var var8 = const__17;
        Var var9 = const__21;
        var9.setMeta((IPersistentMap)((Object)const__24));
        Var var10 = var9;
        var9.bindRoot(new namespaces$import_def());
        const__21.setMacro();
        Object v15 = null;
        Var var11 = const__21;
        Var var12 = const__25;
        var12.setMeta((IPersistentMap)((Object)const__28));
        Var var13 = var12;
        var12.bindRoot(new namespaces$import_vars());
        const__25.setMacro();
        Object v19 = null;
        Var var14 = const__25;
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "potemkin.namespaces");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("potemkin.namespaces", "link-vars");
        const__12 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "src"), Symbol.intern(null, "dst")))), RT.keyword(null, "doc"), "Makes sure that all changes to `src` are reflected in `dst`.", RT.keyword(null, "line"), 3, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/namespaces.clj"));
        const__13 = RT.var("potemkin.namespaces", "import-fn");
        const__16 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "sym")), Tuple.create(Symbol.intern(null, "sym"), Symbol.intern(null, "name")))), RT.keyword(null, "doc"), "Given a function in another namespace, defines a function with the\n   same name in the current namespace.  Argument lists, doc-strings,\n   and original line-numbers are preserved.", RT.keyword(null, "line"), 11, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/namespaces.clj"));
        const__17 = RT.var("potemkin.namespaces", "import-macro");
        const__20 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "sym")), Tuple.create(Symbol.intern(null, "sym"), Symbol.intern(null, "name")))), RT.keyword(null, "doc"), "Given a macro in another namespace, defines a macro with the same\n   name in the current namespace.  Argument lists, doc-strings, and\n   original line-numbers are preserved.", RT.keyword(null, "line"), 35, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/namespaces.clj"));
        const__21 = RT.var("potemkin.namespaces", "import-def");
        const__24 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "sym")), Tuple.create(Symbol.intern(null, "sym"), Symbol.intern(null, "name")))), RT.keyword(null, "doc"), "Given a regular def'd var from another namespace, defined a new var with the\n   same name in the current namespace.", RT.keyword(null, "line"), 58, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/namespaces.clj"));
        const__25 = RT.var("potemkin.namespaces", "import-vars");
        const__28 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "&"), Symbol.intern(null, "syms")))), RT.keyword(null, "doc"), "Imports a list of vars from other namespaces.", RT.keyword(null, "line"), 77, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/namespaces.clj"));
    }

    static {
        namespaces__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("potemkin.namespaces__init").getClassLoader());
        try {
            namespaces__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

