/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders.classloading;

import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.Scanner;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmptyScanner
implements Scanner {
    private static final Logger log = LoggerFactory.getLogger(EmptyScanner.class);

    @Override
    public Collection<DeploymentUnit> scan() {
        return Collections.emptyList();
    }

    @Override
    public Collection<DeploymentUnit> getDeploymentUnits() {
        return Collections.emptyList();
    }

    @Override
    public void reset() {
    }

    @Override
    public void remove(DeploymentUnit unit) {
        log.warn("EmptyScanner.remove called for {}", (Object)unit);
    }
}

