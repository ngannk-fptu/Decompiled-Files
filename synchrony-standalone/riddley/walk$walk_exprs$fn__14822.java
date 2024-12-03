/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.IObj;
import clojure.lang.IRecord;
import clojure.lang.Keyword;
import clojure.lang.KeywordLookupSite;
import clojure.lang.MapEntry;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Util;
import clojure.lang.Var;
import java.util.Map;
import riddley.walk$walk_exprs$fn__14822$fn__14823;
import riddley.walk$walk_exprs$fn__14822$fn__14827;

public final class walk$walk_exprs$fn__14822
extends AFunction {
    Object x;
    Object predicate;
    Object special_form_QMARK_;
    Object handler;
    public static final Var const__0 = RT.var("clojure.core", "partial");
    public static final Var const__1 = RT.var("riddley.walk", "walk-exprs");
    public static final Var const__2 = RT.var("clojure.core", "seq?");
    public static final Var const__3 = RT.var("clojure.core", "=");
    public static final AFn const__4 = Symbol.intern(null, "var");
    public static final Var const__5 = RT.var("clojure.core", "first");
    public static final Var const__6 = RT.var("clojure.core", "eval");
    public static final AFn const__7 = Symbol.intern(null, "quote");
    public static final Var const__8 = RT.var("clojure.core", "not");
    public static final AFn const__9 = Symbol.intern(null, "do");
    public static final Var const__10 = RT.var("riddley.walk", "do-handler");
    public static final AFn const__11 = Symbol.intern(null, "def");
    public static final Var const__12 = RT.var("riddley.walk", "def-handler");
    public static final AFn const__13 = Symbol.intern(null, "fn*");
    public static final Var const__14 = RT.var("riddley.walk", "fn-handler");
    public static final AFn const__15 = Symbol.intern(null, "let*");
    public static final Var const__16 = RT.var("riddley.walk", "let-handler");
    public static final AFn const__17 = Symbol.intern(null, "loop*");
    public static final AFn const__18 = Symbol.intern(null, "letfn*");
    public static final AFn const__19 = Symbol.intern(null, "case*");
    public static final Var const__20 = RT.var("riddley.walk", "case-handler");
    public static final AFn const__21 = Symbol.intern(null, "catch");
    public static final Var const__22 = RT.var("riddley.walk", "catch-handler");
    public static final AFn const__23 = Symbol.intern(null, "reify*");
    public static final Var const__24 = RT.var("riddley.walk", "reify-handler");
    public static final AFn const__25 = Symbol.intern(null, "deftype*");
    public static final Var const__26 = RT.var("riddley.walk", "deftype-handler");
    public static final AFn const__27 = Symbol.intern(null, ".");
    public static final Var const__28 = RT.var("riddley.walk", "dot-handler");
    public static final Var const__31 = RT.var("clojure.core", "key");
    public static final Var const__32 = RT.var("clojure.core", "val");
    public static final Var const__33 = RT.var("clojure.core", "set?");
    public static final Var const__34 = RT.var("clojure.core", "vector?");
    public static final Var const__35 = RT.var("clojure.core", "into");
    public static final Var const__36 = RT.var("clojure.core", "empty");
    public static final Var const__37 = RT.var("clojure.core", "map");
    public static final Var const__39 = RT.var("clojure.core", "map?");
    public static final Var const__40 = RT.var("clojure.core", "symbol?");
    public static final Var const__42 = RT.var("clojure.core", "meta");
    public static final Var const__43 = RT.var("clojure.core", "vary-meta");
    public static final Var const__44 = RT.var("clojure.core", "update-in");
    public static final AFn const__45 = (AFn)((Object)Tuple.create(RT.keyword(null, "test")));
    public static final Keyword const__46 = RT.keyword(null, "else");
    public static final Var const__48 = RT.var("clojure.core", "with-meta");
    public static final Var const__49 = RT.var("clojure.core", "merge");
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword(null, "test"));
    static ILookupThunk __thunk__0__ = __site__0__;

    public walk$walk_exprs$fn__14822(Object object, Object object2, Object object3, Object object4) {
        this.x = object;
        this.predicate = object2;
        this.special_form_QMARK_ = object3;
        this.handler = object4;
    }

    @Override
    public Object invoke() {
        Object object;
        Object x_SINGLEQUOTE_;
        Object object2;
        Object object3;
        Object and__5579__auto__14831;
        Object x = ((IFn)new walk$walk_exprs$fn__14822$fn__14823(this_.x, this_.special_form_QMARK_)).invoke();
        Object walk_exprs_SINGLEQUOTE_ = ((IFn)const__0.getRawRoot()).invoke(const__1.getRawRoot(), this_.predicate, this_.handler, this_.special_form_QMARK_);
        Object object4 = and__5579__auto__14831 = ((IFn)const__2.getRawRoot()).invoke(x);
        if (object4 != null && object4 != Boolean.FALSE) {
            boolean and__5579__auto__14830 = Util.equiv((Object)const__4, ((IFn)const__5.getRawRoot()).invoke(x));
            object3 = and__5579__auto__14830 ? ((IFn)this_.predicate).invoke(x) : (and__5579__auto__14830 ? Boolean.TRUE : Boolean.FALSE);
        } else {
            object3 = and__5579__auto__14831;
            and__5579__auto__14831 = null;
        }
        if (object3 != null && object3 != Boolean.FALSE) {
            object2 = ((IFn)this_.handler).invoke(((IFn)const__6.getRawRoot()).invoke(x));
        } else {
            Object object5;
            Object and__5579__auto__14833;
            Object object6 = and__5579__auto__14833 = ((IFn)const__2.getRawRoot()).invoke(x);
            if (object6 != null && object6 != Boolean.FALSE) {
                boolean and__5579__auto__14832 = Util.equiv((Object)const__7, ((IFn)const__5.getRawRoot()).invoke(x));
                object5 = and__5579__auto__14832 ? ((IFn)const__8.getRawRoot()).invoke(((IFn)this_.predicate).invoke(x)) : (and__5579__auto__14832 ? Boolean.TRUE : Boolean.FALSE);
            } else {
                object5 = and__5579__auto__14833;
                and__5579__auto__14833 = null;
            }
            if (object5 != null && object5 != Boolean.FALSE) {
                object2 = x;
            } else {
                Object object7 = ((IFn)this_.predicate).invoke(x);
                if (object7 != null && object7 != Boolean.FALSE) {
                    object2 = ((IFn)this_.handler).invoke(x);
                } else {
                    Object object8 = ((IFn)const__2.getRawRoot()).invoke(x);
                    if (object8 != null && object8 != Boolean.FALSE) {
                        Object object9;
                        Object pred__14825 = const__3.getRawRoot();
                        Object expr__14826 = ((IFn)const__5.getRawRoot()).invoke(x);
                        Object object10 = ((IFn)pred__14825).invoke(const__9, expr__14826);
                        if (object10 != null && object10 != Boolean.FALSE) {
                            object9 = const__10.getRawRoot();
                        } else {
                            Object object11 = ((IFn)pred__14825).invoke(const__11, expr__14826);
                            if (object11 != null && object11 != Boolean.FALSE) {
                                object9 = const__12.getRawRoot();
                            } else {
                                Object object12 = ((IFn)pred__14825).invoke(const__13, expr__14826);
                                if (object12 != null && object12 != Boolean.FALSE) {
                                    object9 = const__14.getRawRoot();
                                } else {
                                    Object object13 = ((IFn)pred__14825).invoke(const__15, expr__14826);
                                    if (object13 != null && object13 != Boolean.FALSE) {
                                        object9 = const__16.getRawRoot();
                                    } else {
                                        Object object14 = ((IFn)pred__14825).invoke(const__17, expr__14826);
                                        if (object14 != null && object14 != Boolean.FALSE) {
                                            object9 = const__16.getRawRoot();
                                        } else {
                                            Object object15 = ((IFn)pred__14825).invoke(const__18, expr__14826);
                                            if (object15 != null && object15 != Boolean.FALSE) {
                                                object9 = const__16.getRawRoot();
                                            } else {
                                                Object object16 = ((IFn)pred__14825).invoke(const__19, expr__14826);
                                                if (object16 != null && object16 != Boolean.FALSE) {
                                                    object9 = const__20.getRawRoot();
                                                } else {
                                                    Object object17 = ((IFn)pred__14825).invoke(const__21, expr__14826);
                                                    if (object17 != null && object17 != Boolean.FALSE) {
                                                        object9 = const__22.getRawRoot();
                                                    } else {
                                                        Object object18 = ((IFn)pred__14825).invoke(const__23, expr__14826);
                                                        if (object18 != null && object18 != Boolean.FALSE) {
                                                            object9 = const__24.getRawRoot();
                                                        } else {
                                                            Object object19 = ((IFn)pred__14825).invoke(const__25, expr__14826);
                                                            if (object19 != null && object19 != Boolean.FALSE) {
                                                                object9 = const__26.getRawRoot();
                                                            } else {
                                                                Object object20 = pred__14825;
                                                                pred__14825 = null;
                                                                Object object21 = expr__14826;
                                                                expr__14826 = null;
                                                                Object object22 = ((IFn)object20).invoke(const__27, object21);
                                                                object9 = object22 != null && object22 != Boolean.FALSE ? const__28.getRawRoot() : new walk$walk_exprs$fn__14822$fn__14827();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Object object23 = walk_exprs_SINGLEQUOTE_;
                        walk_exprs_SINGLEQUOTE_ = null;
                        object2 = ((IFn)object9).invoke(object23, x);
                    } else if (x instanceof Map.Entry) {
                        walk_exprs_SINGLEQUOTE_ = null;
                        object2 = new MapEntry(((IFn)walk_exprs_SINGLEQUOTE_).invoke(((IFn)const__31.getRawRoot()).invoke(x)), ((IFn)walk_exprs_SINGLEQUOTE_).invoke(((IFn)const__32.getRawRoot()).invoke(x)));
                    } else {
                        Object object24;
                        Object or__5581__auto__14834;
                        Object object25 = or__5581__auto__14834 = ((IFn)const__33.getRawRoot()).invoke(x);
                        if (object25 != null && object25 != Boolean.FALSE) {
                            object24 = or__5581__auto__14834;
                            or__5581__auto__14834 = null;
                        } else {
                            object24 = ((IFn)const__34.getRawRoot()).invoke(x);
                        }
                        if (object24 != null && object24 != Boolean.FALSE) {
                            Object object26 = walk_exprs_SINGLEQUOTE_;
                            walk_exprs_SINGLEQUOTE_ = null;
                            object2 = ((IFn)const__35.getRawRoot()).invoke(((IFn)const__36.getRawRoot()).invoke(x), ((IFn)const__37.getRawRoot()).invoke(object26, x));
                        } else if (x instanceof IRecord) {
                            object2 = x;
                        } else {
                            Object object27 = ((IFn)const__39.getRawRoot()).invoke(x);
                            if (object27 != null && object27 != Boolean.FALSE) {
                                Object object28 = walk_exprs_SINGLEQUOTE_;
                                walk_exprs_SINGLEQUOTE_ = null;
                                object2 = ((IFn)const__35.getRawRoot()).invoke(((IFn)const__36.getRawRoot()).invoke(x), ((IFn)const__37.getRawRoot()).invoke(object28, x));
                            } else {
                                Object object29;
                                Object and__5579__auto__14835;
                                Object object30 = and__5579__auto__14835 = ((IFn)const__40.getRawRoot()).invoke(x);
                                if (object30 != null && object30 != Boolean.FALSE) {
                                    ILookupThunk iLookupThunk = __thunk__0__;
                                    Object object31 = ((IFn)const__42.getRawRoot()).invoke(x);
                                    object29 = iLookupThunk.get(object31);
                                    if (iLookupThunk == object29) {
                                        __thunk__0__ = __site__0__.fault(object31);
                                        object29 = __thunk__0__.get(object31);
                                    }
                                } else {
                                    object29 = and__5579__auto__14835;
                                    and__5579__auto__14835 = null;
                                }
                                if (object29 != null && object29 != Boolean.FALSE) {
                                    Object object32 = walk_exprs_SINGLEQUOTE_;
                                    walk_exprs_SINGLEQUOTE_ = null;
                                    object2 = ((IFn)const__43.getRawRoot()).invoke(x, const__44.getRawRoot(), const__45, object32);
                                } else {
                                    Keyword keyword2 = const__46;
                                    object2 = x_SINGLEQUOTE_ = keyword2 != null && keyword2 != Boolean.FALSE ? x : null;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (x_SINGLEQUOTE_ instanceof IObj) {
            Object object33 = x_SINGLEQUOTE_;
            Object object34 = x;
            x = null;
            Object object35 = x_SINGLEQUOTE_;
            x_SINGLEQUOTE_ = null;
            walk$walk_exprs$fn__14822 this_ = null;
            object = ((IFn)const__48.getRawRoot()).invoke(object33, ((IFn)const__49.getRawRoot()).invoke(((IFn)const__42.getRawRoot()).invoke(object34), ((IFn)const__42.getRawRoot()).invoke(object35)));
        } else {
            object = x_SINGLEQUOTE_;
            Object var3_3 = null;
        }
        return object;
    }
}

