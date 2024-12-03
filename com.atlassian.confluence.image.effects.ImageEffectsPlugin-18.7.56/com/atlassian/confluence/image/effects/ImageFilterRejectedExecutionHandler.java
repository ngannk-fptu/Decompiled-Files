/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.image.effects;

import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageFilterRejectedExecutionHandler
extends ThreadPoolExecutor.AbortPolicy {
    private final Logger log = LoggerFactory.getLogger(ImageFilterRejectedExecutionHandler.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        this.log.debug("ImageFilter task {} rejected from {}", (Object[])new String[]{r.toString(), e.toString()});
        super.rejectedExecution(r, e);
    }
}

