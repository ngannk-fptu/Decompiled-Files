/*
 * Decompiled with CFR 0.152.
 */
package potemkin;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import clojure.lang.Keyword;
import clojure.lang.Numbers;
import clojure.lang.PersistentArrayMap;
import clojure.lang.RT;
import clojure.lang.RestFn;
import clojure.lang.Symbol;
import clojure.lang.Util;
import clojure.lang.Var;

public final class utils$fast_bound_fn
extends RestFn {
    public static final Var const__0 = RT.var("clojure.core", "*clojure-version*");
    public static final Var const__1 = RT.var("clojure.core", "seq?");
    public static final Var const__2 = RT.var("clojure.core", "next");
    public static final Var const__3 = RT.var("clojure.core", "to-array");
    public static final Var const__4 = RT.var("clojure.core", "seq");
    public static final Var const__5 = RT.var("clojure.core", "first");
    public static final Keyword const__7 = RT.keyword(null, "major");
    public static final Keyword const__8 = RT.keyword(null, "minor");
    public static final Var const__14 = RT.var("clojure.core", "concat");
    public static final Var const__15 = RT.var("clojure.core", "list");
    public static final AFn const__16 = Symbol.intern("clojure.core", "let");
    public static final Var const__17 = RT.var("clojure.core", "apply");
    public static final Var const__18 = RT.var("clojure.core", "vector");
    public static final AFn const__19 = Symbol.intern(null, "bindings__26224__auto__");
    public static final AFn const__20 = Symbol.intern("clojure.core", "get-thread-bindings");
    public static final AFn const__21 = Symbol.intern(null, "f__26225__auto__");
    public static final AFn const__22 = Symbol.intern("clojure.core", "fn");
    public static final AFn const__23 = Symbol.intern("clojure.core", "fn");
    public static final AFn const__24 = Symbol.intern(null, "&");
    public static final AFn const__25 = Symbol.intern(null, "args__26226__auto__");
    public static final AFn const__26 = Symbol.intern("clojure.core", "with-bindings");
    public static final AFn const__27 = Symbol.intern("clojure.core", "apply");
    public static final AFn const__28 = Symbol.intern("clojure.core", "let");
    public static final AFn const__29 = Symbol.intern(null, "bound-frame__26227__auto__");
    public static final AFn const__30 = Symbol.intern("clojure.lang.Var", "getThreadBindingFrame");
    public static final AFn const__31 = Symbol.intern("clojure.lang.Var", "cloneThreadBindingFrame");
    public static final AFn const__32 = Symbol.intern(null, "f__26228__auto__");
    public static final AFn const__33 = Symbol.intern("clojure.core", "fn");
    public static final AFn const__34 = Symbol.intern("clojure.core", "fn");
    public static final AFn const__35 = Symbol.intern(null, "&");
    public static final AFn const__36 = Symbol.intern(null, "args__26229__auto__");
    public static final AFn const__37 = Symbol.intern("clojure.core", "let");
    public static final AFn const__38 = Symbol.intern(null, "curr-frame__26230__auto__");
    public static final AFn const__39 = Symbol.intern("clojure.lang.Var", "getThreadBindingFrame");
    public static final AFn const__40 = Symbol.intern("clojure.lang.Var", "resetThreadBindingFrame");
    public static final AFn const__41 = Symbol.intern(null, "try");
    public static final AFn const__42 = Symbol.intern("clojure.core", "apply");
    public static final AFn const__43 = Symbol.intern(null, "finally");
    public static final AFn const__44 = Symbol.intern("clojure.lang.Var", "resetThreadBindingFrame");

    public static Object invokeStatic(Object _AMPERSAND_form, Object _AMPERSAND_env, ISeq fn_body) {
        Object object;
        boolean use_get_binding_QMARK_;
        boolean bl;
        Object object2;
        Object map__26231 = const__0.get();
        Object object3 = ((IFn)const__1.getRawRoot()).invoke(map__26231);
        if (object3 != null && object3 != Boolean.FALSE) {
            Object object4 = ((IFn)const__2.getRawRoot()).invoke(map__26231);
            if (object4 != null && object4 != Boolean.FALSE) {
                Object object5 = map__26231;
                map__26231 = null;
                object2 = PersistentArrayMap.createAsIfByAssoc((Object[])((IFn)const__3.getRawRoot()).invoke(object5));
            } else {
                Object object6 = ((IFn)const__4.getRawRoot()).invoke(map__26231);
                if (object6 != null && object6 != Boolean.FALSE) {
                    Object object7 = map__26231;
                    map__26231 = null;
                    object2 = ((IFn)const__5.getRawRoot()).invoke(object7);
                } else {
                    object2 = PersistentArrayMap.EMPTY;
                }
            }
        } else {
            object2 = map__26231;
            map__26231 = null;
        }
        Object map__262312 = object2;
        Object major = RT.get(map__262312, const__7);
        Object object8 = map__262312;
        map__262312 = null;
        Object minor = RT.get(object8, const__8);
        boolean and__5579__auto__26233 = Util.equiv(1L, major);
        boolean use_thread_bindings_QMARK_ = and__5579__auto__26233 ? Numbers.lt(minor, 3L) : and__5579__auto__26233;
        Object object9 = major;
        major = null;
        boolean and__5579__auto__26234 = Util.equiv(1L, object9);
        if (and__5579__auto__26234) {
            Object object10 = minor;
            minor = null;
            bl = Numbers.lt(object10, 4L);
        } else {
            bl = use_get_binding_QMARK_ = and__5579__auto__26234;
        }
        if (use_thread_bindings_QMARK_) {
            ISeq iSeq = fn_body;
            fn_body = null;
            object = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__16), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__17.getRawRoot()).invoke(const__18.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__19), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__20)))), ((IFn)const__15.getRawRoot()).invoke(const__21), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__22), iSeq))))))), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__23), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__17.getRawRoot()).invoke(const__18.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__24), ((IFn)const__15.getRawRoot()).invoke(const__25))))), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__26), ((IFn)const__15.getRawRoot()).invoke(const__19), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__27), ((IFn)const__15.getRawRoot()).invoke(const__21), ((IFn)const__15.getRawRoot()).invoke(const__25))))))))))));
        } else {
            ISeq iSeq = fn_body;
            fn_body = null;
            object = ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__28), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__17.getRawRoot()).invoke(const__18.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__29), ((IFn)const__15.getRawRoot()).invoke(use_get_binding_QMARK_ ? ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__30))) : ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__31)))), ((IFn)const__15.getRawRoot()).invoke(const__32), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__33), iSeq))))))), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__34), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__17.getRawRoot()).invoke(const__18.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__35), ((IFn)const__15.getRawRoot()).invoke(const__36))))), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__37), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__17.getRawRoot()).invoke(const__18.getRawRoot(), ((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__38), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__39)))))))), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__40), ((IFn)const__15.getRawRoot()).invoke(const__29)))), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__41), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__42), ((IFn)const__15.getRawRoot()).invoke(const__32), ((IFn)const__15.getRawRoot()).invoke(const__36)))), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__43), ((IFn)const__15.getRawRoot()).invoke(((IFn)const__4.getRawRoot()).invoke(((IFn)const__14.getRawRoot()).invoke(((IFn)const__15.getRawRoot()).invoke(const__44), ((IFn)const__15.getRawRoot()).invoke(const__38))))))))))))))))));
        }
        return object;
    }

    @Override
    public Object doInvoke(Object object, Object object2, Object object3) {
        Object object4 = object;
        object = null;
        Object object5 = object2;
        object2 = null;
        ISeq iSeq = (ISeq)object3;
        object3 = null;
        return utils$fast_bound_fn.invokeStatic(object4, object5, iSeq);
    }

    @Override
    public int getRequiredArity() {
        return 2;
    }
}

