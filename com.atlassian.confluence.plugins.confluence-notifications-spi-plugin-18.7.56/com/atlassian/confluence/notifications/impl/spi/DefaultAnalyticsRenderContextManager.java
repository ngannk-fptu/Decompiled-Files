/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.impl.spi;

import com.atlassian.confluence.notifications.AnalyticsRenderContext;
import com.atlassian.confluence.notifications.impl.AnalyticsRenderContextManager;

public class DefaultAnalyticsRenderContextManager
implements AnalyticsRenderContextManager {
    private final ThreadLocal<AnalyticsRenderContext.Context> contextThreadLocal = new ThreadLocal();

    @Override
    public void setContext(AnalyticsRenderContext.Context context, Runnable runnable) {
        try {
            this.contextThreadLocal.set(context);
            runnable.run();
        }
        finally {
            this.contextThreadLocal.remove();
        }
    }

    @Override
    public AnalyticsRenderContext.Context getContext() {
        return this.contextThreadLocal.get();
    }
}

