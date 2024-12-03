/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.IPersistentVector;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Tuple;
import clojure.lang.Util;
import clojure.lang.Var;

public final class async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170$fn__9173
extends AFunction {
    Object old_frame__5445__auto__;
    Object state_9160;
    Object G__9115;
    Object G__9112;
    Object G__9114;
    Object G__9113;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aget-object");
    public static final Object const__1 = 3L;
    public static final Object const__3 = 1L;
    public static final Var const__5 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__6 = 7L;
    public static final Object const__7 = 8L;
    public static final Object const__8 = 9L;
    public static final Object const__9 = 10L;
    public static final Object const__10 = 2L;
    public static final Keyword const__11 = RT.keyword(null, "recur");
    public static final Object const__13 = 11L;
    public static final Object const__14 = 4L;
    public static final Var const__15 = RT.var("clojure.core", "cons");
    public static final Object const__16 = 5L;
    public static final Var const__17 = RT.var("ginga.async", "coerce-timeout");
    public static final Var const__18 = RT.var("clojure.core.async", "ioc-alts!");
    public static final Object const__19 = 6L;
    public static final Var const__21 = RT.var("clojure.core.async.impl.ioc-macros", "return-chan");
    public static final Var const__23 = RT.var("ginga.core", "error");
    public static final Keyword const__27 = RT.keyword(null, "else");
    public static final Keyword const__32 = RT.keyword(null, "default");
    public static final Var const__33 = RT.var("clojure.core", "rest");
    public static final Var const__34 = RT.var("clojure.core", "str");
    public static final Var const__36 = RT.var("clojure.core", "seq");
    public static final Var const__37 = RT.var("clojure.core", "first");

    public async$try_put_BANG_$fn__9161$state_machine__5444__auto____9170$fn__9173(Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        this.old_frame__5445__auto__ = object;
        this.state_9160 = object2;
        this.G__9115 = object3;
        this.G__9112 = object4;
        this.G__9114 = object5;
        this.G__9113 = object6;
    }

    @Override
    public Object invoke() {
        Object object;
        try {
            Object result__5447__auto__9193;
            Var.resetThreadBindingFrame(((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__1));
            do {
                Object object2;
                int G__9174 = RT.intCast(((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__3));
                switch (G__9174) {
                    case 1: {
                        Object state_9160;
                        Object inst_9128 = ((IFn)this.G__9112).invoke();
                        Object inst_9129 = ((IFn)this.G__9113).invoke();
                        Object inst_9130 = ((IFn)this.G__9114).invoke();
                        Object inst_9131 = ((IFn)this.G__9115).invoke();
                        Object object3 = inst_9128;
                        inst_9128 = null;
                        Object inst_9132 = object3;
                        Object object4 = inst_9129;
                        inst_9129 = null;
                        Object inst_9133 = object4;
                        Object object5 = inst_9130;
                        inst_9130 = null;
                        Object inst_9134 = object5;
                        Object object6 = inst_9131;
                        inst_9131 = null;
                        Object inst_9135 = object6;
                        Object statearr_9175 = this.state_9160;
                        Object object7 = inst_9134;
                        inst_9134 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9175, const__6, object7);
                        Object object8 = inst_9135;
                        inst_9135 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9175, const__7, object8);
                        Object object9 = inst_9133;
                        inst_9133 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9175, const__8, object9);
                        Object object10 = inst_9132;
                        inst_9132 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9175, const__9, object10);
                        Object object11 = statearr_9175;
                        statearr_9175 = null;
                        Object object12 = state_9160 = object11;
                        state_9160 = null;
                        Object statearr_9176 = object12;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9176, const__10, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9176, const__3, const__10);
                        object2 = const__11;
                        break;
                    }
                    case 2: {
                        Object state_9160;
                        Object G__9117;
                        Object inst_9142;
                        ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__13);
                        Object inst_9134 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__6);
                        Object inst_9135 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__7);
                        Object inst_9133 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__8);
                        Object inst_9132 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__9);
                        Object statearr_9177 = this.state_9160;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9177, const__14, ((IFn)const__15.getRawRoot()).invoke(const__16, ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__14)));
                        statearr_9177 = null;
                        Object timeout2 = inst_9134;
                        Object object13 = timeout2;
                        timeout2 = null;
                        Object G__9119 = inst_9142 = ((IFn)const__17.getRawRoot()).invoke(object13);
                        Object G__9118 = inst_9133;
                        inst_9133 = null;
                        inst_9134 = null;
                        inst_9135 = null;
                        Object object14 = inst_9132;
                        inst_9132 = null;
                        Object object15 = G__9117 = object14;
                        G__9117 = null;
                        Object object16 = G__9118;
                        G__9118 = null;
                        Object object17 = G__9119;
                        G__9119 = null;
                        IPersistentVector inst_9143 = Tuple.create(Tuple.create(object15, object16), object17);
                        Object statearr_9178 = this.state_9160;
                        Object object18 = inst_9142;
                        inst_9142 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9178, const__13, object18);
                        Object object19 = statearr_9178;
                        statearr_9178 = null;
                        Object object20 = state_9160 = object19;
                        state_9160 = null;
                        IPersistentVector iPersistentVector = inst_9143;
                        inst_9143 = null;
                        object2 = ((IFn)const__18.getRawRoot()).invoke(object20, const__19, iPersistentVector);
                        break;
                    }
                    case 3: {
                        Object inst_9158;
                        Object object21 = inst_9158 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__10);
                        inst_9158 = null;
                        object2 = ((IFn)const__21.getRawRoot()).invoke(this.state_9160, object21);
                        break;
                    }
                    case 4: {
                        Object e__8316__auto__9190;
                        Object inst_9134 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__6);
                        Object inst_9135 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__7);
                        Object inst_9133 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__8);
                        Object inst_9132 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__9);
                        Object inst_9136 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__10);
                        inst_9133 = null;
                        inst_9132 = null;
                        inst_9134 = null;
                        inst_9135 = null;
                        Object object22 = inst_9136;
                        inst_9136 = null;
                        Object object23 = e__8316__auto__9190 = object22;
                        e__8316__auto__9190 = null;
                        Object inst_9137 = ((IFn)const__23.getRawRoot()).invoke(object23);
                        Object statearr_9179 = this.state_9160;
                        Object object24 = inst_9137;
                        inst_9137 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9179, const__10, object24);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9179, const__3, const__1);
                        object2 = const__11;
                        break;
                    }
                    case 5: {
                        Object ex9172 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__10);
                        Object statearr_9180 = this.state_9160;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9180, const__16, ex9172);
                        if (ex9172 instanceof Throwable) {
                            Object statearr_9181 = this.state_9160;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_9181, const__3, const__14);
                            ((IFn)const__5.getRawRoot()).invoke(statearr_9181, const__16, null);
                        } else {
                            Keyword keyword2 = const__27;
                            if (keyword2 != null && keyword2 != Boolean.FALSE) {
                                Object object25 = ex9172;
                                ex9172 = null;
                                throw (Throwable)object25;
                            }
                        }
                        object2 = const__11;
                        break;
                    }
                    case 6: {
                        Object object26;
                        Object vec__9122;
                        Object vec__91222;
                        Object inst_9142 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__13);
                        Object inst_9134 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__6);
                        Object inst_9135 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__7);
                        Object inst_9133 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__8);
                        Object inst_9132 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__9);
                        Object inst_9145 = ((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__10);
                        Object object27 = vec__91222 = inst_9145;
                        vec__91222 = null;
                        Object inst_9146 = RT.nth(object27, RT.intCast(0L), null);
                        Object object28 = vec__9122 = inst_9145;
                        vec__9122 = null;
                        Object inst_9147 = RT.nth(object28, RT.intCast(1L), null);
                        Object inst_9148 = inst_9132;
                        Object inst_9149 = inst_9133;
                        Object object29 = inst_9142;
                        inst_9142 = null;
                        Object inst_9150 = object29;
                        Object inst_9151 = inst_9145;
                        Object object30 = inst_9146;
                        inst_9146 = null;
                        Object inst_9152 = object30;
                        Object object31 = inst_9147;
                        inst_9147 = null;
                        Object inst_9153 = object31;
                        Object object32 = inst_9145;
                        inst_9145 = null;
                        Object inst_9154 = object32;
                        Object object33 = inst_9150;
                        inst_9150 = null;
                        Object G__9119 = object33;
                        inst_9149 = null;
                        inst_9133 = null;
                        inst_9132 = null;
                        inst_9134 = null;
                        Object object34 = inst_9135;
                        inst_9135 = null;
                        Object timeout_val = object34;
                        Object object35 = inst_9153;
                        inst_9153 = null;
                        Object ch9120 = object35;
                        Object object36 = inst_9152;
                        inst_9152 = null;
                        Object val__5616__auto__9192 = object36;
                        Object object37 = inst_9154;
                        inst_9154 = null;
                        Object ret9121 = object37;
                        Object object38 = inst_9148;
                        inst_9148 = null;
                        Object G__9117 = object38;
                        inst_9151 = null;
                        Object object39 = G__9117;
                        G__9117 = null;
                        if (Util.equiv(ch9120, object39)) {
                            Object success2;
                            Object vec__9182;
                            Object object40 = ret9121;
                            ret9121 = null;
                            Object object41 = vec__9182 = object40;
                            vec__9182 = null;
                            object26 = success2 = RT.nth(object41, RT.intCast(0L), null);
                            success2 = null;
                        } else {
                            Object object42 = G__9119;
                            G__9119 = null;
                            if (Util.equiv(ch9120, object42)) {
                                object26 = timeout_val;
                                timeout_val = null;
                            } else {
                                Object object43 = ch9120;
                                ch9120 = null;
                                if (Util.equiv(object43, (Object)const__32)) {
                                    object26 = val__5616__auto__9192;
                                    val__5616__auto__9192 = null;
                                } else {
                                    object26 = null;
                                }
                            }
                        }
                        Object inst_9155 = object26;
                        Object statearr_9185 = this.state_9160;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9185, const__14, ((IFn)const__33.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__14)));
                        statearr_9185 = null;
                        Object statearr_9186 = this.state_9160;
                        Object object44 = inst_9155;
                        inst_9155 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9186, const__10, object44);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_9186, const__3, const__1);
                        object2 = const__11;
                        break;
                    }
                    default: {
                        throw (Throwable)new IllegalArgumentException((String)((IFn)const__34.getRawRoot()).invoke("No matching clause: ", G__9174));
                    }
                }
                result__5447__auto__9193 = object2;
            } while (Util.identical(result__5447__auto__9193, const__11));
            Object object45 = result__5447__auto__9193;
            result__5447__auto__9193 = null;
            object = object45;
        }
        catch (Throwable ex__5448__auto__2) {
            Object statearr_9187 = this.state_9160;
            ((IFn)const__5.getRawRoot()).invoke(statearr_9187, const__10, ex__5448__auto__2);
            Object object46 = ((IFn)const__36.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__14));
            if (object46 == null || object46 == Boolean.FALSE) {
                Object ex__5448__auto__2 = null;
                throw ex__5448__auto__2;
            }
            Object statearr_9188 = this.state_9160;
            ((IFn)const__5.getRawRoot()).invoke(statearr_9188, const__3, ((IFn)const__37.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__14)));
            this.state_9160 = null;
            ((IFn)const__5.getRawRoot()).invoke(statearr_9188, const__14, ((IFn)const__33.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_9160, const__14)));
            object = const__11;
        }
        finally {
            this.old_frame__5445__auto__ = null;
            Var.resetThreadBindingFrame(this.old_frame__5445__auto__);
        }
        return object;
    }
}

