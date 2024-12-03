/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.input.SAXBuilder
 */
package com.atlassian.confluence.extra.jira.request;

import java.io.ByteArrayInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

final class SAXBuilderFactory {
    private static final InputSource EMPTY_INPUT_SOURCE = new InputSource(new ByteArrayInputStream(new byte[0]));
    private static final EntityResolver EMPTY_ENTITY_RESOLVER = (publicId, systemId) -> EMPTY_INPUT_SOURCE;

    private SAXBuilderFactory() {
    }

    private static XMLReader createNamespaceAwareXmlReader() {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            spf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            XMLReader xr = spf.newSAXParser().getXMLReader();
            xr.setEntityResolver(EMPTY_ENTITY_RESOLVER);
            return xr;
        }
        catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    static SAXBuilder createSAXBuilder() {
        return new SAXBuilder(){

            protected XMLReader createParser() {
                return SAXBuilderFactory.createNamespaceAwareXmlReader();
            }
        };
    }
}

