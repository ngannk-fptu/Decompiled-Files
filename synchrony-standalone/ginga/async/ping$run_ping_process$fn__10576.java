/*
 * Decompiled with CFR 0.152.
 */
package ginga.async;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Var;
import ginga.async.ping$run_ping_process$fn__10576$G__10451__10577;
import ginga.async.ping$run_ping_process$fn__10576$G__10452__10579;
import ginga.async.ping$run_ping_process$fn__10576$G__10453__10581;
import ginga.async.ping$run_ping_process$fn__10576$G__10454__10583;
import ginga.async.ping$run_ping_process$fn__10576$G__10455__10585;
import ginga.async.ping$run_ping_process$fn__10576$G__10456__10587;
import ginga.async.ping$run_ping_process$fn__10576$G__10457__10589;
import ginga.async.ping$run_ping_process$fn__10576$G__10458__10591;
import ginga.async.ping$run_ping_process$fn__10576$G__10459__10593;
import ginga.async.ping$run_ping_process$fn__10576$state_machine__5444__auto____10595;

public final class ping$run_ping_process$fn__10576
extends AFunction {
    Object p__10449;
    Object alive;
    Object ping_opts;
    Object captured_bindings__5668__auto__;
    Object c__5667__auto__;
    Object close_promise;
    Object ping_timeout_ms;
    Object ping_msg;
    Object upstream;
    Object map__10450;
    Object pong_timeout_ms;
    public static final Var const__0 = RT.var("clojure.core.async.impl.ioc-macros", "aset-object");
    public static final Object const__1 = 6L;
    public static final Object const__2 = 3L;
    public static final Var const__3 = RT.var("clojure.core.async.impl.ioc-macros", "run-state-machine-wrapped");

    public ping$run_ping_process$fn__10576(Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11) {
        this.p__10449 = object;
        this.alive = object2;
        this.ping_opts = object3;
        this.captured_bindings__5668__auto__ = object4;
        this.c__5667__auto__ = object5;
        this.close_promise = object6;
        this.ping_timeout_ms = object7;
        this.ping_msg = object8;
        this.upstream = object9;
        this.map__10450 = object10;
        this.pong_timeout_ms = object11;
    }

    @Override
    public Object invoke() {
        Object state__5670__auto__10641;
        ping$run_ping_process$fn__10576$state_machine__5444__auto____10595 f__5669__auto__10640;
        this_.ping_timeout_ms = null;
        ping$run_ping_process$fn__10576$G__10451__10577 G__10451 = new ping$run_ping_process$fn__10576$G__10451__10577(this_.ping_timeout_ms);
        this_.upstream = null;
        ping$run_ping_process$fn__10576$G__10452__10579 G__10452 = new ping$run_ping_process$fn__10576$G__10452__10579(this_.upstream);
        this_.ping_opts = null;
        ping$run_ping_process$fn__10576$G__10453__10581 G__10453 = new ping$run_ping_process$fn__10576$G__10453__10581(this_.ping_opts);
        this_.map__10450 = null;
        ping$run_ping_process$fn__10576$G__10454__10583 G__10454 = new ping$run_ping_process$fn__10576$G__10454__10583(this_.map__10450);
        this_.alive = null;
        ping$run_ping_process$fn__10576$G__10455__10585 G__10455 = new ping$run_ping_process$fn__10576$G__10455__10585(this_.alive);
        this_.close_promise = null;
        ping$run_ping_process$fn__10576$G__10456__10587 G__10456 = new ping$run_ping_process$fn__10576$G__10456__10587(this_.close_promise);
        this_.p__10449 = null;
        ping$run_ping_process$fn__10576$G__10457__10589 G__10457 = new ping$run_ping_process$fn__10576$G__10457__10589(this_.p__10449);
        this_.ping_msg = null;
        ping$run_ping_process$fn__10576$G__10458__10591 G__10458 = new ping$run_ping_process$fn__10576$G__10458__10591(this_.ping_msg);
        this_.pong_timeout_ms = null;
        ping$run_ping_process$fn__10576$G__10459__10593 G__10459 = new ping$run_ping_process$fn__10576$G__10459__10593(this_.pong_timeout_ms);
        ping$run_ping_process$fn__10576$G__10454__10583 ping$run_ping_process$fn__10576$G__10454__10583 = G__10454;
        G__10454 = null;
        ping$run_ping_process$fn__10576$G__10452__10579 ping$run_ping_process$fn__10576$G__10452__10579 = G__10452;
        G__10452 = null;
        ping$run_ping_process$fn__10576$G__10458__10591 ping$run_ping_process$fn__10576$G__10458__10591 = G__10458;
        G__10458 = null;
        ping$run_ping_process$fn__10576$G__10451__10577 ping$run_ping_process$fn__10576$G__10451__10577 = G__10451;
        G__10451 = null;
        ping$run_ping_process$fn__10576$G__10453__10581 ping$run_ping_process$fn__10576$G__10453__10581 = G__10453;
        G__10453 = null;
        ping$run_ping_process$fn__10576$G__10456__10587 ping$run_ping_process$fn__10576$G__10456__10587 = G__10456;
        G__10456 = null;
        ping$run_ping_process$fn__10576$G__10459__10593 ping$run_ping_process$fn__10576$G__10459__10593 = G__10459;
        G__10459 = null;
        ping$run_ping_process$fn__10576$G__10457__10589 ping$run_ping_process$fn__10576$G__10457__10589 = G__10457;
        G__10457 = null;
        ping$run_ping_process$fn__10576$G__10455__10585 ping$run_ping_process$fn__10576$G__10455__10585 = G__10455;
        G__10455 = null;
        ping$run_ping_process$fn__10576$state_machine__5444__auto____10595 ping$run_ping_process$fn__10576$state_machine__5444__auto____10595 = f__5669__auto__10640 = new ping$run_ping_process$fn__10576$state_machine__5444__auto____10595(ping$run_ping_process$fn__10576$G__10454__10583, ping$run_ping_process$fn__10576$G__10452__10579, ping$run_ping_process$fn__10576$G__10458__10591, ping$run_ping_process$fn__10576$G__10451__10577, ping$run_ping_process$fn__10576$G__10453__10581, ping$run_ping_process$fn__10576$G__10456__10587, ping$run_ping_process$fn__10576$G__10459__10593, ping$run_ping_process$fn__10576$G__10457__10589, ping$run_ping_process$fn__10576$G__10455__10585);
        f__5669__auto__10640 = null;
        Object statearr_10638 = ((IFn)ping$run_ping_process$fn__10576$state_machine__5444__auto____10595).invoke();
        this_.c__5667__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_10638, const__1, this_.c__5667__auto__);
        this_.captured_bindings__5668__auto__ = null;
        ((IFn)const__0.getRawRoot()).invoke(statearr_10638, const__2, this_.captured_bindings__5668__auto__);
        Object object = statearr_10638;
        statearr_10638 = null;
        Object object2 = state__5670__auto__10641 = object;
        state__5670__auto__10641 = null;
        ping$run_ping_process$fn__10576 this_ = null;
        return ((IFn)const__3.getRawRoot()).invoke(object2);
    }
}

