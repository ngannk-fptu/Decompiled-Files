/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.model.application.Application;

public interface InternalApplicationHelper {
    public Application findCrowdConsoleApplication() throws RuntimeException;
}

