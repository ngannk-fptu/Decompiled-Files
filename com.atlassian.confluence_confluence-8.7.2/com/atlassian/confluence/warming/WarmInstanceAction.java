/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.io.output.NullWriter
 */
package com.atlassian.confluence.warming;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.output.NullWriter;

@Deprecated(forRemoval=true)
public class WarmInstanceAction
extends ConfluenceActionSupport {
    private SpaceManager spaceManager;
    private XhtmlContent xhtmlContent;
    private TemplateRenderer templateRenderer;
    private PageBuilderService pageBuilderService;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    @HtmlSafe
    public String getWarm() {
        this.warmWebResources();
        this.warmSoyTemplates();
        this.warmXhtmlContentRenderer();
        return "Your instance is now toasty.";
    }

    private void warmWebResources() {
        ImmutableList contexts = ImmutableList.of((Object)"page", (Object)"atl.confluence.plugins.pagetree-desktop", (Object)"viewcontent", (Object)"atl.comments", (Object)"plugin.quick.comment.hider", (Object)"main", (Object)"atl.general");
        for (String context : contexts) {
            this.pageBuilderService.assembler().resources().requireContext(context);
        }
        ImmutableList webResources = ImmutableList.of((Object)"confluence.web.resources:master-styles", (Object)"confluence.web.resources:ajs", (Object)"com.atlassian.confluence.plugins.confluence-labels:labels-editor", (Object)"confluence.web.resources:aui-forms", (Object)"confluence.web.resources:master-styles", (Object)"confluence.web.resources:breadcrumbs", (Object)"confluence.web.resources:print-styles", (Object)"com.atlassian.confluence.plugins.confluence-space-ia:spacesidebar", (Object)"confluence.web.resources:atlassian-effects");
        for (String resource : webResources) {
            this.pageBuilderService.assembler().resources().requireWebResource(resource);
        }
    }

    private void warmSoyTemplates() {
        this.warmSoyTemplate("com.atlassian.confluence.plugins.quickedit:quick-comment-initial", "Confluence.Templates.Comments.displayTopLevelCommentEditorPlaceholder.soy");
        this.warmSoyTemplate("com.atlassian.confluence.plugins.confluence-page-banner:soy-resources", "Confluence.Templates.PageBanner.banner.soy");
        this.warmSoyTemplate("com.atlassian.confluence.plugins.confluence-space-ia:soy-resources", "Confluence.Templates.Sidebar.sidebar.soy");
        this.warmSoyTemplate("com.atlassian.confluence.plugins.confluence-space-ia:soy-resources", "Confluence.Templates.Sidebar.headerStyles.soy");
        this.warmSoyTemplate("com.atlassian.confluence.plugins.confluence-quicknav:quicknav-resources", "Confluence.Templates.QuickNav.panel.soy");
        this.warmSoyTemplate("com.atlassian.plugins.atlassian-nav-links-plugin:rotp-menu", "navlinks.templates.appswitcher.switcher.soy");
        this.warmSoyTemplate("com.atlassian.auiplugin:aui-experimental-soy-templates", "aui.page.header.soy");
        this.warmSoyTemplate("com.atlassian.confluence.plugins.confluence-create-content-plugin:resources", "Confluence.Templates.Blueprints.createDialogInitParams.soy");
        this.warmSoyTemplate("com.atlassian.confluence.plugins.confluence-labels:labels-resources-server", "Confluence.Templates.Labels.labels.soy");
        this.warmSoyTemplate("com.atlassian.confluence.contributors:soy-templates", "Confluence.ContributorsMacro.ajaxContainer.soy");
    }

    private void warmSoyTemplate(String moduleKey, String templateName) {
        try {
            this.templateRenderer.render(moduleKey, templateName, (Map<String, Object>)ImmutableMap.of()).writeTo((Writer)new NullWriter());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void warmXhtmlContentRenderer() {
        try {
            this.xhtmlContent.convertStorageToView("<ac:macro ac:name=\"pagetree\"><ac:parameter ac:name=\"root\">@none</ac:parameter><ac:parameter ac:name=\"expandCurrent\">true</ac:parameter></ac:macro>", new DefaultConversionContext(new PageContext(this.getSpaceKey())));
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        catch (XhtmlException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSpaceKey() {
        List<Space> spaces = this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser(this.getAuthenticatedUser()).build()).getPage(0, 1);
        if (spaces.isEmpty()) {
            return null;
        }
        return spaces.get(0).getKey();
    }

    public void setXhtmlContent(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setPageBuilderService(PageBuilderService pageBuilderService) {
        this.pageBuilderService = pageBuilderService;
    }
}

