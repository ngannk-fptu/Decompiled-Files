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
import java.util.regex.Pattern;
import potemkin.macros$equivalent_QMARK_;
import potemkin.macros$fn__26063;
import potemkin.macros$gensym_QMARK_;
import potemkin.macros$loading__6789__auto____26052;
import potemkin.macros$normalize_gensyms;
import potemkin.macros$safe_resolve;
import potemkin.macros$un_gensym;
import potemkin.macros$unified_gensym_QMARK_;
import potemkin.macros$unify_gensyms;

public class macros__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__11;
    public static final Var const__12;
    public static final AFn const__14;
    public static final Object const__15;
    public static final Var const__16;
    public static final AFn const__18;
    public static final Object const__19;
    public static final Var const__20;
    public static final AFn const__23;
    public static final Var const__24;
    public static final AFn const__27;
    public static final Var const__28;
    public static final AFn const__31;
    public static final Var const__32;
    public static final AFn const__36;
    public static final Var const__37;
    public static final AFn const__40;
    public static final Var const__41;
    public static final AFn const__44;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new macros$loading__6789__auto____26052()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new macros$fn__26063());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__11));
        Var var2 = var;
        var.bindRoot(new macros$safe_resolve());
        Var var3 = const__12;
        var3.setMeta((IPersistentMap)((Object)const__14));
        Var var4 = var3;
        var3.bindRoot(const__15);
        Var var5 = const__16;
        var5.setMeta((IPersistentMap)((Object)const__18));
        Var var6 = var5;
        var5.bindRoot(const__19);
        Var var7 = const__20;
        var7.setMeta((IPersistentMap)((Object)const__23));
        Var var8 = var7;
        var7.bindRoot(new macros$unified_gensym_QMARK_());
        Var var9 = const__24;
        var9.setMeta((IPersistentMap)((Object)const__27));
        Var var10 = var9;
        var9.bindRoot(new macros$gensym_QMARK_());
        Var var11 = const__28;
        var11.setMeta((IPersistentMap)((Object)const__31));
        Var var12 = var11;
        var11.bindRoot(new macros$un_gensym());
        Var var13 = const__32;
        var13.setMeta((IPersistentMap)((Object)const__36));
        Var var14 = var13;
        var13.bindRoot(new macros$unify_gensyms());
        Var var15 = const__37;
        var15.setMeta((IPersistentMap)((Object)const__40));
        Var var16 = var15;
        var15.bindRoot(new macros$normalize_gensyms());
        Var var17 = const__41;
        var17.setMeta((IPersistentMap)((Object)const__44));
        Var var18 = var17;
        var17.bindRoot(new macros$equivalent_QMARK_());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "potemkin.macros");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("potemkin.macros", "safe-resolve");
        const__11 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "line"), 6, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__12 = RT.var("potemkin.macros", "unified-gensym-regex");
        const__14 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 12, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__15 = Pattern.compile("([a-zA-Z0-9\\-\\'\\*]+)#__\\d+__auto__$");
        const__16 = RT.var("potemkin.macros", "gensym-regex");
        const__18 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 14, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__19 = Pattern.compile("(_|[a-zA-Z0-9\\-\\'\\*]+)#?_+(\\d+_*#?)+(auto__)?$");
        const__20 = RT.var("potemkin.macros", "unified-gensym?");
        const__23 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "s")))), RT.keyword(null, "line"), 16, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__24 = RT.var("potemkin.macros", "gensym?");
        const__27 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "s")))), RT.keyword(null, "line"), 21, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__28 = RT.var("potemkin.macros", "un-gensym");
        const__31 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "s")))), RT.keyword(null, "line"), 26, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__32 = RT.var("potemkin.macros", "unify-gensyms");
        const__36 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "body")))), RT.keyword(null, "doc"), "All gensyms defined using two hash symbols are unified to the same\n   value, even if they were defined within different syntax-quote scopes.", RT.keyword(null, "line"), 29, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__37 = RT.var("potemkin.macros", "normalize-gensyms");
        const__40 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "body")))), RT.keyword(null, "line"), 40, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
        const__41 = RT.var("potemkin.macros", "equivalent?");
        const__44 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "a"), Symbol.intern(null, "b")))), RT.keyword(null, "line"), 50, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/macros.clj"));
    }

    static {
        macros__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("potemkin.macros__init").getClassLoader());
        try {
            macros__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

