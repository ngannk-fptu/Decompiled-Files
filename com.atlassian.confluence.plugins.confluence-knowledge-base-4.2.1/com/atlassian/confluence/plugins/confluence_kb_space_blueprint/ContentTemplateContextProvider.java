/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint;

import com.atlassian.confluence.plugins.createcontent.TemplateRendererHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.plugin.PluginParseException;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ContentTemplateContextProvider
extends AbstractBlueprintContextProvider {
    static final String LABELS_PARAMETER = "labelsString";
    static final String KB_RESOURCE = "com.atlassian.confluence.plugins.confluence-knowledge-base:kb-article-resources";
    static final String CONTENTBYLABEL_MACRO_TEMPLATE = "Confluence.Blueprints.Plugin.KnowledgeBaseArticle.contentbylabelMacro.soy";
    static final String JIRAISSUE_MACRO_TEMPLATE = "Confluence.Blueprints.Plugin.KnowledgeBaseArticle.jiraIssuesMacro.soy";
    static final String ISSUE_KEY_PARAMETER = "jiraIssueKey";
    static final String SERVER_ID_PARAMETER = "jiraServerId";
    private String defaultBlueprintLabel;

    public ContentTemplateContextProvider(TemplateRendererHelper templateRendererHelper) {
        super(templateRendererHelper);
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.defaultBlueprintLabel = params.get("blueprintLabel");
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        context.put("jiraIssuesMacro", (Object)this.buildJiraIssuesMacro(context));
        context.put("contentbylabelMacro", (Object)this.getContentbylabelMacro(context));
        return context;
    }

    private String buildJiraIssuesMacro(BlueprintContext context) {
        String jiraIssuesMacro = "";
        HashMap jiraMacroParameters = Maps.newHashMap();
        String issueKey = (String)context.get(ISSUE_KEY_PARAMETER);
        String serverId = (String)context.get(SERVER_ID_PARAMETER);
        if (StringUtils.isNotBlank((CharSequence)issueKey) && StringUtils.isNotBlank((CharSequence)serverId)) {
            jiraMacroParameters.put(ISSUE_KEY_PARAMETER, issueKey);
            jiraMacroParameters.put(SERVER_ID_PARAMETER, serverId);
            jiraIssuesMacro = this.renderFromSoy(JIRAISSUE_MACRO_TEMPLATE, jiraMacroParameters);
        }
        return jiraIssuesMacro;
    }

    private String getContentbylabelMacro(BlueprintContext context) {
        String labels = (String)context.get(LABELS_PARAMETER);
        if (StringUtils.isBlank((CharSequence)labels)) {
            labels = this.defaultBlueprintLabel;
        }
        String spaceKey = context.getSpaceKey();
        HashMap soyContext = Maps.newHashMap();
        soyContext.put("spaceKey", spaceKey);
        soyContext.put("labels", labels);
        return this.renderFromSoy(CONTENTBYLABEL_MACRO_TEMPLATE, soyContext);
    }

    private String renderFromSoy(String soyTemplate, Map<String, Object> soyContext) {
        return this.templateRendererHelper.renderFromSoy(KB_RESOURCE, soyTemplate, soyContext);
    }
}

