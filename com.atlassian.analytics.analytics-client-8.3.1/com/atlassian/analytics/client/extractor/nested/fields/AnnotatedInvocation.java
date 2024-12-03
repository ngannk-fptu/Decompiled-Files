/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.analytics.client.extractor.nested.fields;

import com.atlassian.analytics.client.extractor.nested.fields.AnalyticsFieldAnnotations;
import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;

public class AnnotatedInvocation {
    private final Object invocationResult;
    private final EnumSet<AnalyticsFieldAnnotations> annotations;

    AnnotatedInvocation(Object invocationResult, EnumSet<AnalyticsFieldAnnotations> annotations) {
        this.invocationResult = invocationResult;
        this.annotations = annotations;
    }

    public Object getInvocationResult() {
        return this.invocationResult;
    }

    public boolean isHashed() {
        return this.annotations.contains((Object)AnalyticsFieldAnnotations.HASHED);
    }

    public boolean isException() {
        return this.annotations.contains((Object)AnalyticsFieldAnnotations.SECURITY_EXCEPTION);
    }

    @VisibleForTesting
    EnumSet<AnalyticsFieldAnnotations> getAnnotations() {
        return this.annotations;
    }
}

