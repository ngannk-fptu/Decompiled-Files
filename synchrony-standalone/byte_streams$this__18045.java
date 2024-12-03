/*
 * Decompiled with CFR 0.152.
 */
import byte_streams.graph.Type;
import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Util;
import clojure.lang.Var;

public final class byte_streams$this__18045
extends AFunction {
    public static final Var const__1 = RT.var("byte-streams", "converter");
    public static final AFn const__3 = Symbol.intern(null, "seq");
    public static final AFn const__4 = Symbol.intern(null, "stream");
    public static final Var const__5 = RT.var("clojure.core", "first");
    public static final Var const__6 = RT.var("clojure.core", "map");
    public static final Var const__7 = RT.var("clojure.core", "keys");
    public static final Var const__8 = RT.var("clojure.core", "deref");
    public static final Var const__9 = RT.var("byte-streams", "src->dst->transfer");
    public static final Var const__13 = RT.var("clojure.core", "get-in");
    public static final Var const__14 = RT.var("byte-streams.graph", "type");
    public static final Var const__15 = RT.var("byte-streams.protocols", "ByteSource");
    public static final Var const__16 = RT.var("byte-streams.protocols", "ByteSink");
    public static final Keyword const__17 = RT.keyword(null, "else");

    public static Object invokeStatic(Object src, Object dst) {
        AFunction aFunction;
        Object object;
        Object and__5579__auto__18073;
        Object converter_fn = Util.identical(((Type)src).wrapper, null) ? const__1.getRawRoot() : (Util.equiv((Object)const__3, ((Type)src).wrapper) ? new byte_streams$this__18045$fn__18046() : (Util.equiv((Object)const__4, ((Type)src).wrapper) ? new byte_streams$this__18045$fn__18048() : null));
        Object vec__18050 = ((IFn)const__5.getRawRoot()).invoke(((IFn)const__6.getRawRoot()).invoke(new byte_streams$this__18045$fn__18053(dst, converter_fn, src), ((IFn)const__7.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot()))));
        Object src_SINGLEQUOTE_ = RT.nth(vec__18050, RT.intCast(0L), null);
        Object object2 = vec__18050;
        vec__18050 = null;
        Object dst_SINGLEQUOTE_ = RT.nth(object2, RT.intCast(1L), null);
        Object object3 = and__5579__auto__18073 = src_SINGLEQUOTE_;
        if (object3 != null && object3 != Boolean.FALSE) {
            object = dst_SINGLEQUOTE_;
        } else {
            object = and__5579__auto__18073;
            and__5579__auto__18073 = null;
        }
        if (object != null && object != Boolean.FALSE) {
            Object f = ((IFn)const__13.getRawRoot()).invoke(((IFn)const__8.getRawRoot()).invoke(const__9.getRawRoot()), Tuple.create(src_SINGLEQUOTE_, dst_SINGLEQUOTE_));
            dst_SINGLEQUOTE_ = null;
            src_SINGLEQUOTE_ = null;
            f = null;
            aFunction = new byte_streams$this__18045$fn__18060(dst_SINGLEQUOTE_, src_SINGLEQUOTE_, f);
        } else {
            Object object4;
            Object and__5579__auto__18074;
            Object object5 = converter_fn;
            converter_fn = null;
            Object object6 = src;
            src = null;
            Object object7 = and__5579__auto__18074 = ((IFn)object5).invoke(object6, ((IFn)const__14.getRawRoot()).invoke(const__15));
            if (object7 != null && object7 != Boolean.FALSE) {
                Object object8 = dst;
                dst = null;
                object4 = ((IFn)const__1.getRawRoot()).invoke(object8, ((IFn)const__14.getRawRoot()).invoke(const__16));
            } else {
                object4 = and__5579__auto__18074;
                and__5579__auto__18074 = null;
            }
            if (object4 != null && object4 != Boolean.FALSE) {
                aFunction = new byte_streams$this__18045$fn__18063();
            } else {
                Keyword keyword2 = const__17;
                aFunction = keyword2 != null && keyword2 != Boolean.FALSE ? null : null;
            }
        }
        return aFunction;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return byte_streams$this__18045.invokeStatic(object3, object4);
    }
}

