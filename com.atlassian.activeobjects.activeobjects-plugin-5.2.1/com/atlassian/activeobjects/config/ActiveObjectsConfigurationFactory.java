/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 */
package com.atlassian.activeobjects.config;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import java.util.List;
import java.util.Set;
import net.java.ao.RawEntity;
import org.osgi.framework.Bundle;

public interface ActiveObjectsConfigurationFactory {
    public ActiveObjectsConfiguration getConfiguration(Bundle var1, String var2, Set<Class<? extends RawEntity<?>>> var3, List<ActiveObjectsUpgradeTask> var4);
}

