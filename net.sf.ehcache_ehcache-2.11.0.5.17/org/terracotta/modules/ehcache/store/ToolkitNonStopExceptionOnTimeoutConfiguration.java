/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationFields$NonStopReadTimeoutBehavior
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationFields$NonStopWriteTimeoutBehavior
 */
package org.terracotta.modules.ehcache.store;

import net.sf.ehcache.config.NonstopConfiguration;
import org.terracotta.modules.ehcache.store.ToolkitNonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopConfigurationFields;

public class ToolkitNonStopExceptionOnTimeoutConfiguration
extends ToolkitNonStopConfiguration {
    public ToolkitNonStopExceptionOnTimeoutConfiguration(NonstopConfiguration ehcacheNonStopConfig) {
        super(ehcacheNonStopConfig);
    }

    @Override
    public NonStopConfigurationFields.NonStopReadTimeoutBehavior getReadOpNonStopTimeoutBehavior() {
        return NonStopConfigurationFields.NonStopReadTimeoutBehavior.EXCEPTION;
    }

    @Override
    public NonStopConfigurationFields.NonStopWriteTimeoutBehavior getWriteOpNonStopTimeoutBehavior() {
        return NonStopConfigurationFields.NonStopWriteTimeoutBehavior.EXCEPTION;
    }
}

