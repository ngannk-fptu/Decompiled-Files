/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.components.phrase.TemplateParamRenderComponent
 *  com.google.common.io.CharStreams
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.TemplateHandler;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.util.RegexUtils;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.renderer.v2.components.phrase.TemplateParamRenderComponent;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class WikiTemplateHandler
implements TemplateHandler {
    private final EditorFormatService editorFormatService;
    private final SpaceManagerInternal spaceManager;

    public WikiTemplateHandler(EditorFormatService editorFormatService, SpaceManagerInternal spaceManager) {
        this.editorFormatService = editorFormatService;
        this.spaceManager = spaceManager;
    }

    @Override
    public List<Variable> getTemplateVariables(PageTemplate template) {
        if (template == null) {
            return null;
        }
        ArrayList<Variable> variables = new ArrayList<Variable>();
        Matcher match = TemplateParamRenderComponent.VARIABLE_PATTERN.matcher(template.getContent());
        while (match.find()) {
            StringVariable variableObj;
            String matchText;
            String paramName = matchText = match.group(1);
            if (matchText.indexOf(124) > 0) {
                paramName = matchText.substring(0, matchText.indexOf(124));
            }
            if (variables.contains(variableObj = new StringVariable(paramName))) continue;
            variables.add(variableObj);
        }
        return variables;
    }

    @Override
    public String insertVariables(Reader templateXml, List<? extends Variable> variables) {
        String content;
        try {
            content = CharStreams.toString((Readable)templateXml);
        }
        catch (IOException e) {
            throw new RuntimeException("Error converting reader of template XML into a string.");
        }
        for (int i = 0; i < variables.size(); ++i) {
            Variable variable = variables.get(i);
            content = content.replaceAll("@" + variable.getName() + "(\\|[\\p{L}0-9_\\(\\),]*)?@", RegexUtils.quoteReplacement(variable.getValue()));
        }
        return content;
    }

    @Override
    public String generateEditorFormat(PageTemplate template, List<? extends Variable> variables, String spaceKey) throws XhtmlException {
        String content = this.insertVariables(new StringReader(template.getContent()), variables);
        template.setSpace(this.spaceManager.getSpace(spaceKey));
        return this.editorFormatService.convertWikiToEdit(content, new DefaultConversionContext(new PageTemplateContext(template)));
    }
}

