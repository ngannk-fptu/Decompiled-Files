/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.nonstop.NonStopConfiguration
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationFields$NonStopReadTimeoutBehavior
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationFields$NonStopWriteTimeoutBehavior
 */
package org.terracotta.modules.ehcache.store.nonstop;

import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopConfigurationFields;

public class ToolkitNonstopDisableConfig
implements NonStopConfiguration {
    public NonStopConfigurationFields.NonStopReadTimeoutBehavior getReadOpNonStopTimeoutBehavior() {
        return NonStopConfigurationFields.NonStopReadTimeoutBehavior.EXCEPTION;
    }

    public NonStopConfigurationFields.NonStopWriteTimeoutBehavior getWriteOpNonStopTimeoutBehavior() {
        return NonStopConfigurationFields.NonStopWriteTimeoutBehavior.EXCEPTION;
    }

    public long getTimeoutMillis() {
        return -1L;
    }

    public long getSearchTimeoutMillis() {
        return -1L;
    }

    public boolean isEnabled() {
        return false;
    }

    public boolean isImmediateTimeoutEnabled() {
        return false;
    }
}

