/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.loaders.classloading;

import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.Scanner;
import com.google.common.base.Preconditions;
import java.util.Collection;

public class ForwardingScanner
implements Scanner {
    private final Scanner delegate;

    public ForwardingScanner(Scanner delegate) {
        this.delegate = (Scanner)Preconditions.checkNotNull((Object)delegate);
    }

    @Override
    public Collection<DeploymentUnit> scan() {
        return this.delegate.scan();
    }

    @Override
    public Collection<DeploymentUnit> getDeploymentUnits() {
        return this.delegate.getDeploymentUnits();
    }

    @Override
    public void reset() {
        this.delegate.reset();
    }

    @Override
    public void remove(DeploymentUnit unit) {
        this.delegate.remove(unit);
    }
}

