/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.embedded.EmbeddedAudio
 *  com.atlassian.renderer.embedded.EmbeddedFlash
 *  com.atlassian.renderer.embedded.EmbeddedImage
 *  com.atlassian.renderer.embedded.EmbeddedQuicktime
 *  com.atlassian.renderer.embedded.EmbeddedRealMedia
 *  com.atlassian.renderer.embedded.EmbeddedResource
 *  com.atlassian.renderer.embedded.EmbeddedWindowsMedia
 *  com.atlassian.renderer.embedded.UnembeddableObject
 *  com.atlassian.renderer.v2.components.EmbeddedRendererComponent
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.renderer.BlogPostReferenceParser;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.embedded.EmbeddedAudio;
import com.atlassian.renderer.embedded.EmbeddedFlash;
import com.atlassian.renderer.embedded.EmbeddedImage;
import com.atlassian.renderer.embedded.EmbeddedQuicktime;
import com.atlassian.renderer.embedded.EmbeddedRealMedia;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedWindowsMedia;
import com.atlassian.renderer.embedded.UnembeddableObject;
import com.atlassian.renderer.v2.components.EmbeddedRendererComponent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XhtmlEmbeddedRendererComponent
extends EmbeddedRendererComponent {
    private static final Logger log = LoggerFactory.getLogger(XhtmlEmbeddedRendererComponent.class);
    private final Marshaller<MacroDefinition> macroMarshaller;
    private final MacroParameterTypeParser macroParameterTypeParser;

    public XhtmlEmbeddedRendererComponent(Marshaller<MacroDefinition> wikiMarkupMacroMarshaller, MacroParameterTypeParser macroParameterTypeParser) {
        this.macroMarshaller = wikiMarkupMacroMarshaller;
        this.macroParameterTypeParser = macroParameterTypeParser;
    }

    protected String renderResource(RenderContext context, EmbeddedResource embeddedResource, String matchStr) {
        DefaultConversionContext conversionContext = new DefaultConversionContext(context);
        if (embeddedResource instanceof EmbeddedImage) {
            return super.renderResource(context, embeddedResource, matchStr);
        }
        Object renderedContent = null;
        if (this.isMultimediaResource(embeddedResource)) {
            renderedContent = this.renderAsMultimediaMacro(embeddedResource, matchStr, conversionContext);
        } else if (embeddedResource instanceof UnembeddableObject) {
            renderedContent = "!" + matchStr + "!";
        }
        if (renderedContent == null) {
            renderedContent = this.renderAsWikiMarkup(matchStr);
        }
        return renderedContent;
    }

    private String renderAsWikiMarkup(String matchStr) {
        String original = "!" + matchStr + "!";
        PlainTextMacroBody body = new PlainTextMacroBody(original);
        MacroDefinition macro = MacroDefinition.builder("unmigrated-wiki-markup").withMacroBody(body).build();
        try {
            return Streamables.writeToString(this.macroMarshaller.marshal(macro, null));
        }
        catch (XhtmlException e) {
            return original;
        }
    }

    private boolean isMultimediaResource(EmbeddedResource embeddedResource) {
        return embeddedResource instanceof EmbeddedAudio || embeddedResource instanceof EmbeddedFlash || embeddedResource instanceof EmbeddedQuicktime || embeddedResource instanceof EmbeddedRealMedia || embeddedResource instanceof EmbeddedWindowsMedia || StringUtils.endsWith((CharSequence)embeddedResource.getFilename(), (CharSequence)".avi");
    }

    private String renderAsMultimediaMacro(EmbeddedResource embeddedResource, String matchStr, ConversionContext context) {
        String pageRef = embeddedResource.getPage();
        String date = null;
        String page = pageRef;
        if (pageRef != null) {
            try {
                BlogPostReferenceParser blogParser = new BlogPostReferenceParser(pageRef);
                page = blogParser.getEntityName();
                SimpleDateFormat macroDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                date = macroDateFormat.format(blogParser.getCalendarPostingDay().getTime());
            }
            catch (ParseException blogParser) {
                // empty catch block
            }
        }
        MacroDefinitionBuilder builder = MacroDefinition.builder("multimedia").withParameter("space", embeddedResource.getSpace()).withParameter("page", page).withParameter("date", date).withParameter("name", embeddedResource.getFilename()).withParameter("width", embeddedResource.getProperties().getProperty("width")).withParameter("height", embeddedResource.getProperties().getProperty("height"));
        try {
            Map<String, Object> typedParameters = this.macroParameterTypeParser.parseMacroParameters("multimedia", builder.getParameters(), context);
            builder.withTypedParameters(typedParameters);
            return Streamables.writeToString(this.macroMarshaller.marshal(builder.build(), context));
        }
        catch (InvalidMacroParameterException e) {
            log.warn("Unable to convert embedded content to multimedia macro (!" + matchStr + "!). Will fallback to wiki markup macro.", (Throwable)e);
            return null;
        }
        catch (XhtmlException e) {
            log.warn("Unable to convert embedded content to multimedia macro (!" + matchStr + "!). Will fallback to wiki markup macro.", (Throwable)e);
            return null;
        }
    }
}

