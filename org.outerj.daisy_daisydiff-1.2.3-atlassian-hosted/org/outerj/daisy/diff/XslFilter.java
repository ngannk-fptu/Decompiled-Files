/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.ContentHandler;

public class XslFilter {
    private final ConcurrentMap<String, Templates> templatesCache = new ConcurrentHashMap<String, Templates>();

    public ContentHandler xsl(ContentHandler consumer, String xslPath) throws IOException {
        try {
            Templates template = this.getOrCreateTemplates(xslPath);
            TransformerFactory transFact = TransformerFactory.newInstance();
            SAXTransformerFactory saxTransFact = (SAXTransformerFactory)transFact;
            TransformerHandler transHand = saxTransFact.newTransformerHandler(template);
            transHand.setResult(new SAXResult(consumer));
            return transHand;
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Can't transform xml.");
    }

    private Templates getOrCreateTemplates(String xslPath) throws TransformerConfigurationException {
        Templates result = (Templates)this.templatesCache.get(xslPath);
        if (result == null) {
            Templates newTemplates = this.createTemplates(xslPath);
            result = this.templatesCache.putIfAbsent(xslPath, newTemplates);
            result = result == null ? newTemplates : result;
        }
        return result;
    }

    private Templates createTemplates(String xslPath) throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        return factory.newTemplates(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(xslPath)));
    }
}

