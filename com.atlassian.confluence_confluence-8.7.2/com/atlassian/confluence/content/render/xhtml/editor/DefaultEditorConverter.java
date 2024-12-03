/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.render.xhtml.editor.EditorConverter;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import java.io.StringReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEditorConverter
implements EditorConverter {
    private final Transformer storageConverter;
    private final HtmlToXmlConverter htmlToXmlConverter;
    private static final Logger log = LoggerFactory.getLogger(DefaultEditorConverter.class);

    public DefaultEditorConverter(Transformer storageConverter, HtmlToXmlConverter htmlToXmlConverter) {
        this.storageConverter = storageConverter;
        this.htmlToXmlConverter = htmlToXmlConverter;
    }

    @Override
    public String convert(String editorFormat, ConversionContext conversionContext) throws XhtmlParsingException, XhtmlException {
        if (StringUtils.isBlank((CharSequence)editorFormat)) {
            return "";
        }
        log.debug("Converting editor format: \n{}", (Object)editorFormat);
        String xmlEditorFormat = this.htmlToXmlConverter.convert(editorFormat);
        return this.storageConverter.transform(new StringReader(xmlEditorFormat), conversionContext);
    }
}

