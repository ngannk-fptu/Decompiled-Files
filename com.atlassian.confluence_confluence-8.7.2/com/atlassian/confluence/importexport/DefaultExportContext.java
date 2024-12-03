/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.user.User;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DefaultExportContext
implements ExportContext {
    private static final Logger log = LoggerFactory.getLogger(DefaultExportContext.class);
    private File exportDirectory;
    private List<ConfluenceEntityObject> workingEntities = new ArrayList<ConfluenceEntityObject>();
    private List<ConfluenceEntityObject> exceptionEntities = new ArrayList<ConfluenceEntityObject>();
    private boolean exportComments = true;
    private boolean exportAttachments;
    private boolean exportHierarchy;
    private Set processedIds = new HashSet();
    private ContentTree contentTree;
    private ExportScope scope;
    private String type;
    private boolean exportAll = false;
    private User user;
    private String spaceKey;

    public static DefaultExportContext getXmlBackupInstance() {
        DefaultExportContext context = new DefaultExportContext();
        context.setType("TYPE_ALL_DATA");
        context.setExportScope(ExportScope.ALL);
        return context;
    }

    public void addWorkingEntity(ConfluenceEntityObject entity) {
        if (this.workingEntities == null) {
            this.workingEntities = new ArrayList<ConfluenceEntityObject>();
        }
        if (!this.workingEntities.contains(entity)) {
            this.workingEntities.add(entity);
        }
    }

    public void addExceptionEntities(List<ConfluenceEntityObject> entities, boolean validate) throws IllegalArgumentException {
        if (validate) {
            for (int i = 0; i < entities.size(); ++i) {
                ConfluenceEntityObject o = entities.get(i);
                if (o instanceof ConfluenceEntityObject) continue;
                throw new IllegalArgumentException("Entities list contains something other than a ConfluenceEntityObject - position [" + i + "], o = " + o);
            }
        }
        if (this.exceptionEntities == null) {
            this.exceptionEntities = new ArrayList<ConfluenceEntityObject>();
        }
        this.exceptionEntities.addAll(entities);
    }

    public void addExceptionEntity(ConfluenceEntityObject entity) {
        if (this.exceptionEntities == null) {
            this.exceptionEntities = new ArrayList<ConfluenceEntityObject>();
        }
        if (!this.exceptionEntities.contains(entity)) {
            this.exceptionEntities.add(entity);
        }
    }

    @Override
    public boolean isPageInExport(Page page, PageManager pageManager) {
        if (page == null) {
            return false;
        }
        if (this.isExceptionEntity(page)) {
            return false;
        }
        for (ContentNode node : this.getContentTree().getAllContentNodes()) {
            if (!page.equals(node.getPage())) continue;
            return true;
        }
        return false;
    }

    public List<ConfluenceEntityObject> getExceptionEntities() {
        return this.exceptionEntities;
    }

    @Override
    public List<ConfluenceEntityObject> getWorkingEntities() {
        return this.workingEntities;
    }

    @Override
    public Set getProcessedIds() {
        return this.processedIds;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public String getSpaceKeyOfSpaceExport() {
        return this.spaceKey;
    }

    @Override
    public boolean isExceptionEntity(ConfluenceEntityObject entity) {
        return this.exceptionEntities != null && this.exceptionEntities.contains(entity);
    }

    @Override
    public boolean isExportComments() {
        return this.exportComments;
    }

    public void setExportComments(boolean exportComments) {
        this.exportComments = exportComments;
    }

    @Override
    public boolean isExportAttachments() {
        return this.exportAttachments;
    }

    @Override
    public void setExportAttachments(boolean exportAttachments) {
        this.exportAttachments = exportAttachments;
    }

    @Override
    public boolean isExportHierarchy() {
        return this.exportHierarchy;
    }

    @Override
    public void setExportHierarchy(boolean exportHierarchy) {
        this.exportHierarchy = exportHierarchy;
    }

    public File getExportDirectory() {
        return this.exportDirectory;
    }

    public void setExportDirectory(File exportDirectory) {
        this.exportDirectory = exportDirectory.getAbsoluteFile();
    }

    public ContentTree getContentTree() {
        return this.contentTree;
    }

    public void setContentTree(ContentTree contentTree) {
        this.contentTree = contentTree;
    }

    @Override
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public ExportScope getExportScope() {
        return this.scope;
    }

    public void setExportScope(ExportScope scope) {
        this.scope = scope;
    }

    public boolean isExportAll() {
        return this.exportAll;
    }

    public void setExportAll(boolean exportAll) {
        this.exportAll = exportAll;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }
}

