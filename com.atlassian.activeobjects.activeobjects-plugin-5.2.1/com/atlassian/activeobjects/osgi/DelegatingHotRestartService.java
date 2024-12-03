/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.HotRestartService
 */
package com.atlassian.activeobjects.osgi;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.osgi.ActiveObjectsServiceFactory;
import com.atlassian.activeobjects.spi.HotRestartService;
import java.util.List;
import java.util.concurrent.Future;

public class DelegatingHotRestartService
implements HotRestartService {
    private final ActiveObjectsServiceFactory delegate;

    public DelegatingHotRestartService(ActiveObjectsServiceFactory delegate) {
        this.delegate = delegate;
    }

    public Future<List<ActiveObjects>> doHotRestart() {
        return this.delegate.doHotRestart();
    }
}

