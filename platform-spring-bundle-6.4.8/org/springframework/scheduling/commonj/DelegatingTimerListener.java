/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  commonj.timers.Timer
 *  commonj.timers.TimerListener
 */
package org.springframework.scheduling.commonj;

import commonj.timers.Timer;
import commonj.timers.TimerListener;
import org.springframework.util.Assert;

@Deprecated
public class DelegatingTimerListener
implements TimerListener {
    private final Runnable runnable;

    public DelegatingTimerListener(Runnable runnable) {
        Assert.notNull((Object)runnable, "Runnable is required");
        this.runnable = runnable;
    }

    public void timerExpired(Timer timer) {
        this.runnable.run();
    }
}

