/*
 * Decompiled with CFR 0.152.
 */
package riddley;

import clojure.lang.AFn;
import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.ILookupThunk;
import clojure.lang.IObj;
import clojure.lang.KeywordLookupSite;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Tuple;
import clojure.lang.Util;
import clojure.lang.Var;

public final class walk$macroexpand$fn__14704
extends AFunction {
    Object special_form_QMARK_;
    Object x;
    public static final Var const__0 = RT.var("clojure.core", "seq?");
    public static final Var const__1 = RT.var("clojure.core", "first");
    public static final Var const__2 = RT.var("clojure.core", "contains?");
    public static final Var const__3 = RT.var("riddley.compiler", "locals");
    public static final Var const__4 = RT.var("clojure.core", "macroexpand-1");
    public static final Var const__5 = RT.var("clojure.core", "not");
    public static final Var const__7 = RT.var("riddley.walk", "macroexpand");
    public static final Var const__8 = RT.var("clojure.core", "symbol?");
    public static final Var const__10 = RT.var("clojure.core", "meta");
    public static final Var const__12 = RT.var("clojure.core", "resolve");
    public static final Var const__14 = RT.var("clojure.core", "rest");
    public static final Var const__16 = RT.var("clojure.core", "apply");
    public static final AFn const__18 = Symbol.intern(null, ".");
    public static final Var const__19 = RT.var("clojure.core", "with-meta");
    public static final Var const__20 = RT.var("clojure.core", "concat");
    public static final Var const__21 = RT.var("clojure.core", "butlast");
    public static final Var const__24 = RT.var("clojure.core", "last");
    public static final Var const__25 = RT.var("clojure.core", "merge");
    public static final AFn const__26 = (AFn)((Object)RT.map(RT.keyword("riddley.walk", "transformed"), Boolean.TRUE));
    static final KeywordLookupSite __site__0__ = new KeywordLookupSite(RT.keyword("riddley.walk", "transformed"));
    static ILookupThunk __thunk__0__ = __site__0__;
    static final KeywordLookupSite __site__1__ = new KeywordLookupSite(RT.keyword(null, "inline-arities"));
    static ILookupThunk __thunk__1__ = __site__1__;
    static final KeywordLookupSite __site__2__ = new KeywordLookupSite(RT.keyword(null, "inline-arities"));
    static ILookupThunk __thunk__2__ = __site__2__;
    static final KeywordLookupSite __site__3__ = new KeywordLookupSite(RT.keyword(null, "inline"));
    static ILookupThunk __thunk__3__ = __site__3__;

    public walk$macroexpand$fn__14704(Object object, Object object2) {
        this.special_form_QMARK_ = object;
        this.x = object2;
    }

    @Override
    public Object invoke() {
        Object object;
        Object object2 = ((IFn)const__0.getRawRoot()).invoke(this_.x);
        if (object2 != null && object2 != Boolean.FALSE) {
            Object object3;
            Object or__5581__auto__14707;
            Object object4;
            Object and__5579__auto__14706;
            Object frst = ((IFn)const__1.getRawRoot()).invoke(this_.x);
            Object object5 = and__5579__auto__14706 = this_.special_form_QMARK_;
            if (object5 != null && object5 != Boolean.FALSE) {
                object4 = ((IFn)this_.special_form_QMARK_).invoke(frst);
            } else {
                object4 = and__5579__auto__14706;
                or__5581__auto__14707 = null;
            }
            Object object6 = or__5581__auto__14707 = object4;
            if (object6 != null && object6 != Boolean.FALSE) {
                object3 = or__5581__auto__14707;
                or__5581__auto__14707 = null;
            } else {
                Object object7 = frst;
                frst = null;
                object3 = ((IFn)const__2.getRawRoot()).invoke(((IFn)const__3.getRawRoot()).invoke(), object7);
            }
            if (object3 != null && object3 != Boolean.FALSE) {
                object = this_.x;
            } else {
                walk$macroexpand$fn__14704 this_;
                Object x_SINGLEQUOTE_ = ((IFn)const__4.getRawRoot()).invoke(this_.x);
                Object object8 = ((IFn)const__5.getRawRoot()).invoke(Util.identical(this_.x, x_SINGLEQUOTE_) ? Boolean.TRUE : Boolean.FALSE);
                if (object8 != null && object8 != Boolean.FALSE) {
                    Object object9 = x_SINGLEQUOTE_;
                    x_SINGLEQUOTE_ = null;
                    this_ = null;
                    object = ((IFn)const__7.getRawRoot()).invoke(object9, this_.special_form_QMARK_);
                } else {
                    Object temp__5802__auto__14713;
                    Object object10;
                    Object and__5579__auto__14712;
                    Object object11 = and__5579__auto__14712 = ((IFn)const__0.getRawRoot()).invoke(x_SINGLEQUOTE_);
                    if (object11 != null && object11 != Boolean.FALSE) {
                        Object and__5579__auto__14711;
                        Object object12 = and__5579__auto__14711 = ((IFn)const__8.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(x_SINGLEQUOTE_));
                        if (object12 != null && object12 != Boolean.FALSE) {
                            Object and__5579__auto__14710;
                            IFn iFn = (IFn)const__5.getRawRoot();
                            ILookupThunk iLookupThunk = __thunk__0__;
                            Object object13 = ((IFn)const__10.getRawRoot()).invoke(x_SINGLEQUOTE_);
                            Object object14 = iLookupThunk.get(object13);
                            if (iLookupThunk == object14) {
                                __thunk__0__ = __site__0__.fault(object13);
                                object14 = __thunk__0__.get(object13);
                            }
                            Object object15 = and__5579__auto__14710 = iFn.invoke(object14);
                            if (object15 != null && object15 != Boolean.FALSE) {
                                Object and__5579__auto__14709;
                                Object object16;
                                Object or__5581__auto__14708;
                                IFn iFn2 = (IFn)const__5.getRawRoot();
                                ILookupThunk iLookupThunk2 = __thunk__1__;
                                Object object17 = ((IFn)const__10.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(x_SINGLEQUOTE_)));
                                Object object18 = iLookupThunk2.get(object17);
                                if (iLookupThunk2 == object18) {
                                    __thunk__1__ = __site__1__.fault(object17);
                                    object18 = __thunk__1__.get(object17);
                                }
                                Object object19 = or__5581__auto__14708 = iFn2.invoke(object18);
                                if (object19 != null && object19 != Boolean.FALSE) {
                                    object16 = or__5581__auto__14708;
                                    or__5581__auto__14708 = null;
                                } else {
                                    ILookupThunk iLookupThunk3 = __thunk__2__;
                                    Object object20 = ((IFn)const__10.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(x_SINGLEQUOTE_)));
                                    Object object21 = iLookupThunk3.get(object20);
                                    if (iLookupThunk3 == object21) {
                                        __thunk__2__ = __site__2__.fault(object20);
                                        object21 = __thunk__2__.get(object20);
                                    }
                                    object16 = ((IFn)object21).invoke(RT.count(((IFn)const__14.getRawRoot()).invoke(x_SINGLEQUOTE_)));
                                }
                                Object object22 = and__5579__auto__14709 = object16;
                                if (object22 != null && object22 != Boolean.FALSE) {
                                    ILookupThunk iLookupThunk4 = __thunk__3__;
                                    Object object23 = ((IFn)const__10.getRawRoot()).invoke(((IFn)const__12.getRawRoot()).invoke(((IFn)const__1.getRawRoot()).invoke(x_SINGLEQUOTE_)));
                                    object10 = iLookupThunk4.get(object23);
                                    if (iLookupThunk4 == object10) {
                                        __thunk__3__ = __site__3__.fault(object23);
                                        object10 = __thunk__3__.get(object23);
                                    }
                                } else {
                                    object10 = and__5579__auto__14709;
                                    and__5579__auto__14709 = null;
                                }
                            } else {
                                object10 = and__5579__auto__14710;
                                and__5579__auto__14710 = null;
                            }
                        } else {
                            object10 = and__5579__auto__14711;
                            and__5579__auto__14711 = null;
                        }
                    } else {
                        object10 = and__5579__auto__14712;
                        temp__5802__auto__14713 = null;
                    }
                    Object object24 = temp__5802__auto__14713 = object10;
                    if (object24 != null && object24 != Boolean.FALSE) {
                        Object object25;
                        Object inline_fn;
                        Object object26 = temp__5802__auto__14713;
                        temp__5802__auto__14713 = null;
                        Object object27 = inline_fn = object26;
                        inline_fn = null;
                        Object object28 = x_SINGLEQUOTE_;
                        x_SINGLEQUOTE_ = null;
                        Object x_SINGLEQUOTE__SINGLEQUOTE_ = ((IFn)const__16.getRawRoot()).invoke(object27, ((IFn)const__14.getRawRoot()).invoke(object28));
                        IFn iFn = (IFn)const__7.getRawRoot();
                        if (Util.equiv((Object)const__18, ((IFn)const__1.getRawRoot()).invoke(x_SINGLEQUOTE__SINGLEQUOTE_))) {
                            Object object29 = ((IFn)const__21.getRawRoot()).invoke(x_SINGLEQUOTE__SINGLEQUOTE_);
                            Object object30 = ((IFn)const__24.getRawRoot()).invoke(x_SINGLEQUOTE__SINGLEQUOTE_) instanceof IObj ? ((IFn)const__19.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(x_SINGLEQUOTE__SINGLEQUOTE_), ((IFn)const__25.getRawRoot()).invoke(((IFn)const__10.getRawRoot()).invoke(((IFn)const__24.getRawRoot()).invoke(x_SINGLEQUOTE__SINGLEQUOTE_)), const__26)) : ((IFn)const__24.getRawRoot()).invoke(x_SINGLEQUOTE__SINGLEQUOTE_);
                            Object object31 = x_SINGLEQUOTE__SINGLEQUOTE_;
                            x_SINGLEQUOTE__SINGLEQUOTE_ = null;
                            object25 = ((IFn)const__19.getRawRoot()).invoke(((IFn)const__20.getRawRoot()).invoke(object29, Tuple.create(object30)), ((IFn)const__10.getRawRoot()).invoke(object31));
                        } else {
                            object25 = x_SINGLEQUOTE__SINGLEQUOTE_;
                            x_SINGLEQUOTE__SINGLEQUOTE_ = null;
                        }
                        this_ = null;
                        object = iFn.invoke(object25, this_.special_form_QMARK_);
                    } else {
                        object = x_SINGLEQUOTE_;
                        Object var2_2 = null;
                    }
                }
            }
        } else {
            object = this_.x;
        }
        return object;
    }
}

