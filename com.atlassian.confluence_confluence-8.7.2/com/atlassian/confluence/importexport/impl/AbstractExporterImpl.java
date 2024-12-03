/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.Exporter;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class AbstractExporterImpl
extends Exporter {
    private static final Logger log = LoggerFactory.getLogger(AbstractExporterImpl.class);
    private ExportFileNameGenerator exportFileNameGenerator;
    protected I18NBeanFactory i18nBeanFactory;

    protected DefaultExportContext getWorkingExportContext() {
        return (DefaultExportContext)this.context;
    }

    public String createAndSetExportDirectory() throws ImportExportException {
        this.getWorkingExportContext().setExportDirectory(this.createExportDirectory());
        return this.getWorkingExportContext().getExportDirectory().getAbsolutePath();
    }

    protected void ensureDirectoryCreated(File directory) throws ImportExportException {
        if (directory.exists() && directory.isDirectory()) {
            if (log.isDebugEnabled()) {
                log.debug("Directory exists not created [" + directory + "]");
            }
            return;
        }
        if (directory.exists() && directory.isFile()) {
            throw new ImportExportException("Export directory exists but is a file [" + directory + "]");
        }
        if (!directory.mkdirs()) {
            throw new ImportExportException("Couldn't create directory: " + directory.getAbsolutePath());
        }
        if (log.isDebugEnabled()) {
            log.debug("Directory created [" + directory + "]");
        }
    }

    protected String getSpaceKeyForExportFileName(String spaceKey) {
        if (spaceKey.startsWith("~")) {
            spaceKey = spaceKey.substring(1);
        }
        return HtmlUtil.urlEncode(spaceKey);
    }

    protected void checkHaveSomethingToExport() throws ImportExportException {
        if (this.getWorkingExportContext() == null) {
            throw new ImportExportException("Context is NULL. Nothing to export!");
        }
        List<ConfluenceEntityObject> workingEntities = this.getWorkingExportContext().getWorkingEntities();
        if (workingEntities == null || workingEntities.isEmpty()) {
            throw new ImportExportException("Nothing to export!");
        }
    }

    protected String prepareExportFileName(ConfluenceEntityObject entity) {
        ArrayList<String> nameParts = new ArrayList<String>(2);
        I18NBean i18NBean = this.i18nBeanFactory.getI18NBean();
        if (entity instanceof Space) {
            nameParts.add(i18NBean.getText("export.space.filename"));
        } else if (entity instanceof Page) {
            Page page = (Page)entity;
            nameParts.add(i18NBean.getText("export.page.filename"));
            nameParts.add(String.valueOf(page.getId()));
        }
        return this.exportFileNameGenerator.getExportFileName(nameParts.toArray(new String[nameParts.size()]));
    }

    protected File createExportDirectory() throws ImportExportException {
        try {
            return this.exportFileNameGenerator.createExportDirectory();
        }
        catch (IOException ex) {
            throw new ImportExportException(ex);
        }
    }

    public void setExportFileNameGenerator(ExportFileNameGenerator exportFileNameGenerator) {
        this.exportFileNameGenerator = exportFileNameGenerator;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18nBeanFactory) {
        this.i18nBeanFactory = i18nBeanFactory;
    }
}

