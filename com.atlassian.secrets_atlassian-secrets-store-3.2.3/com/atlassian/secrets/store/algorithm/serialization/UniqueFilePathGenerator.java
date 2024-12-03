/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.secrets.store.algorithm.serialization;

import java.time.Clock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniqueFilePathGenerator {
    private final Clock clock;
    private final String objectClassName;
    private static final Logger log = LoggerFactory.getLogger(UniqueFilePathGenerator.class);

    public UniqueFilePathGenerator(String objectClassName, Clock clock) {
        this.objectClassName = objectClassName;
        this.clock = clock;
    }

    public String generateName() {
        String nonEmptyPath = this.objectClassName + "_" + this.clock.instant().toEpochMilli();
        log.debug("Will use generated name: {}", (Object)nonEmptyPath);
        return nonEmptyPath;
    }
}

