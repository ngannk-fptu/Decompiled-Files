/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 */
package com.atlassian.applinks.ui.velocity;

import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;

public abstract class AbstractVelocityContext {
    protected final String contextPath;
    protected final InternalHostApplication internalHostApplication;
    protected final InternalTypeAccessor typeAccessor;
    protected final DocumentationLinker documentationLinker;

    protected AbstractVelocityContext(String contextPath, InternalHostApplication internalHostApplication, InternalTypeAccessor typeAccessor, DocumentationLinker documentationLinker) {
        this.contextPath = contextPath;
        this.internalHostApplication = internalHostApplication;
        this.typeAccessor = typeAccessor;
        this.documentationLinker = documentationLinker;
    }

    public DocumentationLinker getDocLinker() {
        return this.documentationLinker;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public String getBaseUrl() {
        return this.internalHostApplication.getBaseUrl().toString();
    }

    public String getApplicationName() {
        return this.internalHostApplication.getName();
    }
}

