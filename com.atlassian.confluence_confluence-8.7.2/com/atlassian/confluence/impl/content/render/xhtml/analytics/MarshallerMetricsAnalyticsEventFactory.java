/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.content.render.xhtml.analytics;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetrics;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAnalyticsEvent;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MarshallerMetricsAnalyticsEventFactory {
    public static @NonNull MarshallerMetricsAnalyticsEvent newMarshallerMetricsAnalyticsEvent(ConversionContext context, MarshallerMetrics metrics, String eventName, String accumulationKey) {
        ContentEntityObject entity = context.getEntity();
        return new MarshallerMetricsAnalyticsEvent(StringUtils.trimToEmpty((String)RequestCacheThreadLocal.getRequestCorrelationId()), eventName, accumulationKey, metrics.getExecutionCount(), metrics.getCumulativeExecutionTimeNanos(), metrics.getCumulativeStreamingTimeNanos(), metrics.getCustomMetrics(), StringUtils.trimToEmpty((String)(entity != null ? entity.getIdAsString() : null)), StringUtils.trimToEmpty((String)MarshallerMetricsAnalyticsEventFactory.extractContainerEntityId(entity)), StringUtils.trimToEmpty((String)context.getOutputType()), StringUtils.trimToEmpty((String)context.getOutputDeviceType()), StringUtils.trimToEmpty((String)(entity != null ? entity.getType() : null)), context.isAsyncRenderSafe());
    }

    private static String extractContainerEntityId(@Nullable ContentEntityObject entity) {
        if (entity instanceof Contained) {
            Object container = ((Contained)((Object)entity)).getContainer();
            return container != null ? ((ContentEntityObject)container).getIdAsString() : null;
        }
        return null;
    }
}

