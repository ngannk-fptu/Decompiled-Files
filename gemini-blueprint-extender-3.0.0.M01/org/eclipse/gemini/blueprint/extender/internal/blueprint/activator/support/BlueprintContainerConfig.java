/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 */
package org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support;

import java.util.Dictionary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintConfigUtils;
import org.eclipse.gemini.blueprint.extender.internal.blueprint.activator.support.BlueprintConfigurationScanner;
import org.eclipse.gemini.blueprint.extender.support.ApplicationContextConfiguration;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;

public class BlueprintContainerConfig
extends ApplicationContextConfiguration {
    private static final Log log = LogFactory.getLog(BlueprintContainerConfig.class);
    private final long timeout;
    private final boolean createAsync;
    private final boolean waitForDep;
    private final boolean publishContext;
    private final boolean hasTimeout;
    private final String toString;

    public BlueprintContainerConfig(Bundle bundle) {
        super(bundle, new BlueprintConfigurationScanner());
        Dictionary headers = bundle.getHeaders();
        this.hasTimeout = BlueprintConfigUtils.hasTimeout(headers);
        long option = BlueprintConfigUtils.getTimeOut(headers);
        this.timeout = option >= 0L ? option : 300000L;
        this.createAsync = BlueprintConfigUtils.getCreateAsync(headers);
        this.waitForDep = BlueprintConfigUtils.getWaitForDependencies(headers);
        this.publishContext = BlueprintConfigUtils.getPublishContext(headers);
        StringBuilder buf = new StringBuilder();
        buf.append("Blueprint Config [Bundle=");
        buf.append(OsgiStringUtils.nullSafeSymbolicName((Bundle)bundle));
        buf.append("]isBlueprintBundle=");
        buf.append(this.isSpringPoweredBundle());
        buf.append("|async=");
        buf.append(this.createAsync);
        buf.append("|graceperiod=");
        buf.append(this.waitForDep);
        buf.append("|publishCtx=");
        buf.append(this.publishContext);
        buf.append("|timeout=");
        buf.append(this.timeout);
        buf.append("ms");
        this.toString = buf.toString();
        if (log.isTraceEnabled()) {
            log.trace((Object)("Configuration: " + this.toString));
        }
    }

    @Override
    public boolean isTimeoutDeclared() {
        return this.hasTimeout;
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    @Override
    public boolean isCreateAsynchronously() {
        return this.createAsync;
    }

    @Override
    public boolean isWaitForDependencies() {
        return this.waitForDep;
    }

    @Override
    public boolean isPublishContextAsService() {
        return this.publishContext;
    }

    @Override
    public String toString() {
        return this.toString;
    }
}

