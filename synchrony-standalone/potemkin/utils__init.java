/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.Keyword;
import clojure.lang.LockingTransaction;
import clojure.lang.PersistentList;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Var;
import java.util.Arrays;
import potemkin.utils$condp_case;
import potemkin.utils$de_nil;
import potemkin.utils$de_nil__26283;
import potemkin.utils$doary;
import potemkin.utils$doit;
import potemkin.utils$fast_bound_fn;
import potemkin.utils$fast_bound_fn_STAR_;
import potemkin.utils$fast_memoize;
import potemkin.utils$fn__26222;
import potemkin.utils$loading__6789__auto____26220;
import potemkin.utils$memoize_form;
import potemkin.utils$re_nil;
import potemkin.utils$re_nil__26274;
import potemkin.utils$retry_exception_QMARK_;
import potemkin.utils$try_STAR_;

public class utils__init {
    public static final Var const__0;
    public static final AFn const__1;
    public static final AFn const__2;
    public static final Var const__3;
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
    public static final Var const__37;
    public static final Keyword const__38;
    public static final Var const__39;
    public static final AFn const__42;
    public static final Var const__43;
    public static final AFn const__46;
    public static final Var const__47;
    public static final AFn const__50;
    public static final Var const__51;
    public static final AFn const__54;
    public static final Var const__55;
    public static final AFn const__58;

    public static void load() {
        Object v2;
        Object object = ((IFn)const__0.getRawRoot()).invoke(const__1);
        Object object2 = ((IFn)new utils$loading__6789__auto____26220()).invoke();
        if (((Symbol)const__1).equals(const__2)) {
            v2 = null;
        } else {
            LockingTransaction.runInTransaction(new utils$fn__26222());
            v2 = null;
        }
        Var var = const__3;
        var.setMeta((IPersistentMap)((Object)const__15));
        Var var2 = var;
        var.bindRoot(new utils$fast_bound_fn());
        const__3.setMacro();
        Object v5 = null;
        Var var3 = const__3;
        Var var4 = const__16;
        var4.setMeta((IPersistentMap)((Object)const__19));
        Var var5 = var4;
        var4.bindRoot(new utils$fast_bound_fn_STAR_());
        Var var6 = const__20;
        var6.setMeta((IPersistentMap)((Object)const__23));
        Var var7 = var6;
        var6.bindRoot(new utils$retry_exception_QMARK_());
        Var var8 = const__24;
        var8.setMeta((IPersistentMap)((Object)const__27));
        Var var9 = var8;
        var8.bindRoot(new utils$try_STAR_());
        const__24.setMacro();
        Object v13 = null;
        Var var10 = const__24;
        Var var11 = const__28;
        var11.setMeta((IPersistentMap)((Object)const__31));
        Var var12 = var11;
        var11.bindRoot(new utils$condp_case());
        const__28.setMacro();
        Object v17 = null;
        Var var13 = const__28;
        Var var14 = const__32;
        var14.setMeta((IPersistentMap)((Object)const__35));
        Var var15 = var14;
        var14.bindRoot(new utils$re_nil());
        Object object3 = ((IFn)const__36.getRawRoot()).invoke(const__32, const__37.getRawRoot(), const__38, new utils$re_nil__26274());
        Var var16 = const__32;
        Var var17 = const__39;
        var17.setMeta((IPersistentMap)((Object)const__42));
        Var var18 = var17;
        var17.bindRoot(new utils$de_nil());
        Object object4 = ((IFn)const__36.getRawRoot()).invoke(const__39, const__37.getRawRoot(), const__38, new utils$de_nil__26283());
        Var var19 = const__39;
        Var var20 = const__43;
        var20.setMeta((IPersistentMap)((Object)const__46));
        Var var21 = var20;
        var20.bindRoot(new utils$memoize_form());
        const__43.setMacro();
        Object v29 = null;
        Var var22 = const__43;
        Var var23 = const__47;
        var23.setMeta((IPersistentMap)((Object)const__50));
        Var var24 = var23;
        var23.bindRoot(new utils$fast_memoize());
        Var var25 = const__51;
        var25.setMeta((IPersistentMap)((Object)const__54));
        Var var26 = var25;
        var25.bindRoot(new utils$doit());
        const__51.setMacro();
        Object v35 = null;
        Var var27 = const__51;
        Var var28 = const__55;
        var28.setMeta((IPersistentMap)((Object)const__58));
        Var var29 = var28;
        var28.bindRoot(new utils$doary());
        const__55.setMacro();
        Object v39 = null;
        Var var30 = const__55;
    }

    public static void __init0() {
        const__0 = RT.var("clojure.core", "in-ns");
        const__1 = Symbol.intern(null, "potemkin.utils");
        const__2 = Symbol.intern(null, "clojure.core");
        const__3 = RT.var("potemkin.utils", "fast-bound-fn");
        const__15 = (AFn)((Object)RT.map(RT.keyword(null, "deprecated"), Boolean.TRUE, RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "superseded-by"), "clojure.core/bound-fn", RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "&"), Symbol.intern(null, "fn-body")))), RT.keyword(null, "doc"), "Quite probably not faster than core bound-fn these days.\n\n   ~45% slower in personal testing. Be sure to profile your use case.", RT.keyword(null, "line"), 9, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__16 = RT.var("potemkin.utils", "fast-bound-fn*");
        const__19 = (AFn)((Object)RT.map(RT.keyword(null, "deprecated"), Boolean.TRUE, RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "superseded-by"), "clojure.core/bound-fn*", RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f")))), RT.keyword(null, "doc"), "Quite probably not faster than core bound-fn* these days.\n\n   ~45% slower in personal testing. Be sure to profile your use case.", RT.keyword(null, "line"), 37, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__20 = RT.var("potemkin.utils", "retry-exception?");
        const__23 = (AFn)((Object)RT.map(RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "line"), 47, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__24 = RT.var("potemkin.utils", "try*");
        const__27 = (AFn)((Object)RT.map(RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "deprecated"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "&"), Symbol.intern(null, "body+catch")))), RT.keyword(null, "doc"), "A variant of try that is fully transparent to transaction retry exceptions", RT.keyword(null, "line"), 50, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__28 = RT.var("potemkin.utils", "condp-case");
        const__31 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "predicate"), Symbol.intern(null, "value"), Symbol.intern(null, "&"), Symbol.intern(null, "cases")))), RT.keyword(null, "doc"), "A variant of condp which has case-like syntax for options.  When comparing\n   smaller numbers of keywords, this can be faster, sometimes significantly.", RT.keyword(null, "line"), 71, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__32 = RT.var("potemkin.utils", "re-nil");
        const__35 = (AFn)((Object)RT.map(RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "line"), 95, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__36 = RT.var("clojure.core", "alter-meta!");
        const__37 = RT.var("clojure.core", "assoc");
        const__38 = RT.keyword(null, "inline");
        const__39 = RT.var("potemkin.utils", "de-nil");
        const__42 = (AFn)((Object)RT.map(RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "x")))), RT.keyword(null, "line"), 99, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__43 = RT.var("potemkin.utils", "memoize-form");
        const__46 = (AFn)((Object)RT.map(RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "m"), Symbol.intern(null, "f"), Symbol.intern(null, "&"), Symbol.intern(null, "args")))), RT.keyword(null, "line"), 103, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__47 = RT.var("potemkin.utils", "fast-memoize");
        const__50 = (AFn)((Object)RT.map(RT.keyword(null, "deprecated"), Boolean.TRUE, RT.keyword(null, "no-doc"), Boolean.TRUE, RT.keyword(null, "superseded-by"), "clojure.core/memoize", RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Symbol.intern(null, "f")))), RT.keyword(null, "doc"), "Quite possibly not faster than core memoize any more.\n   See https://github.com/clj-commons/byte-streams/pull/50 and profile your use case.", RT.keyword(null, "line"), 111, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__51 = RT.var("potemkin.utils", "doit");
        const__54 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Tuple.create(Symbol.intern(null, "x"), Symbol.intern(null, "it")), Symbol.intern(null, "&"), Symbol.intern(null, "body")))), RT.keyword(null, "doc"), "An iterable-based version of doseq that doesn't emit inline-destroying chunked-seq code.", RT.keyword(null, "line"), 143, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
        const__55 = RT.var("potemkin.utils", "doary");
        const__58 = (AFn)((Object)RT.map(RT.keyword(null, "arglists"), PersistentList.create(Arrays.asList(Tuple.create(Tuple.create(Symbol.intern(null, "x"), Symbol.intern(null, "ary")), Symbol.intern(null, "&"), Symbol.intern(null, "body")))), RT.keyword(null, "doc"), "An array-specific version of doseq.", RT.keyword(null, "line"), 155, RT.keyword(null, "column"), 1, RT.keyword(null, "file"), "potemkin/utils.clj"));
    }

    static {
        utils__init.__init0();
        Compiler.pushNSandLoader(RT.classForName("potemkin.utils__init").getClassLoader());
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

