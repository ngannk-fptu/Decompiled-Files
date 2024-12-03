/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.extractor.nested.fields;

import com.atlassian.analytics.client.extractor.nested.fields.AnnotatedInvocation;
import java.util.Map;

public interface SingleObjectExtractor {
    public Map<String, AnnotatedInvocation> extractSingleObject(Object var1);
}

