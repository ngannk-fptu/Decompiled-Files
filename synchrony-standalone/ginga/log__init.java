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
import ginga.log$catchall_logging;
import ginga.log$cljs_log_group;
import ginga.log$cljs_log_group_end;
import ginga.log$cljs_tag_log;
import ginga.log$fn__8716;
import ginga.log$loading__6789__auto____8179;
import ginga.log$log;
import ginga.log$with_cljs_tag_log;
import ginga.log$wrap_log_group;
import java.util.Arrays;

public class log__init {
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
    public static final Var const__29;
    public static final AFn const__32;
    public static final Var const__33;
    public static final AFn const__36;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new log$loading__6789__auto____8179()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new log$fn__8716());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__12));
        Var var2 = var;
        var.bindRoot(new log$log());
        const__3.setMacro();
        Object v5 = null;
        Var var3 = const__3;
        Var var4 = const__13;
        var4.setMeta((IPersistentMap)((Object)const__16));
        Var var5 = var4;
        var4.bindRoot(new log$catchall_logging());
        const__13.setMacro();
        Object v9 = null;
        Var var6 = const__13;
        Var var7 = const__17;
        var7.setMeta((IPersistentMap)((Object)const__20));
        Var var8 = var7;
        var7.bindRoot(new log$cljs_tag_log());
        Var var9 = const__21;
        var9.setMeta((IPersistentMap)((Object)const__24));
        Var var10 = var9;
        var9.bindRoot(new log$with_cljs_tag_log());
        Var var11 = const__25;
        var11.setMeta((IPersistentMap)((Object)const__28));
        Var var12 = var11;
        var11.bindRoot(new log$cljs_log_group());
        Var var13 = const__29;
        var13.setMeta((IPersistentMap)((Object)const__32));
        Var var14 = var13;
        var13.bindRoot(new log$cljs_log_group_end());
        Var var15 = const__33;
        var15.setMeta((IPersistentMap)((Object)const__36));
        Var var16 = var15;
        var15.bindRoot(new log$wrap_log_group());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "ginga.log");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("ginga.log", "log");
        const__12 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "level"), Symbol.intern(null, "message")), Tuple.create(Symbol.intern(null, "level"), Symbol.intern(null, "throwable"), Symbol.intern(null, "message")), Tuple.create(Symbol.intern(null, "message")))), RT.keyword(null, "doc"), "Like clojure.tools.logging/log, but supports cljs.", RT.keyword(null, "line"), 40, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/log.cljc"));
        const__13 = RT.var("ginga.log", "catchall-logging");
        const__16 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "&"), Symbol.intern(null, "body")))), RT.keyword(null, "doc"), "Like ginga.core/catchall but logs exceptions.", RT.keyword(null, "line"), 55, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/log.cljc"));
        const__17 = RT.var("ginga.log", "cljs-tag-log");
        const__20 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "tag"), Symbol.intern(null, "object")))), RT.keyword(null, "line"), 60, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/log.cljc"));
        const__21 = RT.var("ginga.log", "with-cljs-tag-log");
        const__24 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "tag"), Symbol.intern(null, "f"), Symbol.intern(null, "&"), Symbol.intern(null, "args")))), RT.keyword(null, "line"), 63, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/log.cljc"));
        const__25 = RT.var("ginga.log", "cljs-log-group");
        const__28 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "group-name")))), RT.keyword(null, "line"), 68, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/log.cljc"));
        const__29 = RT.var("ginga.log", "cljs-log-group-end");
        const__32 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create())), RT.keyword(null, "line"), 69, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/log.cljc"));
        const__33 = RT.var("ginga.log", "wrap-log-group");
        const__36 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "group-name"), Symbol.intern(null, "f")))), RT.keyword(null, "line"), 70, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/log.cljc"));
    }

    static {
        log__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("ginga.log__init").getClassLoader());
        try {
            log__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

