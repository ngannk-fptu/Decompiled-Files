/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Util;
import clojure.lang.Var;

public final class ping$run_ping_process$fn__10576$state_machine__5444__auto____10595$fn__10597
extends AFunction {
    Object state_10575;
    Object G__10454;
    Object G__10452;
    Object G__10458;
    Object old_frame__5445__auto__;
    Object G__10451;
    Object G__10453;
    Object G__10456;
    Object G__10459;
    Object G__10457;
    Object G__10455;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aget-object");
    public static final Object const__1 = 3L;
    public static final Object const__3 = 1L;
    public static final Var const__5 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__6 = 7L;
    public static final Object const__7 = 8L;
    public static final Object const__8 = 9L;
    public static final Object const__9 = 10L;
    public static final Object const__10 = 11L;
    public static final Object const__11 = 12L;
    public static final Object const__12 = 13L;
    public static final Object const__13 = 14L;
    public static final Object const__14 = 15L;
    public static final Object const__15 = 2L;
    public static final Keyword const__16 = RT.keyword(null, "recur");
    public static final Object const__18 = 17L;
    public static final Var const__19 = RT.var("clojure.core", "reset!");
    public static final Var const__20 = RT.var("clojure.core.async", "timeout");
    public static final Var const__21 = RT.var("clojure.core", "vector");
    public static final Object const__22 = 37L;
    public static final Var const__23 = RT.var("clojure.core.async", "ioc-alts!");
    public static final Object const__24 = 4L;
    public static final Var const__26 = RT.var("clojure.core.async.impl.ioc-macros", "return-chan");
    public static final Object const__28 = 16L;
    public static final Object const__29 = 18L;
    public static final Object const__31 = 19L;
    public static final Object const__32 = 20L;
    public static final Object const__33 = 21L;
    public static final Object const__34 = 22L;
    public static final Object const__35 = 5L;
    public static final Object const__36 = 6L;
    public static final Var const__38 = RT.var("clojure.core", "deref");
    public static final Keyword const__41 = RT.keyword(null, "default");
    public static final Object const__45 = 38L;
    public static final Object const__48 = 33L;
    public static final Object const__49 = 35L;
    public static final Object const__50 = 30L;
    public static final Object const__51 = 31L;
    public static final Object const__52 = 32L;
    public static final Object const__53 = 34L;
    public static final Object const__54 = 36L;
    public static final Object const__56 = 24L;
    public static final Object const__60 = 23L;
    public static final Object const__61 = 25L;
    public static final Object const__62 = 26L;
    public static final Object const__63 = 27L;
    public static final Object const__64 = 28L;
    public static final Object const__65 = 29L;
    public static final Keyword const__71 = RT.keyword(null, "timeout");
    public static final Var const__73 = RT.var("clojure.core", "str");
    public static final Var const__75 = RT.var("clojure.core", "seq");
    public static final Var const__76 = RT.var("clojure.core", "first");
    public static final Var const__77 = RT.var("clojure.core", "rest");

    public ping$run_ping_process$fn__10576$state_machine__5444__auto____10595$fn__10597(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11) {
        this.state_10575 = object;
        this.G__10454 = object2;
        this.G__10452 = object3;
        this.G__10458 = object4;
        this.old_frame__5445__auto__ = object5;
        this.G__10451 = object6;
        this.G__10453 = object7;
        this.G__10456 = object8;
        this.G__10459 = object9;
        this.G__10457 = object10;
        this.G__10455 = object11;
    }

    @Override
    public Object invoke() {
        Object object;
        try {
            Object result__5447__auto__10634;
            Var.resetThreadBindingFrame(((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__1));
            do {
                Object object2;
                int G__10598 = RT.intCast(((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__3));
                switch (G__10598) {
                    case 1: {
                        Object state_10575;
                        Object inst_10484 = ((IFn)this.G__10451).invoke();
                        Object inst_10485 = ((IFn)this.G__10452).invoke();
                        Object inst_10486 = ((IFn)this.G__10453).invoke();
                        Object inst_10487 = ((IFn)this.G__10454).invoke();
                        Object inst_10488 = ((IFn)this.G__10455).invoke();
                        Object inst_10489 = ((IFn)this.G__10456).invoke();
                        Object inst_10490 = ((IFn)this.G__10457).invoke();
                        Object inst_10491 = ((IFn)this.G__10458).invoke();
                        Object inst_10492 = ((IFn)this.G__10459).invoke();
                        Object object3 = inst_10484;
                        inst_10484 = null;
                        Object inst_10493 = object3;
                        Object object4 = inst_10485;
                        inst_10485 = null;
                        Object inst_10494 = object4;
                        Object object5 = inst_10486;
                        inst_10486 = null;
                        Object inst_10495 = object5;
                        Object object6 = inst_10487;
                        inst_10487 = null;
                        Object inst_10496 = object6;
                        Object object7 = inst_10488;
                        inst_10488 = null;
                        Object inst_10497 = object7;
                        Object object8 = inst_10489;
                        inst_10489 = null;
                        Object inst_10498 = object8;
                        Object object9 = inst_10490;
                        inst_10490 = null;
                        Object inst_10499 = object9;
                        Object object10 = inst_10491;
                        inst_10491 = null;
                        Object inst_10500 = object10;
                        Object object11 = inst_10492;
                        inst_10492 = null;
                        Object inst_10501 = object11;
                        Object statearr_10599 = this.state_10575;
                        Object object12 = inst_10497;
                        inst_10497 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__6, object12);
                        Object object13 = inst_10496;
                        inst_10496 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__7, object13);
                        Object object14 = inst_10500;
                        inst_10500 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__8, object14);
                        Object object15 = inst_10494;
                        inst_10494 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__9, object15);
                        Object object16 = inst_10495;
                        inst_10495 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__10, object16);
                        Object object17 = inst_10498;
                        inst_10498 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__11, object17);
                        Object object18 = inst_10493;
                        inst_10493 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__12, object18);
                        Object object19 = inst_10501;
                        inst_10501 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__13, object19);
                        Object object20 = inst_10499;
                        inst_10499 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10599, const__14, object20);
                        Object object21 = statearr_10599;
                        statearr_10599 = null;
                        Object object22 = state_10575 = object21;
                        state_10575 = null;
                        Object statearr_10600 = object22;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10600, const__15, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10600, const__3, const__15);
                        object2 = const__16;
                        break;
                    }
                    case 2: {
                        Object state_10575;
                        Object inst_10497 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__6);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__18);
                        Object inst_10493 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__12);
                        Object object23 = inst_10497;
                        inst_10497 = null;
                        Object inst_10503 = ((IFn)const__19.getRawRoot()).invoke(object23, Boolean.FALSE);
                        Object object24 = inst_10493;
                        inst_10493 = null;
                        Object inst_10504 = ((IFn.LO)const__20.getRawRoot()).invokePrim(RT.longCast((Number)object24));
                        Object object25 = inst_10498;
                        inst_10498 = null;
                        Object inst_10505 = ((IFn)const__21.getRawRoot()).invoke(inst_10504, object25);
                        Object statearr_10601 = this.state_10575;
                        Object object26 = inst_10503;
                        inst_10503 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10601, const__22, object26);
                        Object object27 = inst_10504;
                        inst_10504 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10601, const__18, object27);
                        Object object28 = statearr_10601;
                        statearr_10601 = null;
                        Object object29 = state_10575 = object28;
                        state_10575 = null;
                        Object object30 = inst_10505;
                        inst_10505 = null;
                        object2 = ((IFn)const__23.getRawRoot()).invoke(object29, const__24, object30);
                        break;
                    }
                    case 3: {
                        Object inst_10573;
                        Object object31 = inst_10573 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        inst_10573 = null;
                        object2 = ((IFn)const__26.getRawRoot()).invoke(this.state_10575, object31);
                        break;
                    }
                    case 4: {
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__28);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        Object inst_10504 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__18);
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__29);
                        Object inst_10507 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object inst_10508 = RT.nth(inst_10507, RT.intCast(0L), null);
                        Object inst_10509 = RT.nth(inst_10507, RT.intCast(1L), null);
                        Object object32 = inst_10504;
                        inst_10504 = null;
                        Object inst_10510 = object32;
                        Object object33 = inst_10498;
                        inst_10498 = null;
                        Object inst_10511 = object33;
                        Object inst_10512 = inst_10507;
                        Object object34 = inst_10508;
                        inst_10508 = null;
                        Object inst_10513 = object34;
                        Object object35 = inst_10509;
                        inst_10509 = null;
                        Object inst_10514 = object35;
                        Object object36 = inst_10507;
                        inst_10507 = null;
                        Object inst_10515 = object36;
                        boolean inst_10516 = Util.equiv(inst_10514, inst_10510);
                        Object statearr_10602 = this.state_10575;
                        Object object37 = inst_10510;
                        inst_10510 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10602, const__28, object37);
                        Object object38 = inst_10513;
                        inst_10513 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10602, const__31, object38);
                        Object object39 = inst_10512;
                        inst_10512 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10602, const__32, object39);
                        Object object40 = inst_10515;
                        inst_10515 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10602, const__33, object40);
                        Object object41 = inst_10511;
                        inst_10511 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10602, const__34, object41);
                        Object object42 = inst_10514;
                        inst_10514 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10602, const__29, object42);
                        Object object43 = statearr_10602;
                        statearr_10602 = null;
                        Object state_10575 = object43;
                        if (inst_10516) {
                            Object object44 = state_10575;
                            state_10575 = null;
                            Object statearr_10603 = object44;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10603, const__3, const__35);
                        } else {
                            Object object45 = state_10575;
                            state_10575 = null;
                            Object statearr_10604 = object45;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10604, const__3, const__36);
                        }
                        object2 = const__16;
                        break;
                    }
                    case 5: {
                        Object inst_10518;
                        Object inst_10497;
                        Object object46 = inst_10497 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__6);
                        inst_10497 = null;
                        Object object47 = inst_10518 = ((IFn)const__38.getRawRoot()).invoke(object46);
                        inst_10518 = null;
                        if (object47 != null && object47 != Boolean.FALSE) {
                            Object statearr_10605 = this.state_10575;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10605, const__3, const__7);
                        } else {
                            Object statearr_10606 = this.state_10575;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10606, const__3, const__8);
                        }
                        object2 = const__16;
                        break;
                    }
                    case 6: {
                        Object object48;
                        Object inst_10497 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__6);
                        Object inst_10496 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__7);
                        Object inst_10500 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__8);
                        Object inst_10494 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__9);
                        Object inst_10495 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__10);
                        Object inst_10510 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__28);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        Object inst_10493 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__12);
                        Object inst_10513 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__31);
                        Object inst_10512 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__32);
                        Object inst_10515 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__33);
                        Object inst_10511 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__34);
                        Object inst_10501 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__13);
                        Object inst_10514 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__29);
                        Object inst_10499 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__14);
                        inst_10494 = null;
                        inst_10498 = null;
                        inst_10501 = null;
                        inst_10496 = null;
                        inst_10493 = null;
                        inst_10495 = null;
                        Object object49 = inst_10511;
                        inst_10511 = null;
                        Object G__10463 = object49;
                        inst_10512 = null;
                        inst_10515 = null;
                        inst_10499 = null;
                        Object object50 = inst_10513;
                        inst_10513 = null;
                        Object val__5616__auto__10631 = object50;
                        inst_10500 = null;
                        inst_10510 = null;
                        inst_10497 = null;
                        Object object51 = inst_10514;
                        inst_10514 = null;
                        Object ch10464 = object51;
                        Object object52 = G__10463;
                        G__10463 = null;
                        if (Util.equiv(ch10464, object52)) {
                            object48 = null;
                        } else {
                            Object object53 = ch10464;
                            ch10464 = null;
                            if (Util.equiv(object53, (Object)const__41)) {
                                object48 = val__5616__auto__10631;
                                val__5616__auto__10631 = null;
                            } else {
                                object48 = null;
                            }
                        }
                        Object inst_10569 = object48;
                        Object statearr_10607 = this.state_10575;
                        Object object54 = inst_10569;
                        inst_10569 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10607, const__15, object54);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10607, const__3, const__6);
                        object2 = const__16;
                        break;
                    }
                    case 7: {
                        Object inst_10571 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object statearr_10608 = this.state_10575;
                        Object object55 = inst_10571;
                        inst_10571 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10608, const__15, object55);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10608, const__3, const__1);
                        object2 = const__16;
                        break;
                    }
                    case 8: {
                        Object statearr_10609 = this.state_10575;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10609, const__15, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10609, const__3, const__15);
                        object2 = const__16;
                        break;
                    }
                    case 9: {
                        Object inst_10523;
                        Object state_10575;
                        Object inst_10500 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__8);
                        Object inst_10494 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__9);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__45);
                        Object object56 = inst_10500;
                        inst_10500 = null;
                        Object inst_10522 = ((IFn)object56).invoke();
                        Object object57 = inst_10494;
                        inst_10494 = null;
                        Object object58 = inst_10523 = ((IFn)const__21.getRawRoot()).invoke(object57, inst_10522);
                        inst_10523 = null;
                        Object object59 = inst_10498;
                        inst_10498 = null;
                        Object inst_10524 = ((IFn)const__21.getRawRoot()).invoke(object58, object59);
                        Object statearr_10610 = this.state_10575;
                        Object object60 = inst_10522;
                        inst_10522 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10610, const__45, object60);
                        Object object61 = statearr_10610;
                        statearr_10610 = null;
                        Object object62 = state_10575 = object61;
                        state_10575 = null;
                        Object object63 = inst_10524;
                        inst_10524 = null;
                        object2 = ((IFn)const__23.getRawRoot()).invoke(object62, const__10, object63);
                        break;
                    }
                    case 10: {
                        Object inst_10567 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object statearr_10611 = this.state_10575;
                        Object object64 = inst_10567;
                        inst_10567 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10611, const__15, object64);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10611, const__3, const__6);
                        object2 = const__16;
                        break;
                    }
                    case 11: {
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__48);
                        Object inst_10494 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__9);
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__49);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        Object inst_10522 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__45);
                        Object inst_10526 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object inst_10527 = RT.nth(inst_10526, RT.intCast(0L), null);
                        Object inst_10528 = RT.nth(inst_10526, RT.intCast(1L), null);
                        Object object65 = inst_10494;
                        inst_10494 = null;
                        Object inst_10529 = object65;
                        Object object66 = inst_10522;
                        inst_10522 = null;
                        Object inst_10530 = object66;
                        Object object67 = inst_10498;
                        inst_10498 = null;
                        Object inst_10531 = object67;
                        Object inst_10532 = inst_10526;
                        Object object68 = inst_10527;
                        inst_10527 = null;
                        Object inst_10533 = object68;
                        Object object69 = inst_10528;
                        inst_10528 = null;
                        Object inst_10534 = object69;
                        Object object70 = inst_10526;
                        inst_10526 = null;
                        Object inst_10535 = object70;
                        boolean inst_10536 = Util.equiv(inst_10534, inst_10529);
                        Object statearr_10612 = this.state_10575;
                        Object object71 = inst_10535;
                        inst_10535 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10612, const__50, object71);
                        Object object72 = inst_10533;
                        inst_10533 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10612, const__51, object72);
                        Object object73 = inst_10530;
                        inst_10530 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10612, const__52, object73);
                        Object object74 = inst_10529;
                        inst_10529 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10612, const__48, object74);
                        Object object75 = inst_10532;
                        inst_10532 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10612, const__53, object75);
                        Object object76 = inst_10534;
                        inst_10534 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10612, const__49, object76);
                        Object object77 = inst_10531;
                        inst_10531 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10612, const__54, object77);
                        Object object78 = statearr_10612;
                        statearr_10612 = null;
                        Object state_10575 = object78;
                        if (inst_10536) {
                            Object object79 = state_10575;
                            state_10575 = null;
                            Object statearr_10613 = object79;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10613, const__3, const__11);
                        } else {
                            Object object80 = state_10575;
                            state_10575 = null;
                            Object statearr_10614 = object80;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10614, const__3, const__12);
                        }
                        object2 = const__16;
                        break;
                    }
                    case 12: {
                        Object state_10575;
                        Object inst_10501;
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__56);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        Object object81 = inst_10501 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__13);
                        inst_10501 = null;
                        Object inst_10538 = ((IFn.LO)const__20.getRawRoot()).invokePrim(RT.longCast((Number)object81));
                        Object object82 = inst_10498;
                        inst_10498 = null;
                        Object inst_10539 = ((IFn)const__21.getRawRoot()).invoke(inst_10538, object82);
                        Object statearr_10615 = this.state_10575;
                        Object object83 = inst_10538;
                        inst_10538 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10615, const__56, object83);
                        Object object84 = statearr_10615;
                        statearr_10615 = null;
                        Object object85 = state_10575 = object84;
                        state_10575 = null;
                        Object object86 = inst_10539;
                        inst_10539 = null;
                        object2 = ((IFn)const__23.getRawRoot()).invoke(object85, const__14, object86);
                        break;
                    }
                    case 13: {
                        Object object87;
                        Object inst_10497 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__6);
                        Object inst_10496 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__7);
                        Object inst_10535 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__50);
                        Object inst_10500 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__8);
                        Object inst_10533 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__51);
                        Object inst_10530 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__52);
                        Object inst_10529 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__48);
                        Object inst_10494 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__9);
                        Object inst_10532 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__53);
                        Object inst_10495 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__10);
                        Object inst_10534 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__49);
                        Object inst_10510 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__28);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        Object inst_10493 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__12);
                        Object inst_10512 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__32);
                        Object inst_10515 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__33);
                        Object inst_10511 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__34);
                        Object inst_10531 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__54);
                        Object inst_10501 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__13);
                        Object inst_10514 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__29);
                        Object inst_10499 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__14);
                        inst_10535 = null;
                        inst_10494 = null;
                        inst_10498 = null;
                        inst_10532 = null;
                        inst_10501 = null;
                        inst_10496 = null;
                        inst_10493 = null;
                        Object object88 = inst_10533;
                        inst_10533 = null;
                        Object val__5616__auto__10632 = object88;
                        inst_10530 = null;
                        inst_10495 = null;
                        inst_10511 = null;
                        inst_10512 = null;
                        inst_10515 = null;
                        inst_10499 = null;
                        inst_10500 = null;
                        inst_10529 = null;
                        Object object89 = inst_10534;
                        inst_10534 = null;
                        Object ch10472 = object89;
                        inst_10510 = null;
                        inst_10497 = null;
                        Object object90 = inst_10531;
                        inst_10531 = null;
                        Object G__10471 = object90;
                        inst_10514 = null;
                        Object object91 = G__10471;
                        G__10471 = null;
                        if (Util.equiv(ch10472, object91)) {
                            object87 = null;
                        } else {
                            Object object92 = ch10472;
                            ch10472 = null;
                            if (Util.equiv(object92, (Object)const__41)) {
                                object87 = val__5616__auto__10632;
                                val__5616__auto__10632 = null;
                            } else {
                                object87 = null;
                            }
                        }
                        Object inst_10563 = object87;
                        Object statearr_10616 = this.state_10575;
                        Object object93 = inst_10563;
                        inst_10563 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10616, const__15, object93);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10616, const__3, const__13);
                        object2 = const__16;
                        break;
                    }
                    case 14: {
                        Object inst_10565 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object statearr_10617 = this.state_10575;
                        Object object94 = inst_10565;
                        inst_10565 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10617, const__15, object94);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10617, const__3, const__9);
                        object2 = const__16;
                        break;
                    }
                    case 15: {
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__60);
                        Object inst_10538 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__56);
                        ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__61);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        Object inst_10541 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object inst_10542 = RT.nth(inst_10541, RT.intCast(0L), null);
                        Object inst_10543 = RT.nth(inst_10541, RT.intCast(1L), null);
                        Object object95 = inst_10538;
                        inst_10538 = null;
                        Object inst_10544 = object95;
                        Object object96 = inst_10498;
                        inst_10498 = null;
                        Object inst_10545 = object96;
                        Object inst_10546 = inst_10541;
                        Object object97 = inst_10542;
                        inst_10542 = null;
                        Object inst_10547 = object97;
                        Object object98 = inst_10543;
                        inst_10543 = null;
                        Object inst_10548 = object98;
                        Object object99 = inst_10541;
                        inst_10541 = null;
                        Object inst_10549 = object99;
                        boolean inst_10550 = Util.equiv(inst_10548, inst_10544);
                        Object statearr_10618 = this.state_10575;
                        Object object100 = inst_10544;
                        inst_10544 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10618, const__60, object100);
                        Object object101 = inst_10545;
                        inst_10545 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10618, const__62, object101);
                        Object object102 = inst_10548;
                        inst_10548 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10618, const__61, object102);
                        Object object103 = inst_10549;
                        inst_10549 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10618, const__63, object103);
                        Object object104 = inst_10546;
                        inst_10546 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10618, const__64, object104);
                        Object object105 = inst_10547;
                        inst_10547 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10618, const__65, object105);
                        Object object106 = statearr_10618;
                        statearr_10618 = null;
                        Object state_10575 = object106;
                        if (inst_10550) {
                            Object object107 = state_10575;
                            state_10575 = null;
                            Object statearr_10619 = object107;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10619, const__3, const__28);
                        } else {
                            Object object108 = state_10575;
                            state_10575 = null;
                            Object statearr_10620 = object108;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10620, const__3, const__18);
                        }
                        object2 = const__16;
                        break;
                    }
                    case 16: {
                        Object inst_10552;
                        Object inst_10497;
                        Object object109 = inst_10497 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__6);
                        inst_10497 = null;
                        Object object110 = inst_10552 = ((IFn)const__38.getRawRoot()).invoke(object109);
                        inst_10552 = null;
                        if (object110 != null && object110 != Boolean.FALSE) {
                            Object statearr_10621 = this.state_10575;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10621, const__3, const__31);
                        } else {
                            Object statearr_10622 = this.state_10575;
                            ((IFn)const__5.getRawRoot()).invoke(statearr_10622, const__3, const__32);
                        }
                        object2 = const__16;
                        break;
                    }
                    case 17: {
                        Object object111;
                        Object inst_10544 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__60);
                        Object inst_10497 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__6);
                        Object inst_10496 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__7);
                        Object inst_10545 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__62);
                        Object inst_10535 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__50);
                        Object inst_10548 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__61);
                        Object inst_10500 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__8);
                        Object inst_10530 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__52);
                        Object inst_10529 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__48);
                        Object inst_10494 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__9);
                        Object inst_10532 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__53);
                        Object inst_10549 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__63);
                        Object inst_10495 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__10);
                        Object inst_10546 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__64);
                        Object inst_10534 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__49);
                        Object inst_10510 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__28);
                        Object inst_10498 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__11);
                        Object inst_10493 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__12);
                        Object inst_10512 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__32);
                        Object inst_10515 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__33);
                        Object inst_10511 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__34);
                        Object inst_10531 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__54);
                        Object inst_10501 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__13);
                        Object inst_10514 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__29);
                        Object inst_10499 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__14);
                        Object inst_10547 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__65);
                        inst_10535 = null;
                        inst_10549 = null;
                        inst_10494 = null;
                        inst_10498 = null;
                        inst_10532 = null;
                        inst_10501 = null;
                        inst_10496 = null;
                        Object object112 = inst_10548;
                        inst_10548 = null;
                        Object ch10479 = object112;
                        inst_10546 = null;
                        inst_10493 = null;
                        Object object113 = inst_10547;
                        inst_10547 = null;
                        Object val__5616__auto__10633 = object113;
                        inst_10530 = null;
                        inst_10495 = null;
                        inst_10511 = null;
                        inst_10512 = null;
                        inst_10515 = null;
                        inst_10499 = null;
                        Object object114 = inst_10545;
                        inst_10545 = null;
                        Object G__10478 = object114;
                        inst_10500 = null;
                        inst_10529 = null;
                        inst_10534 = null;
                        inst_10510 = null;
                        inst_10497 = null;
                        inst_10531 = null;
                        inst_10514 = null;
                        inst_10544 = null;
                        Object object115 = G__10478;
                        G__10478 = null;
                        if (Util.equiv(ch10479, object115)) {
                            object111 = null;
                        } else {
                            Object object116 = ch10479;
                            ch10479 = null;
                            if (Util.equiv(object116, (Object)const__41)) {
                                object111 = val__5616__auto__10633;
                                val__5616__auto__10633 = null;
                            } else {
                                object111 = null;
                            }
                        }
                        Object inst_10559 = object111;
                        Object statearr_10623 = this.state_10575;
                        Object object117 = inst_10559;
                        inst_10559 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10623, const__15, object117);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10623, const__3, const__29);
                        object2 = const__16;
                        break;
                    }
                    case 18: {
                        Object inst_10561 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object statearr_10624 = this.state_10575;
                        Object object118 = inst_10561;
                        inst_10561 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10624, const__15, object118);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10624, const__3, const__13);
                        object2 = const__16;
                        break;
                    }
                    case 19: {
                        Object statearr_10625 = this.state_10575;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10625, const__15, null);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10625, const__3, const__15);
                        object2 = const__16;
                        break;
                    }
                    case 20: {
                        Object statearr_10626 = this.state_10575;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10626, const__15, const__71);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10626, const__3, const__33);
                        object2 = const__16;
                        break;
                    }
                    case 21: {
                        Object inst_10557 = ((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__15);
                        Object statearr_10627 = this.state_10575;
                        Object object119 = inst_10557;
                        inst_10557 = null;
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10627, const__15, object119);
                        ((IFn)const__5.getRawRoot()).invoke(statearr_10627, const__3, const__29);
                        object2 = const__16;
                        break;
                    }
                    default: {
                        throw (Throwable)new IllegalArgumentException((String)((IFn)const__73.getRawRoot()).invoke("No matching clause: ", G__10598));
                    }
                }
                result__5447__auto__10634 = object2;
            } while (Util.identical(result__5447__auto__10634, const__16));
            Object object120 = result__5447__auto__10634;
            result__5447__auto__10634 = null;
            object = object120;
        }
        catch (Throwable ex__5448__auto__2) {
            Object statearr_10628 = this.state_10575;
            ((IFn)const__5.getRawRoot()).invoke(statearr_10628, const__15, ex__5448__auto__2);
            Object object121 = ((IFn)const__75.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__24));
            if (object121 == null || object121 == Boolean.FALSE) {
                Object ex__5448__auto__2 = null;
                throw ex__5448__auto__2;
            }
            Object statearr_10629 = this.state_10575;
            ((IFn)const__5.getRawRoot()).invoke(statearr_10629, const__3, ((IFn)const__76.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__24)));
            this.state_10575 = null;
            ((IFn)const__5.getRawRoot()).invoke(statearr_10629, const__24, ((IFn)const__77.getRawRoot()).invoke(((IFn)const__0.getRawRoot()).invoke(this.state_10575, const__24)));
            object = const__16;
        }
        finally {
            this.old_frame__5445__auto__ = null;
            Var.resetThreadBindingFrame(this.old_frame__5445__auto__);
        }
        return object;
    }
}

