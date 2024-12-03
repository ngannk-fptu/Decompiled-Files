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
import ginga.stack$concat;
import ginga.stack$edit;
import ginga.stack$fn__20672;
import ginga.stack$loading__6789__auto____20670;
import ginga.stack$push;
import ginga.stack$replace;
import java.util.Arrays;

public class stack__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__9;
    public static final Var const__10;
    public static final AFn const__12;
    public static final Var const__13;
    public static final Var const__14;
    public static final AFn const__16;
    public static final Var const__17;
    public static final Var const__18;
    public static final AFn const__20;
    public static final Var const__21;
    public static final Var const__22;
    public static final AFn const__24;
    public static final Var const__25;
    public static final Var const__26;
    public static final AFn const__28;
    public static final Var const__29;
    public static final Var const__30;
    public static final AFn const__34;
    public static final Var const__35;
    public static final AFn const__38;
    public static final Var const__39;
    public static final AFn const__42;
    public static final Var const__43;
    public static final AFn const__46;
    public static final Var const__47;
    public static final AFn const__49;
    public static final Var const__50;
    public static final Var const__51;
    public static final AFn const__53;
    public static final Var const__54;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new stack$loading__6789__auto____20670()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new stack$fn__20672());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__9));
        Var var2 = var;
        var.bindRoot(null);
        Var var3 = const__10;
        var3.setMeta((IPersistentMap)((Object)const__12));
        Var var4 = var3;
        var3.bindRoot(const__13.getRawRoot());
        Var var5 = const__14;
        var5.setMeta((IPersistentMap)((Object)const__16));
        Var var6 = var5;
        var5.bindRoot(const__17.getRawRoot());
        Var var7 = const__18;
        var7.setMeta((IPersistentMap)((Object)const__20));
        Var var8 = var7;
        var7.bindRoot(const__21.getRawRoot());
        Var var9 = const__22;
        var9.setMeta((IPersistentMap)((Object)const__24));
        Var var10 = var9;
        var9.bindRoot(const__25.getRawRoot());
        Var var11 = const__26;
        var11.setMeta((IPersistentMap)((Object)const__28));
        Var var12 = var11;
        var11.bindRoot(const__29.getRawRoot());
        Var var13 = const__30;
        var13.setMeta((IPersistentMap)((Object)const__34));
        Var var14 = var13;
        var13.bindRoot(new stack$concat());
        Var var15 = const__35;
        var15.setMeta((IPersistentMap)((Object)const__38));
        Var var16 = var15;
        var15.bindRoot(new stack$push());
        Var var17 = const__39;
        var17.setMeta((IPersistentMap)((Object)const__42));
        Var var18 = var17;
        var17.bindRoot(new stack$replace());
        Var var19 = const__43;
        var19.setMeta((IPersistentMap)((Object)const__46));
        Var var20 = var19;
        var19.bindRoot(new stack$edit());
        Var var21 = const__47;
        var21.setMeta((IPersistentMap)((Object)const__49));
        Var var22 = var21;
        var21.bindRoot(const__50.getRawRoot());
        Var var23 = const__51;
        var23.setMeta((IPersistentMap)((Object)const__53));
        Var var24 = var23;
        var23.bindRoot(const__54.getRawRoot());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "ginga.stack");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("ginga.stack", "empty");
        const__9 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 5, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__10 = RT.var("ginga.stack", "cast");
        const__12 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 6, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__13 = RT.var("clojure.core", "seq");
        const__14 = RT.var("ginga.stack", "pop");
        const__16 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 7, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__17 = RT.var("clojure.core", "next");
        const__18 = RT.var("ginga.stack", "peek");
        const__20 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 8, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__21 = RT.var("clojure.core", "first");
        const__22 = RT.var("ginga.stack", "into");
        const__24 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 9, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__25 = RT.var("clojure.core", "into");
        const__26 = RT.var("ginga.stack", "of");
        const__28 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 10, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__29 = RT.var("clojure.core", "list");
        const__30 = RT.var("ginga.stack", "concat");
        const__34 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "a"), Symbol.intern(null, "b")))), RT.keyword(null, "line"), 11, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__35 = RT.var("ginga.stack", "push");
        const__38 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "s"), Symbol.intern(null, "frame")))), RT.keyword(null, "line"), 12, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__39 = RT.var("ginga.stack", "replace");
        const__42 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "s"), Symbol.intern(null, "frame")))), RT.keyword(null, "line"), 13, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__43 = RT.var("ginga.stack", "edit");
        const__46 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f"), Symbol.intern(null, "stack")))), RT.keyword(null, "line"), 14, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__47 = RT.var("ginga.stack", "empty?");
        const__49 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 16, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__50 = RT.var("clojure.core", "not");
        const__51 = RT.var("ginga.stack", "not-empty?");
        const__53 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 17, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/stack.cljc"));
        const__54 = RT.var("clojure.core", "identity");
    }

    static {
        stack__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("ginga.stack__init").getClassLoader());
        try {
            stack__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

