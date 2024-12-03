/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentVector;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class async$take_until$fn__10024$state_machine__5444__auto____10029$fn__10032
extends AFunction {
    Object G__9975;
    Object G__9976;
    Object state_10023;
    Object old_frame__5445__auto__;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aget-object");
    public static final Object const__1 = 3L;
    public static final Object const__3 = 1L;
    public static final Var const__5 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__6 = 7L;
    public static final Object const__7 = 8L;
    public static final Object const__8 = 2L;
    public static final Keyword const__9 = RT.keyword(null, "recur");
    public static final Object const__11 = 4L;
    public static final Var const__12 = RT.var("clojure.core", "cons");
    public static final Object const__13 = 5L;
    public static final Object const__14 = 10L;
    public static final Object const__15 = 6L;
    public static final Var const__17 = RT.var("clojure.core.async.impl.ioc-macros", "return-chan");
    public static final Var const__19 = RT.var("ginga.core", "error");
    public static final Keyword const__23 = RT.keyword(null, "else");
    public static final Var const__25 = RT.var("clojure.core.async.impl.ioc-macros", "take!");
    public static final Var const__27 = RT.var("clojure.core", "rest");
    public static final Object const__29 = 12L;
    public static final Var const__30 = RT.var("ginga.core", "error?");
    public static final Object const__31 = 9L;
    public static final Var const__33 = RT.var("clojure.core", "deref");
    public static final Var const__34 = RT.var("ginga.async", "chain-async-stacktrace");
    public static final Object const__35 = RT.readString("#=(find-ns ginga.async)");
    public static final Object const__36 = 443;
    public static final Object const__37 = 11L;
    public static final Object const__40 = 13L;
    public static final Var const__42 = RT.var("clojure.core", "conj");
    public static final Object const__43 = 15L;
    public static final Object const__44 = 16L;
    public static final Object const__46 = 14L;
    public static final Object const__49 = 17L;
    public static final Var const__52 = RT.var("clojure.core", "str");
    public static final Var const__54 = RT.var("clojure.core", "seq");
    public static final Var const__55 = RT.var("clojure.core", "first");

    public async$take_until$fn__10024$state_machine__5444__auto____10029$fn__10032(Object object, Object object2, Object object3, Object object4) {
        this.G__9975 = object;
        this.G__9976 = object2;
        this.state_10023 = object3;
        this.old_frame__5445__auto__ = object4;
    }

    @Override
    public Object invoke() {
        Object object;
        try {
            Object result__5447__auto__10065;
            Var.resetThreadBindingFrame(((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__1));
            do {
                Object object2;
                int G__10033 = RT.intCast(((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__3));
                switch (G__10033) {
                    case 1: {
                        Object state_10023;
                        Object inst_9979 = ((IFn)this.G__9975).invoke();
                        Object inst_9980 = ((IFn)this.G__9976).invoke();
                        Object object3 = inst_9979;
                        inst_9979 = null;
                        Object inst_9981 = object3;
                        Object object4 = inst_9980;
                        inst_9980 = null;
                        Object inst_9982 = object4;
                        Object statearr_10034 = this.state_10023;
                        Object object5 = inst_9982;
                        inst_9982 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10034, const__6, object5);
                        Object object6 = inst_9981;
                        inst_9981 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10034, const__7, object6);
                        Object object7 = statearr_10034;
                        statearr_10034 = null;
                        Object object8 = state_10023 = object7;
                        state_10023 = null;
                        Object statearr_10035 = object8;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10035, const__8, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10035, const__3, const__8);
                        object2 = const__9;
                        break;
                    }
                    case 2: {
                        Object statearr_10036 = this.state_10023;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10036, const__11, ((IFn)const__12.getRawRoot()).invoke(const__13, ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__11)));
                        Object _2 = null;
                        Object inst_9989 = PersistentVector.EMPTY;
                        Object statearr_10037 = this.state_10023;
                        Object object9 = inst_9989;
                        inst_9989 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10037, const__14, object9);
                        Object state_10023 = null;
                        Object object10 = state_10023 = statearr_10037;
                        state_10023 = null;
                        Object statearr_10038 = object10;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10038, const__8, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10038, const__3, const__15);
                        object2 = const__9;
                        break;
                    }
                    case 3: {
                        Object inst_10021;
                        Object object11 = inst_10021 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        inst_10021 = null;
                        object2 = ((IFn)const__17.getRawRoot()).invoke(this.state_10023, object11);
                        break;
                    }
                    case 4: {
                        Object e__8316__auto__10064;
                        Object inst_9982 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__6);
                        Object inst_9981 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__7);
                        Object inst_9983 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        inst_9982 = null;
                        inst_9981 = null;
                        Object object12 = inst_9983;
                        inst_9983 = null;
                        Object object13 = e__8316__auto__10064 = object12;
                        e__8316__auto__10064 = null;
                        Object inst_9984 = ((IFn)const__19.getRawRoot()).invoke(object13);
                        Object statearr_10039 = this.state_10023;
                        Object object14 = inst_9984;
                        inst_9984 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10039, const__8, object14);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10039, const__3, const__1);
                        object2 = const__9;
                        break;
                    }
                    case 5: {
                        Object ex10031 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        Object statearr_10040 = this.state_10023;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10040, const__13, ex10031);
                        if (ex10031 instanceof Throwable) {
                            Object statearr_10041 = this.state_10023;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10041, const__3, const__11);
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10041, const__13, null);
                        } else {
                            Keyword keyword2 = const__23;
                            if (keyword2 != null && keyword2 != Boolean.FALSE) {
                                Object object15 = ex10031;
                                ex10031 = null;
                                throw (Throwable)object15;
                            }
                        }
                        object2 = const__9;
                        break;
                    }
                    case 6: {
                        Object inst_9982;
                        Object object16 = inst_9982 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__6);
                        inst_9982 = null;
                        object2 = ((IFn)const__25.getRawRoot()).invoke(this.state_10023, const__7, object16);
                        break;
                    }
                    case 7: {
                        Object inst_10018 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        Object statearr_10042 = this.state_10023;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10042, const__11, ((IFn)const__27.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__11)));
                        statearr_10042 = null;
                        Object statearr_10043 = this.state_10023;
                        Object object17 = inst_10018;
                        inst_10018 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10043, const__8, object17);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10043, const__3, const__1);
                        object2 = const__9;
                        break;
                    }
                    case 8: {
                        Object inst_9992;
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__29);
                        Object object18 = inst_9992 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        inst_9992 = null;
                        Object inst_9993 = object18;
                        Object inst_9994 = ((IFn)const__30.getRawRoot()).invoke(inst_9993);
                        Object statearr_10044 = this.state_10023;
                        Object object19 = inst_9993;
                        inst_9993 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10044, const__29, object19);
                        Object object20 = statearr_10044;
                        statearr_10044 = null;
                        Object state_10023 = object20;
                        Object object21 = inst_9994;
                        inst_9994 = null;
                        if (object21 != null && object21 != Boolean.FALSE) {
                            Object object22 = state_10023;
                            state_10023 = null;
                            Object statearr_10045 = object22;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10045, const__3, const__31);
                        } else {
                            Object object23 = state_10023;
                            state_10023 = null;
                            Object statearr_10046 = object23;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10046, const__3, const__14);
                        }
                        object2 = const__9;
                        break;
                    }
                    case 9: {
                        Object inst_9998;
                        Object inst_9993;
                        Object object24 = inst_9993 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__29);
                        inst_9993 = null;
                        Object inst_9996 = ((IFn)const__33.getRawRoot()).invoke(object24);
                        Exception inst_9997 = new Exception();
                        Object object25 = inst_9996;
                        inst_9996 = null;
                        Exception exception = inst_9997;
                        inst_9997 = null;
                        Object object26 = inst_9998 = ((IFn)const__34.getRawRoot()).invoke(object25, exception, const__35, "ginga/async.cljc", const__36);
                        inst_9998 = null;
                        throw (Throwable)object26;
                    }
                    case 10: {
                        Object statearr_10048 = this.state_10023;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10048, const__8, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10048, const__3, const__37);
                        object2 = const__9;
                        break;
                    }
                    case 11: {
                        Object inst_9993 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__29);
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__37);
                        Object inst_10002 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        Object object27 = inst_9993;
                        inst_9993 = null;
                        Object inst_10003 = object27;
                        Object statearr_10049 = this.state_10023;
                        Object object28 = inst_10002;
                        inst_10002 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10049, const__40, object28);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10049, const__37, inst_10003);
                        Object object29 = statearr_10049;
                        statearr_10049 = null;
                        Object state_10023 = object29;
                        Object object30 = inst_10003;
                        inst_10003 = null;
                        if (object30 != null && object30 != Boolean.FALSE) {
                            Object object31 = state_10023;
                            state_10023 = null;
                            Object statearr_10050 = object31;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10050, const__3, const__29);
                        } else {
                            Object object32 = state_10023;
                            state_10023 = null;
                            Object statearr_10051 = object32;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10051, const__3, const__40);
                        }
                        object2 = const__9;
                        break;
                    }
                    case 12: {
                        Object inst_10006;
                        Object inst_9989 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__14);
                        Object inst_10003 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__37);
                        Object inst_9981 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__7);
                        Object object33 = inst_10003;
                        inst_10003 = null;
                        Object inst_10005 = object33;
                        Object object34 = inst_9989;
                        inst_9989 = null;
                        Object object35 = inst_10006 = ((IFn)const__42.getRawRoot()).invoke(object34, inst_10005);
                        inst_10006 = null;
                        Object inst_10007 = object35;
                        Object object36 = inst_9981;
                        inst_9981 = null;
                        Object object37 = inst_10005;
                        inst_10005 = null;
                        Object inst_10008 = ((IFn)object36).invoke(object37);
                        Object statearr_10052 = this.state_10023;
                        Object object38 = inst_10007;
                        inst_10007 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10052, const__31, object38);
                        Object object39 = statearr_10052;
                        statearr_10052 = null;
                        Object state_10023 = object39;
                        Object object40 = inst_10008;
                        inst_10008 = null;
                        if (object40 != null && object40 != Boolean.FALSE) {
                            Object object41 = state_10023;
                            state_10023 = null;
                            Object statearr_10053 = object41;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10053, const__3, const__43);
                        } else {
                            Object object42 = state_10023;
                            state_10023 = null;
                            Object statearr_10054 = object42;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10054, const__3, const__44);
                        }
                        object2 = const__9;
                        break;
                    }
                    case 13: {
                        Object inst_9989 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__14);
                        Object statearr_10055 = this.state_10023;
                        Object object43 = inst_9989;
                        inst_9989 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10055, const__8, object43);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10055, const__3, const__46);
                        object2 = const__9;
                        break;
                    }
                    case 14: {
                        Object inst_10016 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        Object statearr_10056 = this.state_10023;
                        Object object44 = inst_10016;
                        inst_10016 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10056, const__8, object44);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10056, const__3, const__6);
                        object2 = const__9;
                        break;
                    }
                    case 15: {
                        Object inst_10007 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__31);
                        Object statearr_10057 = this.state_10023;
                        Object object45 = inst_10007;
                        inst_10007 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10057, const__8, object45);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10057, const__3, const__49);
                        object2 = const__9;
                        break;
                    }
                    case 16: {
                        Object inst_10007;
                        Object object46 = inst_10007 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__31);
                        inst_10007 = null;
                        Object inst_9989 = object46;
                        Object statearr_10058 = this.state_10023;
                        Object object47 = inst_9989;
                        inst_9989 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10058, const__14, object47);
                        Object state_10023 = null;
                        Object object48 = state_10023 = statearr_10058;
                        state_10023 = null;
                        Object statearr_10059 = object48;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10059, const__8, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10059, const__3, const__15);
                        object2 = const__9;
                        break;
                    }
                    case 17: {
                        Object inst_10013 = ((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__8);
                        Object statearr_10060 = this.state_10023;
                        Object object49 = inst_10013;
                        inst_10013 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10060, const__8, object49);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10060, const__3, const__46);
                        object2 = const__9;
                        break;
                    }
                    default: {
                        throw (Throwable)new IllegalArgumentException((String)((IFn)const__52.getRawRoot()).invoke("No matching clause: ", G__10033));
                    }
                }
                result__5447__auto__10065 = object2;
            } while (Util.identical(result__5447__auto__10065, const__9));
            Object object50 = result__5447__auto__10065;
            result__5447__auto__10065 = null;
            object = object50;
        }
        catch (Throwable ex__5448__auto__2) {
            Object statearr_10061 = this.state_10023;
            ((IFn)const__5.getRawRoot()).invoke(statearr_10061, const__8, ex__5448__auto__2);
            Object object51 = ((IFn)const__54.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__11));
            if (object51 == null || object51 == Boolean.FALSE) {
                Object ex__5448__auto__2 = null;
                throw ex__5448__auto__2;
            }
            Object statearr_10062 = this.state_10023;
            ((IFn)const__5.getRawRoot()).invoke(statearr_10062, const__3, ((IFn)const__55.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__11)));
            this.state_10023 = null;
            ((IFn)const__5.getRawRoot()).invoke(statearr_10062, const__11, ((IFn)const__27.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_10023, const__11)));
            object = const__9;
        }
        finally {
            this.old_frame__5445__auto__ = null;
            Var.resetThreadBindingFrame(this.old_frame__5445__auto__);
        }
        return object;
    }
}

