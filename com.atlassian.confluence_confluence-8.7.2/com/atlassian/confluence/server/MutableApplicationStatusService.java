/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.server;

import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.ApplicationStatusService;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface MutableApplicationStatusService
extends ApplicationStatusService {
    public void setState(@NonNull ApplicationState var1);

    public void notifyServletsLoaded();

    public void notifyApplicationStarted();
}

