/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao.bulk.delete;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.bulk.delete.PageDeleteOptions;

public interface BulkPageDelete {
    public void deepDelete(PageDeleteOptions var1, Page var2);
}

