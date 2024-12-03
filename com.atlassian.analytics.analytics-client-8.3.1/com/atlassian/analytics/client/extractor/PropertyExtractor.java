/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import java.util.Map;

public interface PropertyExtractor {
    public Map<String, Object> extractProperty(String var1, Object var2);

    public boolean isExcluded(String var1);

    public String extractName(Object var1);

    public String extractUser(Object var1, Map<String, Object> var2);

    public Map<String, Object> enrichProperties(Object var1);

    public String extractSubProduct(Object var1, String var2);

    public String getApplicationAccess();

    public String extractRequestCorrelationId(RequestInfo var1);
}

