/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.request;

import com.atlassian.confluence.extra.jira.model.EntityServerCompositeKey;
import com.atlassian.confluence.extra.jira.model.JiraBatchRequestData;
import java.util.HashMap;
import java.util.Map;

public class SingleJiraIssuesThreadLocalAccessor {
    private static final ThreadLocal<Map<EntityServerCompositeKey, JiraBatchRequestData>> jiraBatchRequestDataMapThreadLocal = new ThreadLocal();
    private static final ThreadLocal<Map<Long, Boolean>> batchProcessedMapThreadLocal = ThreadLocal.withInitial(HashMap::new);

    public static void putJiraBatchRequestData(EntityServerCompositeKey entityServerCompositeKey, JiraBatchRequestData jiraBatchRequestData) {
        Map<EntityServerCompositeKey, JiraBatchRequestData> jiraBatchRequestDataMap = jiraBatchRequestDataMapThreadLocal.get();
        if (jiraBatchRequestDataMap != null) {
            jiraBatchRequestDataMap.put(entityServerCompositeKey, jiraBatchRequestData);
        }
    }

    public static void init() {
        jiraBatchRequestDataMapThreadLocal.set(new HashMap());
        batchProcessedMapThreadLocal.set(new HashMap());
    }

    public static JiraBatchRequestData getJiraBatchRequestData(EntityServerCompositeKey entityServerCompositeKey) {
        Map<EntityServerCompositeKey, JiraBatchRequestData> jiraBatchRequestDataMap = jiraBatchRequestDataMapThreadLocal.get();
        if (jiraBatchRequestDataMap != null) {
            return jiraBatchRequestDataMap.get(entityServerCompositeKey);
        }
        return null;
    }

    public static void setBatchProcessedMapThreadLocal(Long contentId, Boolean processed) {
        Map<Long, Boolean> batchProcessedMap = batchProcessedMapThreadLocal.get();
        if (batchProcessedMap != null) {
            batchProcessedMap.put(contentId, processed);
        }
    }

    public static Boolean isBatchProcessed(Long contentId) {
        Map<Long, Boolean> batchProcessMap = batchProcessedMapThreadLocal.get();
        if (batchProcessMap != null) {
            Boolean processed = batchProcessMap.get(contentId);
            return processed == null ? Boolean.FALSE : processed;
        }
        return Boolean.FALSE;
    }
}

