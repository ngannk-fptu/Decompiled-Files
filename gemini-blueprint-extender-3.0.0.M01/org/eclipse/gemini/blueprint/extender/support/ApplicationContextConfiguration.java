/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.support;

import java.util.Dictionary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.support.internal.ConfigUtils;
import org.eclipse.gemini.blueprint.extender.support.scanning.ConfigurationScanner;
import org.eclipse.gemini.blueprint.extender.support.scanning.DefaultConfigurationScanner;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ApplicationContextConfiguration {
    private static final Log log = LogFactory.getLog(ApplicationContextConfiguration.class);
    private final Bundle bundle;
    private final ConfigurationScanner configurationScanner;
    private final boolean asyncCreation;
    private final String[] configurationLocations;
    private final boolean isSpringPoweredBundle;
    private final boolean publishContextAsService;
    private final boolean waitForDeps;
    private final String toString;
    private final long timeout;
    private final boolean hasTimeout;

    public ApplicationContextConfiguration(Bundle bundle) {
        this(bundle, new DefaultConfigurationScanner());
    }

    public ApplicationContextConfiguration(Bundle bundle, ConfigurationScanner configurationScanner) {
        Assert.notNull((Object)bundle);
        Assert.notNull((Object)configurationScanner);
        this.bundle = bundle;
        this.configurationScanner = configurationScanner;
        Dictionary headers = this.bundle.getHeaders();
        Object[] configs = this.configurationScanner.getConfigurations(bundle);
        this.isSpringPoweredBundle = !ObjectUtils.isEmpty((Object[])configs);
        this.configurationLocations = configs;
        this.hasTimeout = ConfigUtils.isDirectiveDefined(headers, "timeout");
        long option = ConfigUtils.getTimeOut(headers);
        this.timeout = option >= 0L ? option * 1000L : option;
        this.publishContextAsService = ConfigUtils.getPublishContext(headers);
        this.asyncCreation = ConfigUtils.getCreateAsync(headers);
        this.waitForDeps = ConfigUtils.getWaitForDependencies(headers);
        StringBuilder buf = new StringBuilder();
        buf.append("AppCtxCfg [Bundle=");
        buf.append(OsgiStringUtils.nullSafeSymbolicName((Bundle)bundle));
        buf.append("]isSpringBundle=");
        buf.append(this.isSpringPoweredBundle());
        buf.append("|async=");
        buf.append(this.isCreateAsynchronously());
        buf.append("|wait-for-deps=");
        buf.append(this.isWaitForDependencies());
        buf.append("|publishCtx=");
        buf.append(this.isPublishContextAsService());
        buf.append("|timeout=");
        buf.append(this.getTimeout() / 1000L);
        buf.append("s");
        this.toString = buf.toString();
        if (log.isTraceEnabled()) {
            log.trace((Object)("Configuration: " + this.toString));
        }
    }

    public boolean isSpringPoweredBundle() {
        return this.isSpringPoweredBundle;
    }

    public boolean isTimeoutDeclared() {
        return this.hasTimeout;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public boolean isCreateAsynchronously() {
        return this.asyncCreation;
    }

    public boolean isPublishContextAsService() {
        return this.publishContextAsService;
    }

    public boolean isWaitForDependencies() {
        return this.waitForDeps;
    }

    public String[] getConfigurationLocations() {
        return this.configurationLocations;
    }

    public String toString() {
        return this.toString;
    }
}

