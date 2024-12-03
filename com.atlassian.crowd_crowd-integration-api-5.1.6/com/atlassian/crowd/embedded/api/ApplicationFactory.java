/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import com.atlassian.crowd.model.application.Application;

public interface ApplicationFactory {
    public Application getApplication();

    public String getApplicationName();

    public boolean isEmbeddedCrowd();
}

