/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.SubRenderer
 *  com.atlassian.renderer.v2.V2SubRenderer
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.impl.velocity.ReadOnlyBeanContextItemProvider;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.plugin.services.DefaultVelocityHelperService;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.velocity.ConfluenceStaticContextItemProvider;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.velocity.context.OutputMimeTypeAwareVelocityContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.V2SubRenderer;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import com.google.common.collect.ImmutableList;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.Context;

public class GenericVelocityMacro
implements Macro {
    private static final String CONTEXT_ITEM_SPACE = "space";
    private static final String CONTEXT_ITEM_CONTENT = "content";
    private static final String CONTEXT_ITEM_RENDER_CONTEXT = "renderContext";
    private static final String CONTEXT_ITEM_CONVERSION_CONTEXT = "conversionContext";
    private static final String CONTEXT_ITEM_CONFIG = "config";
    private static final String CONTEXT_ITEM_BODY = "body";
    private static final String CONTEXT_ITEM_PARAM = "param";
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("<p>(.*?)</p>", 34);
    private static final String MACRO_REQUIRED_CONTEXT_KEYS_PROPERTY_NAME = "macro.required.velocity.context.keys";
    public static final List<String> REQUIRED_VELOCITY_CONTEXT_KEYS = ImmutableList.copyOf((Object[])Optional.ofNullable(System.getProperty("macro.required.velocity.context.keys")).orElse(String.format("%s,%s", ConfluenceStaticContextItemProvider.ContextItems.GENERAL_UTIL.getKey(), ReadOnlyBeanContextItemProvider.ContextItems.BOOTSTRAP_STATUS.getKey())).split(","));
    private String name;
    private String template;
    private Macro.BodyType bodyType;
    private boolean legacyWikiTemplate = false;
    private boolean escapeBody = false;
    private SpaceManager spaceManager;
    private HtmlToXmlConverter htmlToXmlConverter;
    private Transformer transformer;
    private SubRenderer subRenderer;
    private List<MacroParameter> macroParameters;
    private VelocityHelperService velocityHelperService;

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        Map<String, Object> contextMap = MacroUtils.requiredVelocityContext(REQUIRED_VELOCITY_CONTEXT_KEYS);
        parameters.forEach((key, value) -> contextMap.put(CONTEXT_ITEM_PARAM + key, new HtmlFragment((Object)HtmlUtil.htmlEncode(value))));
        if (this.macroParameters != null) {
            for (MacroParameter macroParameter : this.macroParameters) {
                if (contextMap.containsKey(CONTEXT_ITEM_PARAM + macroParameter.getName()) || macroParameter.getDefaultValue() == null) continue;
                contextMap.put(CONTEXT_ITEM_PARAM + macroParameter.getName(), new HtmlFragment((Object)HtmlUtil.htmlEncode(macroParameter.getDefaultValue())));
            }
        }
        if (StringUtils.isNotBlank((CharSequence)body)) {
            if (this.isEscapeBody()) {
                body = HtmlEscaper.escapeAll((String)body, (boolean)false);
            } else if (this.getBodyType() == Macro.BodyType.RICH_TEXT) {
                body = this.stripFirstParagraphTags(body);
            }
            contextMap.put(CONTEXT_ITEM_BODY, body);
        }
        contextMap.put(CONTEXT_ITEM_CONFIG, contextMap.get("setup"));
        contextMap.put(CONTEXT_ITEM_CONVERSION_CONTEXT, context);
        PageContext pageContext = context.getPageContext();
        contextMap.put(CONTEXT_ITEM_RENDER_CONTEXT, (Object)pageContext);
        if (pageContext.getEntity() != null) {
            contextMap.put(CONTEXT_ITEM_CONTENT, pageContext.getEntity());
        }
        if (pageContext.getSpaceKey() != null) {
            Space clonedSpace = this.cloneSpace(pageContext);
            contextMap.put(CONTEXT_ITEM_SPACE, clonedSpace);
        }
        String output = this.getVelocityHelperService().getRenderedContent(this.template, (Context)OutputMimeTypeAwareVelocityContext.newHtmlContext(contextMap));
        if (this.legacyWikiTemplate) {
            return this.subRenderer.render(output, (RenderContext)pageContext);
        }
        try {
            output = this.htmlToXmlConverter.convert(output);
            return this.transformer.transform(new StringReader(output), context);
        }
        catch (XhtmlException ex) {
            throw new MacroExecutionException("Failed to transform the HTML macro template for display. Nested message: " + ex.getMessage(), ex);
        }
    }

    private VelocityHelperService getVelocityHelperService() {
        return this.velocityHelperService != null ? this.velocityHelperService : new DefaultVelocityHelperService();
    }

    private Space cloneSpace(PageContext pageContext) {
        Space space = this.spaceManager.getSpace(pageContext.getSpaceKey());
        if (space == null) {
            return null;
        }
        try {
            return (Space)space.clone();
        }
        catch (CloneNotSupportedException e) {
            return space;
        }
    }

    @Override
    public Macro.BodyType getBodyType() {
        return this.bodyType;
    }

    public void setBodyType(Macro.BodyType bodyType) {
        this.bodyType = bodyType;
    }

    @Override
    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private String stripFirstParagraphTags(String xml) {
        String trimmed = StringUtils.trim((String)xml);
        Matcher matcher = PARAGRAPH_PATTERN.matcher(trimmed);
        if (matcher.find() && matcher.end() == trimmed.length()) {
            return matcher.group(1);
        }
        return xml;
    }

    @Deprecated
    public boolean isLegacyWikiTemplate() {
        return this.legacyWikiTemplate;
    }

    public void setLegacyWikiTemplate(boolean legacyWikiTemplate) {
        this.legacyWikiTemplate = legacyWikiTemplate;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEscapeBody() {
        return this.escapeBody;
    }

    public void setEscapeBody(boolean escapeBody) {
        this.escapeBody = escapeBody;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setHtmlToXmlConverter(HtmlToXmlConverter htmlToXmlConverter) {
        this.htmlToXmlConverter = htmlToXmlConverter;
    }

    public void setStorageToViewTransformer(Transformer storageToViewTransformer) {
        this.transformer = storageToViewTransformer;
    }

    public List<MacroParameter> getMacroParameters() {
        return this.macroParameters;
    }

    public void setMacroParameters(List<MacroParameter> macroParameters) {
        this.macroParameters = macroParameters;
    }

    @Deprecated
    public void setSubRenderer(V2SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }
}

