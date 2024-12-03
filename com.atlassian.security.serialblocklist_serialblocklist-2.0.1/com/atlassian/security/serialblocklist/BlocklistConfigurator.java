/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.serialblocklist;

import com.atlassian.security.serialblocklist.BlocklistFilteringFunction;
import com.atlassian.security.serialblocklist.BlocklistLoader;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlocklistConfigurator {
    private static final Logger log = LoggerFactory.getLogger(BlocklistConfigurator.class);

    public Predicate<Class<?>> configure() {
        BlocklistLoader loader = new BlocklistLoader();
        loader.load();
        return new BlocklistFilteringFunction(loader.getBlockedClasses(), loader.getBlockedPatterns());
    }
}

