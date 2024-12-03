/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xml;

import com.atlassian.confluence.xml.XslTransformer;
import java.io.Reader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XalanXslTransformer
implements XslTransformer {
    private static final Logger log = LoggerFactory.getLogger(XalanXslTransformer.class);
    protected final TransformerFactory transformerFactory = TransformerFactory.newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);

    @Override
    public Result transform(Reader stylesheet, Reader xml, Result output) {
        Source xmlSource = this.createInputSource(xml);
        Source xsltSource = this.createXsltSource(stylesheet);
        if (xmlSource == null || xsltSource == null) {
            return null;
        }
        try {
            Transformer transformer = this.transformerFactory.newTransformer(xsltSource);
            transformer.transform(xmlSource, output);
        }
        catch (TransformerConfigurationException ex) {
            log.warn("No transformer could be created due to a configuration error.", (Throwable)ex);
            return null;
        }
        catch (TransformerException ex) {
            log.warn("An exception occurred while applying the stylesheet.", (Throwable)ex);
            return null;
        }
        return output;
    }

    protected Source createXsltSource(Reader xslt) {
        return new StreamSource(xslt);
    }

    protected Source createInputSource(Reader xml) {
        return new StreamSource(xml);
    }
}

