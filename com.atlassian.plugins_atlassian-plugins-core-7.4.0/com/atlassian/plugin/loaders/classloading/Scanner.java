/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 */
package com.atlassian.plugin.loaders.classloading;

import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import java.util.Collection;

public interface Scanner {
    public Collection<DeploymentUnit> scan();

    public Collection<DeploymentUnit> getDeploymentUnits();

    public void reset();

    public void remove(DeploymentUnit var1);
}

