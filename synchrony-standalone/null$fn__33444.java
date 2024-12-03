/*
 * Decompiled with CFR 0.152.
 */
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;
import clojure.tools.logging.impl.Logger;
import clojure.tools.logging.impl.LoggerFactory;
import manifold.deferred.IDeferred;

public final class null$fn__33444
extends AFunction {
    Object d__15470__auto__;
    Object frame__14960__auto__;
    Object body;
    private static Class __cached_class__0;
    private static Class __cached_class__1;
    public static final Var const__0;
    public static final Var const__1;
    public static final Var const__2;
    public static final Var const__3;
    public static final Var const__4;
    public static final Object const__5;
    public static final Var const__6;
    public static final Keyword const__7;
    public static final Var const__10;
    public static final Var const__11;

    public null$fn__33444(Object object, Object object2, Object object3) {
        this.d__15470__auto__ = object;
        this.frame__14960__auto__ = object2;
        this.body = object3;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public Object invoke() {
        curr_frame__14963__auto__33448 = Var.getThreadBindingFrame();
        Var.resetThreadBindingFrame(this.frame__14960__auto__);
        try {
            if (((IDeferred)this.d__15470__auto__).realized()) {
                v0 = null;
            } else {
                try {
                    var2_2 = ((IFn)null$fn__33444.const__0.getRawRoot()).invoke(this.d__15470__auto__, ((IFn)null$fn__33444.const__1.getRawRoot()).invoke(this.body));
                }
                catch (Throwable e__15471__auto__) {
                    e__15471__auto__ = null;
                    var2_2 = ((IFn)null$fn__33444.const__2.getRawRoot()).invoke(this.d__15470__auto__, e__15471__auto__);
                }
                v0 = var2_2;
            }
            var4_7 = v0;
        }
        catch (Throwable e__14964__auto__) {
            v1 = null$fn__33444.const__4.get();
            if (Util.classOf(v1) == null$fn__33444.__cached_class__0) ** GOTO lbl22
            if (!(v1 instanceof LoggerFactory)) {
                v1 = v1;
                null$fn__33444.__cached_class__0 = Util.classOf(v1);
lbl22:
                // 2 sources

                v2 = null$fn__33444.const__3.getRawRoot().invoke(v1, null$fn__33444.const__5);
            } else {
                v2 = ((LoggerFactory)v1).get_logger(null$fn__33444.const__5);
            }
            if (Util.classOf(v3 = (logger__8647__auto__33447 = v2)) == null$fn__33444.__cached_class__1) ** GOTO lbl29
            if (!(v3 instanceof Logger)) {
                v3 = v3;
                null$fn__33444.__cached_class__1 = Util.classOf(v3);
lbl29:
                // 2 sources

                v4 = null$fn__33444.const__6.getRawRoot().invoke(v3, null$fn__33444.const__7);
            } else {
                v4 = ((Logger)v3).enabled_QMARK_(null$fn__33444.const__7);
            }
            if (v4 != null && v4 != Boolean.FALSE) {
                e__14964__auto__ = null;
                x__8648__auto__33446 = e__14964__auto__;
                if (x__8648__auto__33446 instanceof Throwable) {
                    v5 = logger__8647__auto__33447;
                    logger__8647__auto__33447 = null;
                    v6 = x__8648__auto__33446;
                    x__8648__auto__33446 = null;
                    v7 = ((IFn)null$fn__33444.const__10.getRawRoot()).invoke(v5, null$fn__33444.const__7, v6, ((IFn)null$fn__33444.const__11.getRawRoot()).invoke("error in manifold.utils/future-with"));
                } else {
                    v8 = logger__8647__auto__33447;
                    logger__8647__auto__33447 = null;
                    v9 = x__8648__auto__33446;
                    x__8648__auto__33446 = null;
                    v7 = ((IFn)null$fn__33444.const__10.getRawRoot()).invoke(v8, null$fn__33444.const__7, null, ((IFn)null$fn__33444.const__11.getRawRoot()).invoke(v9, "error in manifold.utils/future-with"));
                }
            } else {
                v7 = null;
            }
            var4_7 = v7;
        }
        finally {
            v10 = curr_frame__14963__auto__33448;
            curr_frame__14963__auto__33448 = null;
            Var.resetThreadBindingFrame(v10);
        }
        return var4_7;
    }

    static {
        const__0 = RT.var("manifold.deferred", "success!");
        const__1 = RT.var("clj-commons.byte-streams", "to-byte-array");
        const__2 = RT.var("manifold.deferred", "error!");
        const__3 = RT.var("clojure.tools.logging.impl", "get-logger");
        const__4 = RT.var("clojure.tools.logging", "*logger-factory*");
        const__5 = RT.readString("#=(find-ns ^#=(clojure.lang.PersistentArrayMap/create {:doc \"This middleware is adapted from clj-http, whose license is amenable to this sort of\\n   copy/pastery\"}) aleph.http.client-middleware)");
        const__6 = RT.var("clojure.tools.logging.impl", "enabled?");
        const__7 = RT.keyword(null, "error");
        const__10 = RT.var("clojure.tools.logging", "log*");
        const__11 = RT.var("clojure.core", "print-str");
    }
}

