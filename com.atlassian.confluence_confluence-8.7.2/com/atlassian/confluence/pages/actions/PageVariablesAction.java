/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.opensymphony.xwork2.ActionContext
 *  org.apache.struts2.dispatcher.HttpParameters
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.actions.CreatePageAction;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.struts2.dispatcher.HttpParameters;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PageVariablesAction
extends CreatePageAction {
    private XhtmlContent xhtmlContent;
    private List templateVariables;
    private boolean templateApplied = false;
    private static final String VARIABLE_VALUE_PARAM_PREFIX = "variableValues.";
    private static final String NO_VARIABLES_IN_TEMPLATE = "novariables";
    public static final String HTML_MACRO_XHTML_MODULE_KEY = "confluence.macros.html:html-xhtml";
    private String renderedTemplateContent;
    private DefaultWebInterfaceContext webInterfaceContext;
    private List<Label> labels;
    private RenderedContentCleaner renderedContentCleaner;
    private List<String> htmlMacroScripts = new ArrayList<String>();

    public List getTemplateVariables() throws XhtmlException {
        if (this.templateVariables == null) {
            this.templateVariables = this.pageTemplateManager.getTemplateVariables(this.getPageTemplate());
        }
        return this.templateVariables;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        if (!this.canAnonymousUseConfluence()) {
            return "login";
        }
        this.setPage(null);
        this.doDefault();
        if (this.getTemplateVariables() == null || this.getTemplateVariables().size() == 0) {
            if (this.getPageTemplate() != null) {
                this.mergeVariablesAndPrepareForEditor(Collections.emptyList());
            }
            return NO_VARIABLES_IN_TEMPLATE;
        }
        ContentEntityObject attachmentSourceContent = this.getAttachmentSourceContent();
        PageContext renderContext = attachmentSourceContent == null ? new PageContext() : attachmentSourceContent.toPageContext();
        renderContext.addParam("com.atlassian.confluence.plugins.templates", true);
        DefaultConversionContext context = new DefaultConversionContext(renderContext);
        PageTemplate pageTemplate = this.getPageTemplate();
        String templateViewFormat = BodyType.XHTML.equals(pageTemplate.getBodyType()) ? this.formatConverter.convertToViewFormat(this.getPageTemplate().getContent(), context.getPageContext()) : this.xhtmlContent.convertWikiToView(this.getPageTemplate().getContent(), context, new ArrayList<RuntimeException>());
        this.setRenderedTemplateContent(templateViewFormat);
        this.setLabelsString(this.getPageTemplate().getLabels());
        return "input";
    }

    @Override
    protected List<Label> getLabels() {
        if (this.labels == null) {
            this.labels = new ArrayList<Label>();
            if (this.getPageTemplate() != null) {
                this.labels.addAll(this.getPageTemplate().getLabels());
            }
        }
        return this.labels;
    }

    @Override
    public DefaultWebInterfaceContext getWebInterfaceContext() {
        if (this.webInterfaceContext == null) {
            this.webInterfaceContext = DefaultWebInterfaceContext.copyOf(super.getWebInterfaceContext());
        }
        return this.webInterfaceContext;
    }

    @HtmlSafe
    public String getRenderedTemplateContent() {
        String sanitizedContent = this.renderedContentCleaner.cleanQuietly(this.renderedTemplateContent);
        return sanitizedContent + this.getHtmlMacroScriptTags();
    }

    public void setRenderedTemplateContent(String renderedTemplateContent) {
        this.renderedTemplateContent = renderedTemplateContent;
    }

    private void mergeVariablesAndPrepareForEditor(List<Variable> variables) {
        try {
            String editorFormat = this.pageTemplateManager.mergeVariables(this.getPageTemplate(), variables, this.getNewSpaceKey());
            this.setWysiwygContent(editorFormat);
            this.setLabelsString(this.getPageTemplate().getLabels());
            this.webInterfaceContext = this.getWebInterfaceContext();
            this.webInterfaceContext.setParameter("numLabelsString", this.getNumberOfLabelsAsString());
            this.webInterfaceContext.setParameter("labels", this.getLabels());
            this.setTemplateApplied(true);
        }
        catch (XhtmlException ex) {
            throw new RuntimeException("Failed to convert the page template " + this.getTemplateId() + " to editor format.", ex);
        }
    }

    @RequireSecurityToken(value=true)
    public String doEnter() throws Exception {
        this.doDefault();
        this.populateParentPageTitleField();
        ArrayList<Variable> variables = new ArrayList<Variable>();
        HttpParameters params = ActionContext.getContext().getParameters();
        params.forEach((key, value) -> {
            if (key.startsWith(VARIABLE_VALUE_PARAM_PREFIX)) {
                String varName = key.substring(VARIABLE_VALUE_PARAM_PREFIX.length());
                StringVariable variable = new StringVariable(HtmlUtil.urlDecode(varName), value.getValue());
                variables.add(variable);
            }
        });
        if (variables.size() > 0) {
            this.mergeVariablesAndPrepareForEditor(variables);
        }
        return "success";
    }

    @Override
    public void validate() {
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    private boolean canAnonymousUseConfluence() {
        ConfluenceUser currentUser = this.getAuthenticatedUser();
        return this.getConfluenceAccessManager().getUserAccessStatusNoExemptions(currentUser).canUseConfluence();
    }

    public boolean isTemplateApplied() {
        return this.templateApplied;
    }

    public void setTemplateApplied(boolean templateApplied) {
        this.templateApplied = templateApplied;
    }

    @Deprecated
    public void setEditorFormatService(EditorFormatService editorFormatService) {
    }

    public void setXhtmlContent(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    @Override
    public void setFormatConverter(FormatConverter formatConverter) {
        this.formatConverter = formatConverter;
    }

    public void setRenderedContentCleaner(RenderedContentCleaner renderedContentCleaner) {
        this.renderedContentCleaner = renderedContentCleaner;
    }

    private String getHtmlMacroScriptTags() {
        this.htmlMacroScripts.clear();
        if (this.templateId != null && this.isHtmlMacroXhtmlModuleEnabled()) {
            this.parseHtmlMacroScriptTagFromTemplate();
        }
        return String.join((CharSequence)"", this.htmlMacroScripts);
    }

    private boolean isHtmlMacroXhtmlModuleEnabled() {
        return this.pluginAccessor.isPluginModuleEnabled(HTML_MACRO_XHTML_MODULE_KEY);
    }

    private String getHtmlMacroXhtmlModuleName() {
        return this.pluginAccessor.getPluginModule(HTML_MACRO_XHTML_MODULE_KEY).getName();
    }

    private void parseHtmlMacroScriptTagFromTemplate() {
        if (this.getPageTemplate() != null) {
            Document document = Jsoup.parse((String)this.getPageTemplate().getContent());
            document.select("ac\\:structured-macro[ac:name=" + this.getHtmlMacroXhtmlModuleName() + "]").forEach(this::parseScriptTagFromHtmlMacro);
        }
    }

    private void parseScriptTagFromHtmlMacro(Element htmlMacroElement) {
        Element bodyElement = htmlMacroElement.selectFirst("ac\\:plain-text-body");
        Document elementDocument = Jsoup.parse((String)bodyElement.text());
        elementDocument.select("script").forEach(scriptElement -> this.htmlMacroScripts.add(scriptElement.outerHtml()));
    }
}

