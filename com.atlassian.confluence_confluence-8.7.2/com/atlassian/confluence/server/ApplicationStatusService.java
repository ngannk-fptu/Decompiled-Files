/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.server;

import com.atlassian.confluence.server.ApplicationState;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ApplicationStatusService {
    public @NonNull ApplicationState getState();
}

