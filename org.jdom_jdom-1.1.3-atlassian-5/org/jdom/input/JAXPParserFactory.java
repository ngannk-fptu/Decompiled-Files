/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.input;

import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

class JAXPParserFactory {
    private static final String CVS_ID = "@(#) $RCSfile: JAXPParserFactory.java,v $ $Revision: 1.6 $ $Date: 2007/11/10 05:29:00 $ $Name:  $";
    private static final String JAXP_SCHEMA_LANGUAGE_PROPERTY = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String JAXP_SCHEMA_LOCATION_PROPERTY = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private JAXPParserFactory() {
    }

    public static XMLReader createParser(boolean validating, Map features, Map properties) throws JDOMException {
        try {
            SAXParser parser = null;
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(validating);
            factory.setNamespaceAware(true);
            try {
                parser = factory.newSAXParser();
            }
            catch (ParserConfigurationException e) {
                throw new JDOMException("Could not allocate JAXP SAX Parser", e);
            }
            JAXPParserFactory.setProperty(parser, properties, JAXP_SCHEMA_LANGUAGE_PROPERTY);
            JAXPParserFactory.setProperty(parser, properties, JAXP_SCHEMA_LOCATION_PROPERTY);
            return parser.getXMLReader();
        }
        catch (SAXException e) {
            throw new JDOMException("Could not allocate JAXP SAX Parser", e);
        }
    }

    private static void setProperty(SAXParser parser, Map properties, String name) throws JDOMException {
        try {
            if (properties.containsKey(name)) {
                parser.setProperty(name, properties.get(name));
            }
        }
        catch (SAXNotSupportedException e) {
            throw new JDOMException(name + " property not supported for JAXP parser " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException e) {
            throw new JDOMException(name + " property not recognized for JAXP parser " + parser.getClass().getName());
        }
    }
}

