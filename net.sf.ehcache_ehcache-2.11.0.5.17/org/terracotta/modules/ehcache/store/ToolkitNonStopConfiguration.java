/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.nonstop.NonStopConfiguration
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationFields
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationFields$NonStopReadTimeoutBehavior
 *  org.terracotta.toolkit.nonstop.NonStopConfigurationFields$NonStopWriteTimeoutBehavior
 */
package org.terracotta.modules.ehcache.store;

import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.config.TimeoutBehaviorConfiguration;
import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopConfigurationFields;

public class ToolkitNonStopConfiguration
implements NonStopConfiguration {
    protected final NonstopConfiguration ehcacheNonStopConfig;

    public ToolkitNonStopConfiguration(NonstopConfiguration ehcacheNonStopConfig) {
        this.ehcacheNonStopConfig = ehcacheNonStopConfig;
    }

    public NonStopConfigurationFields.NonStopReadTimeoutBehavior getReadOpNonStopTimeoutBehavior() {
        return this.convertEhcacheBehaviorToToolkitReadBehavior();
    }

    public NonStopConfigurationFields.NonStopWriteTimeoutBehavior getWriteOpNonStopTimeoutBehavior() {
        return this.convertEhcacheBehaviorToToolkitWriteBehavior();
    }

    public long getTimeoutMillis() {
        return this.ehcacheNonStopConfig.getTimeoutMillis();
    }

    public long getSearchTimeoutMillis() {
        return this.ehcacheNonStopConfig.getSearchTimeoutMillis();
    }

    public boolean isEnabled() {
        return this.ehcacheNonStopConfig.isEnabled();
    }

    public boolean isImmediateTimeoutEnabled() {
        return this.ehcacheNonStopConfig.isImmediateTimeout();
    }

    private NonStopConfigurationFields.NonStopReadTimeoutBehavior convertEhcacheBehaviorToToolkitReadBehavior() {
        TimeoutBehaviorConfiguration behaviorConfiguration = this.ehcacheNonStopConfig.getTimeoutBehavior();
        switch (behaviorConfiguration.getTimeoutBehaviorType()) {
            case EXCEPTION: {
                return NonStopConfigurationFields.NonStopReadTimeoutBehavior.EXCEPTION;
            }
            case LOCAL_READS: 
            case LOCAL_READS_AND_EXCEPTION_ON_WRITES: {
                return NonStopConfigurationFields.NonStopReadTimeoutBehavior.LOCAL_READS;
            }
            case NOOP: {
                return NonStopConfigurationFields.NonStopReadTimeoutBehavior.NO_OP;
            }
        }
        return NonStopConfigurationFields.DEFAULT_NON_STOP_READ_TIMEOUT_BEHAVIOR;
    }

    private NonStopConfigurationFields.NonStopWriteTimeoutBehavior convertEhcacheBehaviorToToolkitWriteBehavior() {
        TimeoutBehaviorConfiguration behaviorConfiguration = this.ehcacheNonStopConfig.getTimeoutBehavior();
        switch (behaviorConfiguration.getTimeoutBehaviorType()) {
            case EXCEPTION: 
            case LOCAL_READS_AND_EXCEPTION_ON_WRITES: {
                return NonStopConfigurationFields.NonStopWriteTimeoutBehavior.EXCEPTION;
            }
            case LOCAL_READS: {
                return NonStopConfigurationFields.NonStopWriteTimeoutBehavior.NO_OP;
            }
            case NOOP: {
                return NonStopConfigurationFields.NonStopWriteTimeoutBehavior.NO_OP;
            }
        }
        return NonStopConfigurationFields.DEFAULT_NON_STOP_WRITE_TIMEOUT_BEHAVIOR;
    }
}

