/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.transformers.Transformer
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.converter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHtmlToStorageConverter;
import java.io.Reader;
import java.io.StringReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEmailHtmlToStorageConverter
implements EmailHtmlToStorageConverter {
    private static final Logger log = LoggerFactory.getLogger(DefaultEmailHtmlToStorageConverter.class);
    private final Transformer emailTransformerChain;
    private final HtmlToXmlConverter htmlToXmlConverter;

    public DefaultEmailHtmlToStorageConverter(Transformer emailTransformerChain, HtmlToXmlConverter htmlToXmlConverter) {
        this.emailTransformerChain = emailTransformerChain;
        this.htmlToXmlConverter = htmlToXmlConverter;
    }

    @Override
    public String convert(String emailHtml, ConversionContext conversionContext) {
        if (StringUtils.isBlank((CharSequence)emailHtml)) {
            return "";
        }
        log.debug("Converting images from email HTML to storage format: \n{}", (Object)emailHtml);
        String xmlEditorFormat = this.htmlToXmlConverter.convert(emailHtml);
        try {
            return this.emailTransformerChain.transform((Reader)new StringReader(xmlEditorFormat), conversionContext);
        }
        catch (XhtmlException e) {
            log.error("Could not convert inline images in HTML to storage format");
            return emailHtml;
        }
    }
}

