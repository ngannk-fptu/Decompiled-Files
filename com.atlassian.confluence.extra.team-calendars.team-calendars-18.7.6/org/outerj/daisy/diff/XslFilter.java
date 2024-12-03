/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff;

import java.io.IOException;
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
    public ContentHandler xsl(ContentHandler consumer, String xslPath) throws IOException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Templates template = factory.newTemplates(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(xslPath)));
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
}

