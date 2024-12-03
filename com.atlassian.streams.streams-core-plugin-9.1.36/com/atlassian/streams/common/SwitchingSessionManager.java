/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.spi.DelegatingSessionManager
 *  com.atlassian.streams.spi.OptionalService
 *  com.atlassian.streams.spi.SessionManager
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.streams.common;

import com.atlassian.streams.spi.DelegatingSessionManager;
import com.atlassian.streams.spi.OptionalService;
import com.atlassian.streams.spi.SessionManager;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import org.osgi.framework.BundleContext;

public class SwitchingSessionManager
extends OptionalService<SessionManager>
implements DelegatingSessionManager {
    private final SessionManager defaultSessionManager;

    public SwitchingSessionManager(SessionManager defaultSessionManager, BundleContext bundleContext) {
        super(SessionManager.class, bundleContext);
        this.defaultSessionManager = (SessionManager)Preconditions.checkNotNull((Object)defaultSessionManager, (Object)"defaultSessionManager");
    }

    public <T> T withSession(Supplier<T> s) {
        return (T)((SessionManager)this.getService().getOrElse((Object)this.defaultSessionManager)).withSession(s);
    }
}

