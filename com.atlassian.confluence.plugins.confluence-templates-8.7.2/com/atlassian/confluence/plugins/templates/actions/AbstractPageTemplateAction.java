/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.user.User
 *  com.opensymphony.xwork2.Action
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.util.breadcrumbs.AdminBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.user.User;
import com.opensymphony.xwork2.Action;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPageTemplateAction
extends AbstractSpaceAction
implements BreadcrumbAware {
    private static final Logger log = LoggerFactory.getLogger(AbstractPageTemplateAction.class);
    protected PageTemplateManager pageTemplateManager;
    protected PageTemplate pageTemplate;
    protected long entityId;
    protected PageTemplate originalPageTemplate;
    protected String labelsString;
    protected BreadcrumbGenerator breadcrumbGenerator;

    public void validate() {
        super.validate();
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    public void setPageTemplateManager(PageTemplateManager pageTemplateManager) {
        this.pageTemplateManager = pageTemplateManager;
    }

    public long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public long getDraftId() {
        return 0L;
    }

    public void setDraftId(long draftId) {
    }

    public PageTemplate getPageTemplate() {
        if (this.pageTemplate == null && this.entityId != 0L) {
            this.pageTemplate = this.pageTemplateManager.getPageTemplate(this.entityId);
            if (this.pageTemplate != null) {
                try {
                    this.originalPageTemplate = (PageTemplate)this.pageTemplate.clone();
                }
                catch (CloneNotSupportedException e) {
                    log.error("Could not clone page template?" + e, (Throwable)e);
                }
            }
        }
        return this.pageTemplate;
    }

    public PageTemplate getPageTemplateByName(String name) {
        if (StringUtils.isNotEmpty((CharSequence)name)) {
            return this.pageTemplateManager.getPageTemplate(name, this.getSpace());
        }
        return null;
    }

    public String getLabelsString() {
        if (!StringUtils.isEmpty((CharSequence)this.labelsString)) {
            return this.labelsString;
        }
        if (this.pageTemplate != null) {
            this.setLabels(this.pageTemplate.getLabels());
        }
        return this.labelsString;
    }

    public void setLabelsString(String labelsString) {
        this.labelsString = labelsString;
    }

    public List<Label> getLabels() {
        if (this.pageTemplate != null) {
            return this.pageTemplate.getLabels();
        }
        return Collections.emptyList();
    }

    protected void setLabels(List<Label> labels) {
        StringBuilder labelStringBuilder = new StringBuilder();
        Iterator<Label> labelIt = labels.iterator();
        while (labelIt.hasNext()) {
            Label label = labelIt.next();
            labelStringBuilder.append(label);
            if (!labelIt.hasNext()) continue;
            labelStringBuilder.append(" ");
        }
        this.labelsString = labelStringBuilder.toString();
    }

    public boolean isPermitted() {
        Space space = this.getPageTemplate() != null ? this.getPageTemplate().getSpace() : this.getSpace();
        Object target = space != null ? space : PermissionManager.TARGET_APPLICATION;
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, target);
    }

    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext context = DefaultWebInterfaceContext.copyOf((WebInterfaceContext)super.getWebInterfaceContext());
        context.setParameter("numLabelsString", (Object)this.getNumberOfLabelsText());
        context.setParameter("labels", this.getLabels());
        return context;
    }

    private String getNumberOfLabelsText() {
        int numLabels;
        String labels = this.getLabelsString();
        int n = numLabels = StringUtils.isEmpty((CharSequence)labels) ? 0 : StringUtils.split((String)labels, (char)' ').length;
        String property = numLabels > 1 ? "editor.labels.plural" : (numLabels == 0 ? "editor.labels.zero" : "editor.labels.singular");
        return this.getText(property, new Object[]{numLabels});
    }

    protected String globalTemplateSuffix() {
        return StringUtils.isEmpty((CharSequence)this.getKey()) ? "-global" : "";
    }

    public Breadcrumb getBreadcrumb() {
        Space space = this.getSpace();
        if (space != null) {
            return this.breadcrumbGenerator.getSpaceAdminBreadcrumb((Action)this, space);
        }
        return AdminBreadcrumb.getInstance();
    }
}

