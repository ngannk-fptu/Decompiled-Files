/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13062__13615;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13063__13617;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13064__13619;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13065__13621;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13066__13623;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13067__13625;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13068__13627;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13069__13629;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13070__13631;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13071__13633;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13072__13635;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13073__13637;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13074__13639;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13075__13641;
import ginga.async.multiplex$run_receive_process$fn__13614$G__13076__13643;
import ginga.async.multiplex$run_receive_process$fn__13614$state_machine__5444__auto____13645;

public final class multiplex$run_receive_process$fn__13614
extends AFunction {
    Object outc_by_id;
    Object map__13061;
    Object inc_by_id;
    Object win_opts;
    Object captured_bindings__5668__auto__;
    Object p__13059;
    Object m;
    Object max_in_connections;
    Object c__5667__auto__;
    Object opts;
    Object on_error;
    Object msg_size;
    Object upstream;
    Object accept;
    Object downstream;
    Object task_ch;
    Object map__13060;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public multiplex$run_receive_process$fn__13614(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11, Object object12, Object object13, Object object14, Object object15, Object object16, Object object17) {
        this.outc_by_id = object;
        this.map__13061 = object2;
        this.inc_by_id = object3;
        this.win_opts = object4;
        this.captured_bindings__5668__auto__ = object5;
        this.p__13059 = object6;
        this.m = object7;
        this.max_in_connections = object8;
        this.c__5667__auto__ = object9;
        this.opts = object10;
        this.on_error = object11;
        this.msg_size = object12;
        this.upstream = object13;
        this.accept = object14;
        this.downstream = object15;
        this.task_ch = object16;
        this.map__13060 = object17;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__14555;
        multiplex$run_receive_process$fn__13614$state_machine__5444__auto____13645 f__5669__auto__14554;
        this_.on_error = null;
        multiplex$run_receive_process$fn__13614$G__13062__13615 G__13062 = new multiplex$run_receive_process$fn__13614$G__13062__13615(this_.on_error);
        this_.upstream = null;
        multiplex$run_receive_process$fn__13614$G__13063__13617 G__13063 = new multiplex$run_receive_process$fn__13614$G__13063__13617(this_.upstream);
        this_.map__13060 = null;
        multiplex$run_receive_process$fn__13614$G__13064__13619 G__13064 = new multiplex$run_receive_process$fn__13614$G__13064__13619(this_.map__13060);
        this_.msg_size = null;
        multiplex$run_receive_process$fn__13614$G__13065__13621 G__13065 = new multiplex$run_receive_process$fn__13614$G__13065__13621(this_.msg_size);
        this_.map__13061 = null;
        multiplex$run_receive_process$fn__13614$G__13066__13623 G__13066 = new multiplex$run_receive_process$fn__13614$G__13066__13623(this_.map__13061);
        this_.accept = null;
        multiplex$run_receive_process$fn__13614$G__13067__13625 G__13067 = new multiplex$run_receive_process$fn__13614$G__13067__13625(this_.accept);
        this_.downstream = null;
        multiplex$run_receive_process$fn__13614$G__13068__13627 G__13068 = new multiplex$run_receive_process$fn__13614$G__13068__13627(this_.downstream);
        this_.p__13059 = null;
        multiplex$run_receive_process$fn__13614$G__13069__13629 G__13069 = new multiplex$run_receive_process$fn__13614$G__13069__13629(this_.p__13059);
        this_.win_opts = null;
        multiplex$run_receive_process$fn__13614$G__13070__13631 G__13070 = new multiplex$run_receive_process$fn__13614$G__13070__13631(this_.win_opts);
        this_.task_ch = null;
        multiplex$run_receive_process$fn__13614$G__13071__13633 G__13071 = new multiplex$run_receive_process$fn__13614$G__13071__13633(this_.task_ch);
        this_.outc_by_id = null;
        multiplex$run_receive_process$fn__13614$G__13072__13635 G__13072 = new multiplex$run_receive_process$fn__13614$G__13072__13635(this_.outc_by_id);
        this_.m = null;
        multiplex$run_receive_process$fn__13614$G__13073__13637 G__13073 = new multiplex$run_receive_process$fn__13614$G__13073__13637(this_.m);
        this_.max_in_connections = null;
        multiplex$run_receive_process$fn__13614$G__13074__13639 G__13074 = new multiplex$run_receive_process$fn__13614$G__13074__13639(this_.max_in_connections);
        this_.inc_by_id = null;
        multiplex$run_receive_process$fn__13614$G__13075__13641 G__13075 = new multiplex$run_receive_process$fn__13614$G__13075__13641(this_.inc_by_id);
        this_.opts = null;
        multiplex$run_receive_process$fn__13614$G__13076__13643 G__13076 = new multiplex$run_receive_process$fn__13614$G__13076__13643(this_.opts);
        multiplex$run_receive_process$fn__13614$G__13067__13625 multiplex$run_receive_process$fn__13614$G__13067__13625 = G__13067;
        G__13067 = null;
        multiplex$run_receive_process$fn__13614$G__13068__13627 multiplex$run_receive_process$fn__13614$G__13068__13627 = G__13068;
        G__13068 = null;
        multiplex$run_receive_process$fn__13614$G__13064__13619 multiplex$run_receive_process$fn__13614$G__13064__13619 = G__13064;
        G__13064 = null;
        multiplex$run_receive_process$fn__13614$G__13063__13617 multiplex$run_receive_process$fn__13614$G__13063__13617 = G__13063;
        G__13063 = null;
        multiplex$run_receive_process$fn__13614$G__13075__13641 multiplex$run_receive_process$fn__13614$G__13075__13641 = G__13075;
        G__13075 = null;
        multiplex$run_receive_process$fn__13614$G__13069__13629 multiplex$run_receive_process$fn__13614$G__13069__13629 = G__13069;
        G__13069 = null;
        multiplex$run_receive_process$fn__13614$G__13074__13639 multiplex$run_receive_process$fn__13614$G__13074__13639 = G__13074;
        G__13074 = null;
        multiplex$run_receive_process$fn__13614$G__13073__13637 multiplex$run_receive_process$fn__13614$G__13073__13637 = G__13073;
        G__13073 = null;
        multiplex$run_receive_process$fn__13614$G__13062__13615 multiplex$run_receive_process$fn__13614$G__13062__13615 = G__13062;
        G__13062 = null;
        multiplex$run_receive_process$fn__13614$G__13066__13623 multiplex$run_receive_process$fn__13614$G__13066__13623 = G__13066;
        G__13066 = null;
        multiplex$run_receive_process$fn__13614$G__13070__13631 multiplex$run_receive_process$fn__13614$G__13070__13631 = G__13070;
        G__13070 = null;
        multiplex$run_receive_process$fn__13614$G__13076__13643 multiplex$run_receive_process$fn__13614$G__13076__13643 = G__13076;
        G__13076 = null;
        multiplex$run_receive_process$fn__13614$G__13071__13633 multiplex$run_receive_process$fn__13614$G__13071__13633 = G__13071;
        G__13071 = null;
        multiplex$run_receive_process$fn__13614$G__13065__13621 multiplex$run_receive_process$fn__13614$G__13065__13621 = G__13065;
        G__13065 = null;
        multiplex$run_receive_process$fn__13614$G__13072__13635 multiplex$run_receive_process$fn__13614$G__13072__13635 = G__13072;
        G__13072 = null;
        multiplex$run_receive_process$fn__13614$state_machine__5444__auto____13645 multiplex$run_receive_process$fn__13614$state_machine__5444__auto____13645 = f__5669__auto__14554 = new multiplex$run_receive_process$fn__13614$state_machine__5444__auto____13645(multiplex$run_receive_process$fn__13614$G__13067__13625, multiplex$run_receive_process$fn__13614$G__13068__13627, multiplex$run_receive_process$fn__13614$G__13064__13619, multiplex$run_receive_process$fn__13614$G__13063__13617, multiplex$run_receive_process$fn__13614$G__13075__13641, multiplex$run_receive_process$fn__13614$G__13069__13629, multiplex$run_receive_process$fn__13614$G__13074__13639, multiplex$run_receive_process$fn__13614$G__13073__13637, multiplex$run_receive_process$fn__13614$G__13062__13615, multiplex$run_receive_process$fn__13614$G__13066__13623, multiplex$run_receive_process$fn__13614$G__13070__13631, multiplex$run_receive_process$fn__13614$G__13076__13643, multiplex$run_receive_process$fn__13614$G__13071__13633, multiplex$run_receive_process$fn__13614$G__13065__13621, multiplex$run_receive_process$fn__13614$G__13072__13635);
        f__5669__auto__14554 = null;
        Object statearr_14552 = ((IFn)multiplex$run_receive_process$fn__13614$state_machine__5444__auto____13645).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_14552, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_14552, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_14552;
        statearr_14552 = null;
        Object object2 = state__5670__auto__14555 = object;
        state__5670__auto__14555 = null;
        multiplex$run_receive_process$fn__13614 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

