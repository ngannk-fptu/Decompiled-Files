/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backgroundjob;

import com.atlassian.confluence.impl.backgroundjob.BackgroundJobResponse;
import java.util.Map;

public interface BackgroundJobProcessor {
    public BackgroundJobResponse process(Long var1, Map<String, Object> var2, long var3);

    default public boolean isSingleton() {
        return false;
    }
}

