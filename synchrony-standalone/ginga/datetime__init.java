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
import ginga.datetime$dt__GT_ts;
import ginga.datetime$fn__19289;
import ginga.datetime$format_int;
import ginga.datetime$loading__6789__auto____18508;
import ginga.datetime$minutes_to_ms;
import ginga.datetime$now;
import ginga.datetime$now_ms;
import ginga.datetime$parse;
import ginga.datetime$seconds_to_ms;
import ginga.datetime$stringify;
import ginga.datetime$ts__GT_dt;
import java.util.Arrays;

public class datetime__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
    public static final AFn const__9;
    public static final Object const__10;
    public static final Var const__11;
    public static final AFn const__16;
    public static final Var const__17;
    public static final AFn const__20;
    public static final Var const__21;
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

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new datetime$loading__6789__auto____18508()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new datetime$fn__19289());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__9));
        Var var2 = var;
        var.bindRoot(const__10);
        Var var3 = const__11;
        var3.setMeta((IPersistentMap)((Object)const__16));
        Var var4 = var3;
        var3.bindRoot(new datetime$now());
        Var var5 = const__17;
        var5.setMeta((IPersistentMap)((Object)const__20));
        Var var6 = var5;
        var5.bindRoot(new datetime$now_ms());
        Var var7 = const__21;
        var7.setMeta((IPersistentMap)((Object)const__25));
        Var var8 = var7;
        var7.bindRoot(new datetime$format_int());
        Var var9 = const__26;
        var9.setMeta((IPersistentMap)((Object)const__29));
        Var var10 = var9;
        var9.bindRoot(new datetime$stringify());
        Var var11 = const__30;
        var11.setMeta((IPersistentMap)((Object)const__33));
        Var var12 = var11;
        var11.bindRoot(new datetime$parse());
        Var var13 = const__34;
        var13.setMeta((IPersistentMap)((Object)const__37));
        Var var14 = var13;
        var13.bindRoot(new datetime$dt__GT_ts());
        Var var15 = const__38;
        var15.setMeta((IPersistentMap)((Object)const__41));
        Var var16 = var15;
        var15.bindRoot(new datetime$ts__GT_dt());
        Var var17 = const__42;
        var17.setMeta((IPersistentMap)((Object)const__45));
        Var var18 = var17;
        var17.bindRoot(new datetime$seconds_to_ms());
        Var var19 = const__46;
        var19.setMeta((IPersistentMap)((Object)const__49));
        Var var20 = var19;
        var19.bindRoot(new datetime$minutes_to_ms());
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "ginga.datetime");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("ginga.datetime", "Date");
        const__9 = (AFn)((Object)RT.map(RT.keyword(null, "line"), 5, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__10 = RT.classForName("org.joda.time.DateTime");
        const__11 = RT.var("ginga.datetime", "now");
        const__16 = (AFn)((Object)RT.map(RT.keyword(null, "export"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create())), RT.keyword(null, "line"), 7, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__17 = RT.var("ginga.datetime", "now-ms");
        const__20 = (AFn)((Object)RT.map(RT.keyword(null, "export"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create())), RT.keyword(null, "line"), 11, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__21 = RT.var("ginga.datetime", "format-int");
        const__25 = (AFn)((Object)RT.map(RT.keyword(null, "private"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "places"), Symbol.intern(null, "i")))), RT.keyword(null, "line"), 15, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__26 = RT.var("ginga.datetime", "stringify");
        const__29 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "t")))), RT.keyword(null, "line"), 23, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__30 = RT.var("ginga.datetime", "parse");
        const__33 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "iso-str")))), RT.keyword(null, "line"), 40, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__34 = RT.var("ginga.datetime", "dt->ts");
        const__37 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(((IObj)Symbol.intern(null, "t")).withMeta(RT.map(RT.keyword(null, "tag"), Symbol.intern(null, "org.joda.time.DateTime")))))), RT.keyword(null, "line"), 44, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__38 = RT.var("ginga.datetime", "ts->dt");
        const__41 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(((IObj)Symbol.intern(null, "timestamp")).withMeta(RT.map(RT.keyword(null, "tag"), Symbol.intern(null, "long")))))), RT.keyword(null, "line"), 48, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__42 = RT.var("ginga.datetime", "seconds-to-ms");
        const__45 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "seconds")))), RT.keyword(null, "line"), 52, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
        const__46 = RT.var("ginga.datetime", "minutes-to-ms");
        const__49 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "minutes")))), RT.keyword(null, "line"), 54, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/datetime.cljc"));
    }

    static {
        datetime__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("ginga.datetime__init").getClassLoader());
        try {
            datetime__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

