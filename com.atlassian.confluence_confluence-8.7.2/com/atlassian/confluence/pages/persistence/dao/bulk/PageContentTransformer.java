/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk;

import com.atlassian.confluence.pages.Page;

public interface PageContentTransformer {
    public String transform(String var1, Page var2, Page var3);
}

