/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.impl;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.bulk.PageContentTransformer;

public class KeepAsIsContentTransformer
implements PageContentTransformer {
    @Override
    public String transform(String content, Page oldPage, Page newPage) {
        return content;
    }
}

