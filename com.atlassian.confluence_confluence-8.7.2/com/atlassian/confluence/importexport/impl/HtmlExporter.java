/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.importexport.ExportLinkFormatter;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.AbstractRendererExporterImpl;
import com.atlassian.confluence.importexport.impl.ExportUtils;
import com.atlassian.confluence.importexport.impl.HtmlExportLinkFormatter;
import com.atlassian.confluence.importexport.impl.HtmlImageProcessingRule;
import com.atlassian.confluence.importexport.impl.ImageProcessingRule;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.TemplateSupport;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.collect.Lists;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class HtmlExporter
extends AbstractRendererExporterImpl {
    private static final Logger log = LoggerFactory.getLogger(HtmlExporter.class);
    private AttachmentManager attachmentManager;

    @Override
    public String doExport(ProgressMeter progress) throws ImportExportException {
        String baseExportPath = super.doExport(progress);
        ConfluenceEntityObject firstEntity = this.getWorkingExportContext().getWorkingEntities().get(0);
        String archivePath = baseExportPath + File.separator + this.prepareExportFileName(firstEntity) + ".zip";
        if (this.context.isExportHierarchy() && firstEntity instanceof Page) {
            Page indexPage = null;
            try {
                indexPage = (Page)firstEntity.clone();
            }
            catch (CloneNotSupportedException e) {
                log.error("Can't clone page?!", (Throwable)e);
                throw new InfrastructureException("Can't clone content entity object: " + indexPage, (Throwable)e);
            }
            indexPage.setTitle("Index");
            ArrayList<Page> children = new ArrayList<Page>(1);
            children.add((Page)firstEntity);
            indexPage.setChildren((List<Page>)children);
            this.exportPage(indexPage, baseExportPath);
        }
        try {
            FileUtils.createZipFile((File)new File(baseExportPath), (File)new File(archivePath));
            return archivePath;
        }
        catch (Exception e) {
            throw new ImportExportException(e);
        }
    }

    @Override
    protected void exportPage(Page page, String baseExportPath) throws ImportExportException {
        if (this.context.isExportHierarchy() && page.hasChildren()) {
            List<Page> children = page.getChildren();
            for (Page child : children) {
                this.exportPage(child, baseExportPath);
            }
        }
        String exportPath = this.getFullExportPath(baseExportPath, page);
        this.ensurePathExists(new File(exportPath).getParent());
        this.doExportEntity(page, exportPath);
    }

    @Override
    protected void exportSpace(Space space, String baseExportPath) throws ImportExportException {
        super.exportSpace(space, baseExportPath);
        List<ContentNode> contentNodes = this.getWorkingExportContext().getContentTree().getAllContentNodes();
        for (ContentNode contentNode : contentNodes) {
            this.exportPage(contentNode.getPage(), baseExportPath);
        }
    }

    private TemplateSupport prepareExportTemplate(ConfluenceEntityObject entity) {
        TemplateSupport templateSupport = this.createTemplateSupport("htmlexport.vm");
        templateSupport.setOutputMimeType("text/html");
        if (entity instanceof Space) {
            templateSupport.putInContext("space", entity);
            templateSupport.putInContext("contentTree", this.getWorkingExportContext().getContentTree());
        } else if (entity instanceof Page) {
            templateSupport.putInContext("page", entity);
            templateSupport.putInContext("exportUtils", new ExportUtils());
            templateSupport.putInContext("breadcrumbs", this.getExportedPageBreadcrumbs((Page)entity));
            templateSupport.putInContext("attachmentManager", this.attachmentManager);
            templateSupport.setExportChildren(this.context.isExportHierarchy());
        }
        return templateSupport;
    }

    private List<Page> getExportedPageBreadcrumbs(Page entity) {
        ArrayList<Page> ancestors = new ArrayList<Page>();
        ContentTree contentTree = this.getWorkingExportContext().getContentTree();
        while (entity.getParent() != null && contentTree.getPage((entity = entity.getParent()).getId()) != null) {
            ancestors.add(entity);
        }
        return Lists.reverse(ancestors);
    }

    @Override
    protected ImageProcessingRule getImageProcessingRule(String exportDir) {
        HtmlImageProcessingRule rule = new HtmlImageProcessingRule();
        ContainerManager.autowireComponent((Object)rule);
        return rule;
    }

    @Override
    protected ExportLinkFormatter getExportLinkFormatter() {
        return HtmlExportLinkFormatter.getInstance();
    }

    @Override
    protected String getFullExportPath(String baseExportPath, ConfluenceEntityObject entity) throws ImportExportException {
        StringBuilder exportPath = new StringBuilder(baseExportPath);
        exportPath.append(File.separator);
        if (entity instanceof Space) {
            Space space = (Space)entity;
            exportPath.append(this.getSpaceKeyForExportFileName(space.getKey()));
            exportPath.append(File.separator).append("index").append(".html");
        } else if (entity instanceof Page) {
            Page page = (Page)entity;
            exportPath.append(this.getSpaceKeyForExportFileName(page.getSpace().getKey()));
            exportPath.append(File.separator);
            exportPath.append(ExportUtils.getTitleAsFilename(page));
        } else {
            throw new ImportExportException("Unsupported export type: " + entity.getClass());
        }
        return exportPath.toString();
    }

    @Override
    protected void doExportEntity(ConfluenceEntityObject entity, String exportPath) throws ImportExportException {
        Writer writer = null;
        StringWriter stringWriter = new StringWriter();
        try {
            writer = new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(exportPath), "UTF-8"));
            this.prepareExportTemplate(entity).processTemplate(entity, stringWriter);
            String exportDir = new File(exportPath).getParent();
            this.exportImages(stringWriter.toString(), writer, exportDir);
            if (entity instanceof Page) {
                Page page = (Page)entity;
                List<Attachment> attachments = page.getAttachments();
                for (Attachment attachment : attachments) {
                    this.exportResource(attachment.getDownloadPathWithoutEncoding(), exportDir, attachment.getExportPath());
                }
                FileUtils.saveTextFile((String)ConfluenceRenderUtils.renderDefaultStylesheet(), (File)new File(exportDir, "styles/site.css"));
            }
        }
        catch (Exception e) {
            log.error("Error occurred during export.", (Throwable)e);
            log.error("Entity being exported was: " + entity);
            throw new ImportExportException(e);
        }
        finally {
            try {
                stringWriter.close();
                if (writer != null) {
                    writer.close();
                }
            }
            catch (IOException e) {
                log.error("Error while closing the writer!", (Throwable)e);
            }
        }
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

