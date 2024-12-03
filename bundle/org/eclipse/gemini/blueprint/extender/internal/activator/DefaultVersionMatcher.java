/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.Version
 */
package org.eclipse.gemini.blueprint.extender.internal.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.extender.internal.activator.LifecycleManager;
import org.eclipse.gemini.blueprint.extender.internal.activator.VersionMatcher;
import org.eclipse.gemini.blueprint.extender.support.internal.ConfigUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class DefaultVersionMatcher
implements VersionMatcher {
    private static final Log log = LogFactory.getLog(LifecycleManager.class);
    private final String versionHeader;
    private final Version expectedVersion;

    public DefaultVersionMatcher(String versionHeader, Version expectedVersion) {
        this.versionHeader = versionHeader;
        this.expectedVersion = expectedVersion;
    }

    @Override
    public boolean matchVersion(Bundle bundle) {
        if (!ConfigUtils.matchExtenderVersionRange(bundle, this.versionHeader, this.expectedVersion)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Bundle [" + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + "] expects an extender w/ version[" + (String)bundle.getHeaders().get(this.versionHeader) + "] which does not match current extender w/ version[" + this.expectedVersion + "]; skipping bundle analysis..."));
            }
            return false;
        }
        return true;
    }
}

