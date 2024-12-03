/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.notifications.AnalyticsRenderContext;

@Internal
public interface AnalyticsRenderContextManager
extends AnalyticsRenderContext {
    public void setContext(AnalyticsRenderContext.Context var1, Runnable var2);
}

