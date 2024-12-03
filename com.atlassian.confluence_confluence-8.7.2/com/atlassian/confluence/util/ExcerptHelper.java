/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.content.render.xhtml.ContentExcerptUtils;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.storage.ContentTransformerFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xml.HTMLParagraphStripper;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcerptHelper {
    private static final Logger log = LoggerFactory.getLogger(ExcerptHelper.class);
    private ContentTransformerFactory contentTransformerFactory;
    private HTMLParagraphStripper htmlParagraphStripper;

    public String getExcerpt(ContentEntityObject contentEntityObject) {
        MacroDefinition macroDefinition = this.getMacroDefinition(contentEntityObject, "excerpt");
        String excerpt = macroDefinition == null ? "" : StringUtils.defaultString((String)macroDefinition.getBodyText());
        try {
            return this.htmlParagraphStripper.stripFirstParagraph(excerpt);
        }
        catch (XMLStreamException e) {
            log.warn("Could not strip leading paragraph from excerpt, returning unstripped", (Throwable)e);
            return excerpt;
        }
    }

    public String getExcerptSummary(ContentEntityObject contentEntityObject) {
        return this.getTextSummary(this.getExcerpt(contentEntityObject), 1, 500);
    }

    public MacroDefinition getMacroDefinition(ContentEntityObject contentEntityObject, String macroName) {
        if (!BodyType.XHTML.equals(contentEntityObject.getBodyContent().getBodyType())) {
            return null;
        }
        AtomicReference atomicMacroDefinition = new AtomicReference();
        Transformer transformer = this.contentTransformerFactory.getTransformer(macroDefinition -> {
            if (macroName.equals(macroDefinition.getName()) && atomicMacroDefinition.get() == null) {
                atomicMacroDefinition.set(macroDefinition);
            }
            return macroDefinition;
        });
        StringReader reader = new StringReader(contentEntityObject.getBodyContent().getBody());
        PageContext pageContext = contentEntityObject.toPageContext();
        DefaultConversionContext conversionContext = new DefaultConversionContext(pageContext);
        try {
            transformer.transform(reader, conversionContext);
        }
        catch (XhtmlException e) {
            e.printStackTrace();
        }
        return (MacroDefinition)atomicMacroDefinition.get();
    }

    @Deprecated
    public String getText(String content) {
        return ContentExcerptUtils.extractTextFromXhtmlContent(content);
    }

    @Deprecated
    public String getTextSummary(String content, int minLength, int maxLength) {
        return ContentExcerptUtils.extractTextSummaryFromXhtmlContent(content, minLength, maxLength);
    }

    public void setContentTransformerFactory(ContentTransformerFactory contentTransformerFactory) {
        this.contentTransformerFactory = contentTransformerFactory;
    }

    public void setHtmlParagraphStripper(HTMLParagraphStripper htmlParagraphStripper) {
        this.htmlParagraphStripper = htmlParagraphStripper;
    }
}

