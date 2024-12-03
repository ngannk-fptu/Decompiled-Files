/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.mail.MailContentProcessor;
import com.atlassian.confluence.xml.XMLEntityResolver;
import com.atlassian.confluence.xml.XslTransformer;
import com.atlassian.plugin.util.ClassLoaderUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XsltMailContentProcessor
implements MailContentProcessor {
    private static final String TEMP_ROOT = "temporaryFragmentRootElement".toLowerCase();
    private static final String TEMP_ROOT_OPEN_TAG = "<" + TEMP_ROOT + ">";
    private static final String TEMP_ROOT_CLOSE_TAG = "</" + TEMP_ROOT + ">";
    private static final Logger log = LoggerFactory.getLogger(XsltMailContentProcessor.class);
    private final String xslResource;
    private final XslTransformer transformer;
    private final XMLEntityResolver entityResolver;
    private HtmlToXmlConverter htmlToXmlConverter;

    public XsltMailContentProcessor(String xslResource, XslTransformer transformer, XMLEntityResolver entityResolver, HtmlToXmlConverter htmlToXmlConverter) {
        this.xslResource = xslResource;
        this.transformer = transformer;
        this.entityResolver = entityResolver;
        this.htmlToXmlConverter = htmlToXmlConverter;
    }

    @Override
    public String process(String input) {
        InputStreamReader xslSource;
        if (StringUtils.isEmpty((CharSequence)input)) {
            return input;
        }
        try {
            URL resource = ClassLoaderUtils.getResource((String)this.xslResource, XsltMailContentProcessor.class);
            xslSource = new InputStreamReader(resource.openStream());
        }
        catch (IOException ex) {
            log.warn("Could not read the mail XSL transform resource: {}", (Object)this.xslResource);
            return input;
        }
        StringBuilder beforeTransform = new StringBuilder();
        beforeTransform.append(TEMP_ROOT_OPEN_TAG).append(input).append(TEMP_ROOT_CLOSE_TAG);
        String cleanedInput = this.htmlToXmlConverter.convert(beforeTransform.toString());
        beforeTransform = new StringBuilder();
        beforeTransform.append("<!DOCTYPE ").append(TEMP_ROOT).append(" [ ").append(this.entityResolver.createDTD()).append("]>").append(cleanedInput);
        StringReader inputReader = new StringReader(beforeTransform.toString());
        StringWriter resultWriter = new StringWriter();
        Result result = new StreamResult(resultWriter);
        result = this.transformer.transform(xslSource, inputReader, result);
        if (result == null || StringUtils.isBlank((CharSequence)resultWriter.toString())) {
            log.warn("The result of the XSL transform was empty.\nInput starts with: {}", (Object)StringUtils.left((String)input, (int)240));
            return input;
        }
        String transformed = StringUtils.trim((String)resultWriter.toString());
        if (transformed.startsWith(TEMP_ROOT_OPEN_TAG) && transformed.endsWith(TEMP_ROOT_CLOSE_TAG)) {
            transformed = StringUtils.trim((String)StringUtils.substringBetween((String)transformed, (String)TEMP_ROOT_OPEN_TAG, (String)TEMP_ROOT_CLOSE_TAG));
        }
        return transformed;
    }
}

