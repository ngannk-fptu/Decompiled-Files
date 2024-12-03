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
import ginga.utils$charset;
import ginga.utils$dummy_writer;
import ginga.utils$fn__20400;
import ginga.utils$fn__20419;
import ginga.utils$loading__6789__auto____20398;
import ginga.utils$secure_eq_QMARK_;
import ginga.utils$secure_eq_bytes_QMARK_;
import java.util.Arrays;

public class utils__init {
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

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new utils$loading__6789__auto____20398()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new utils$fn__20400());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__12));
        Var var2 = var;
        var.bindRoot(new utils$dummy_writer());
        Var var3 = const__13;
        var3.setMeta((IPersistentMap)((Object)const__16));
        Var var4 = var3;
        var3.bindRoot(new utils$secure_eq_bytes_QMARK_());
        Var var5 = const__17;
        var5.setMeta((IPersistentMap)((Object)const__20));
        Var var6 = var5;
        var5.bindRoot(new utils$secure_eq_QMARK_());
        Var var7 = const__21;
        var7.setMeta((IPersistentMap)((Object)const__24));
        Var var8 = var7;
        var7.bindRoot(new utils$charset());
        Object object3 = ((IFn)new utils$fn__20419()).invoke();
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "ginga.utils");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("ginga.utils", "dummy-writer");
        const__12 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create())), RT.keyword(null, "doc"), "Constructs a new java.io.Writer instance that does not write anywhere.", RT.keyword(null, "line"), 6, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/utils.clj"));
        const__13 = RT.var("ginga.utils", "secure-eq-bytes?");
        const__16 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "a"), Symbol.intern(null, "b")))), RT.keyword(null, "line"), 18, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/utils.clj"));
        const__17 = RT.var("ginga.utils", "secure-eq?");
        const__20 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "a"), Symbol.intern(null, "b")))), RT.keyword(null, "line"), 23, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/utils.clj"));
        const__21 = RT.var("ginga.utils", "charset");
        const__24 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "mime-type")))), RT.keyword(null, "doc"), "Given a mime-type (HTTP Conten-Type header) return the charset\n  parameter if present.", RT.keyword(null, "line"), 26, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "ginga/utils.clj"));
    }

    static {
        utils__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("ginga.utils__init").getClassLoader());
        try {
            utils__init.load();
        }
        catch (Throwable throwable2) {
            Var.popThreadBindings();
            throw throwable2;
        }
        Var.popThreadBindings();
    }
}

