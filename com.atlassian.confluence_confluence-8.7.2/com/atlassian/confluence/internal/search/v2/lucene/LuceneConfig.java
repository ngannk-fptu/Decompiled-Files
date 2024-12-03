/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.search.SearchPlatformConfig;

public class LuceneConfig
implements SearchPlatformConfig {
    @Override
    public boolean isSharedIndex() {
        return false;
    }
}

