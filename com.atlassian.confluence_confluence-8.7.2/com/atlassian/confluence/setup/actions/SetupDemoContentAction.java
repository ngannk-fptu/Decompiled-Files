/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.ServletContext
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.ImportedObjectPostProcessor;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import javax.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupDemoContentAction
extends AbstractSetupAction {
    private static final Logger log = LoggerFactory.getLogger(SetupDemoContentAction.class);
    private URL demoSiteUrl;
    private ImportExportManager importExportManager;
    private IndexManager indexManager;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() {
        URL demoSiteZipUrl = this.getDemoSiteUrl();
        if (demoSiteZipUrl == null) {
            log.error("Could not find demo-site.zip on the classpath.");
            this.addActionError("error.could.not.find", "demo-site.zip");
            return "error";
        }
        try {
            this.importAndReindex(demoSiteZipUrl);
        }
        catch (ImportExportException e) {
            log.error("Could not import demo-site.zip: ", (Throwable)e);
            this.addActionError("Could not import demo-site.zip: " + e);
            return "error";
        }
        catch (RuntimeException e) {
            log.error("demo site import failed ", (Throwable)e);
            this.addActionError("error.failure.loading.demo.content", e);
            return "error";
        }
        this.getSetupPersister().progessSetupStep();
        if (this.getActionErrors().isEmpty()) {
            return "success";
        }
        return "error";
    }

    private void importAndReindex(URL demoSiteZipUrl) throws ImportExportException {
        DefaultImportContext importContext = new DefaultImportContext(demoSiteZipUrl, null);
        importContext.setPostProcessor(SetupDemoContentAction.createImportContextPostProcessor(new Date()));
        if (this.importExportManager == null) {
            this.importExportManager = (ImportExportManager)ContainerManager.getComponent((String)"importExportManager");
        }
        if (this.indexManager == null) {
            this.indexManager = (IndexManager)ContainerManager.getComponent((String)"indexManager");
        }
        this.importExportManager.doImport(importContext);
        this.getSetupPersister().setDemonstrationContentInstalled();
        this.indexManager.reIndex();
    }

    private static ImportedObjectPostProcessor createImportContextPostProcessor(Date importStartTime) {
        return obj -> {
            if (obj instanceof ConfluenceEntityObject) {
                ConfluenceEntityObject entityObject = (ConfluenceEntityObject)obj;
                if (entityObject instanceof Page) {
                    entityObject.setLastModificationDate(new Date());
                } else {
                    entityObject.setLastModificationDate(importStartTime);
                }
                return true;
            }
            return false;
        };
    }

    private URL getDemoSiteUrl() {
        if (this.demoSiteUrl != null) {
            return this.demoSiteUrl;
        }
        ServletContext servletContext = ServletActionContext.getRequest().getSession().getServletContext();
        String path = "/WEB-INF/classes/com/atlassian/confluence/setup/demo-site.zip";
        try {
            return servletContext.getResource("/WEB-INF/classes/com/atlassian/confluence/setup/demo-site.zip");
        }
        catch (MalformedURLException e) {
            log.error("Demo site URL is invalid; using default. URL was [{}]", (Object)"/WEB-INF/classes/com/atlassian/confluence/setup/demo-site.zip");
            return null;
        }
    }

    public void setDemoSiteUrl(URL demoSiteUrl) {
        this.demoSiteUrl = demoSiteUrl;
    }

    @Deprecated
    public ImportExportManager getImportExportManager() {
        return this.importExportManager;
    }

    @Deprecated
    public void setImportExportManager(ImportExportManager importExportManager) {
        this.importExportManager = importExportManager;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }
}

