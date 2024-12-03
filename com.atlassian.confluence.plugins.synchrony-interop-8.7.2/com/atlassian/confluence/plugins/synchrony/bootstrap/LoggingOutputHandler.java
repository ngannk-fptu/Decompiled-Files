/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.utils.process.LineOutputHandler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.utils.process.LineOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingOutputHandler
extends LineOutputHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoggingOutputHandler.class);

    protected void processLine(int lineNum, String line) {
        logger.debug(line);
    }
}

