/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.user.User
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImmutableImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.user.User;
import java.io.IOException;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
public interface ImportExportManager {
    public static final String TYPE_ALL_DATA = "TYPE_ALL_DATA";
    public static final String TYPE_XML = "TYPE_XML";
    public static final String TYPE_HTML = "TYPE_HTML";
    public static final String TYPE_PDF = "TYPE_PDF";
    public static final String TYPE_MOINMOIN = "TYPE_MOINMOIN";

    @Transactional(noRollbackFor={ImportExportException.class})
    public void doImport(ImportContext var1) throws ImportExportException;

    @Transactional(noRollbackFor={ImportExportException.class})
    public ImmutableImportProcessorSummary performImport(ImportContext var1) throws ImportExportException;

    @Transactional(readOnly=true, noRollbackFor={ImportExportException.class})
    public String exportAs(ExportContext var1, ProgressMeter var2) throws ImportExportException;

    @Transactional(readOnly=true)
    public List getImportExportTypeSpecifications();

    @Transactional(readOnly=true)
    public ContentTree getContentTree(User var1, Space var2);

    @Transactional(readOnly=true)
    public ContentTree getPageBlogTree(User var1, Space var2);

    @Transactional(propagation=Propagation.SUPPORTS, noRollbackFor={IOException.class})
    public String prepareDownloadPath(String var1) throws IOException;

    @Transactional(readOnly=true)
    public boolean isImportAllowed(String var1);

    @Transactional(readOnly=true)
    public BuildNumber getOldestSpaceImportAllowed();
}

