/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes;

import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.PipesReporter;
import org.apache.tika.pipes.PipesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingPipesReporter
extends PipesReporter {
    Logger LOGGER = LoggerFactory.getLogger(LoggingPipesReporter.class);

    @Override
    public void report(FetchEmitTuple t, PipesResult result, long elapsed) {
        this.LOGGER.debug("{} {} {}", new Object[]{t, result, elapsed});
    }

    @Override
    public void error(Throwable t) {
        this.LOGGER.error("pipes error", t);
    }

    @Override
    public void error(String msg) {
        this.LOGGER.error("error {}", (Object)msg);
    }
}

