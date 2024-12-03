/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.copy;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.bulk.copy.PageCopyOptions;

public interface BulkPageCopy {
    public void deepCopy(PageCopyOptions var1, Page var2, Page var3);
}

