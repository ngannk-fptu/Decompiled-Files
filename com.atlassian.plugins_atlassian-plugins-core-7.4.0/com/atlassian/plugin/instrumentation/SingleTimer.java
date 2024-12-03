/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.operations.OpSnapshot
 *  com.atlassian.instrumentation.operations.OpTimer
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.instrumentation;

import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.plugin.instrumentation.Timer;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleTimer
extends Timer {
    private static final Logger log = LoggerFactory.getLogger(SingleTimer.class);
    private String name;

    SingleTimer(@Nonnull Optional<OpTimer> opTimer, String name) {
        super(opTimer);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void close() {
        Optional<OpTimer> timerOption = this.getOpTimer();
        if (timerOption.isPresent()) {
            OpSnapshot snapshot = timerOption.get().snapshot();
            long cpuTime = snapshot.getCpuTotalTime(TimeUnit.MILLISECONDS);
            long elapsedTime = snapshot.getElapsedTotalTime(TimeUnit.MILLISECONDS);
            log.info("Timer {} took {}ms ({} cpu ns)", new Object[]{snapshot.getName(), elapsedTime, cpuTime});
        }
        super.close();
    }
}

