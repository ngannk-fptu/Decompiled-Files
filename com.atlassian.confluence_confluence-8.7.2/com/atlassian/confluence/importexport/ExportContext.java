/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.user.User;
import java.util.List;
import java.util.Set;

@Deprecated
public interface ExportContext {
    public boolean isExportComments();

    public boolean isExportAttachments();

    public void setExportAttachments(boolean var1);

    public boolean isExportHierarchy();

    public void setExportHierarchy(boolean var1);

    public boolean isExceptionEntity(ConfluenceEntityObject var1);

    public List<ConfluenceEntityObject> getWorkingEntities();

    public Set getProcessedIds();

    public ExportScope getExportScope();

    public String getType();

    public User getUser();

    public String getSpaceKeyOfSpaceExport();

    public boolean isPageInExport(Page var1, PageManager var2);
}

