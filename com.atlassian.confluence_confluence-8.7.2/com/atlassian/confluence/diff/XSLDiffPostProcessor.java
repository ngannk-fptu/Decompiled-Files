/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.jdom.Document
 *  org.jdom.transform.JDOMResult
 *  org.jdom.transform.JDOMSource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.diff;

import com.atlassian.confluence.diff.DiffPostProcessor;
import com.atlassian.core.util.ClassLoaderUtils;
import java.io.IOException;
import java.net.URL;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom.Document;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSLDiffPostProcessor
implements DiffPostProcessor {
    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    private static final Logger log = LoggerFactory.getLogger(XSLDiffPostProcessor.class);
    private final URL styleSheetResource;

    public XSLDiffPostProcessor(String styleSheetResource) {
        this.styleSheetResource = ClassLoaderUtils.getResource((String)styleSheetResource, XSLDiffPostProcessor.class);
        if (this.styleSheetResource == null) {
            throw new IllegalArgumentException("The styleSheetResource " + styleSheetResource + " could not be opened.");
        }
    }

    @Override
    public Document process(Document document) {
        log.debug("Starting the XSLDiffPostProcessor with the stylesheet {}.", (Object)this.styleSheetResource.getFile());
        try {
            JDOMSource docSource = new JDOMSource(document);
            StreamSource transXsltSource = new StreamSource(this.styleSheetResource.openStream());
            JDOMResult result = new JDOMResult();
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer(transXsltSource);
            transformer.transform((Source)docSource, (Result)result);
            return result.getDocument();
        }
        catch (IOException ex) {
            log.warn("An Exception occurred while reading the stylesheet resource " + this.styleSheetResource.getFile() + ". The XSLDiffPostProcessor has not been applied.", (Throwable)ex);
        }
        catch (TransformerException ex) {
            log.warn("An Exception occurred while trying to create the transformer to apply the XSL " + this.styleSheetResource.getFile() + ". The XSLDiffPostProcessor has not been applied.", (Throwable)ex);
        }
        log.debug("Finished the XSLDiffPostProcessor with the stylesheet {}.", (Object)this.styleSheetResource.getFile());
        return document;
    }
}

