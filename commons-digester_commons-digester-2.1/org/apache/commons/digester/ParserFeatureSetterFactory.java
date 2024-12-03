/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.digester.parser.GenericParser;
import org.apache.commons.digester.parser.XercesParser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

@Deprecated
public class ParserFeatureSetterFactory {
    private static boolean isXercesUsed;

    public static SAXParser newSAXParser(Properties properties) throws ParserConfigurationException, SAXException, SAXNotRecognizedException, SAXNotSupportedException {
        if (isXercesUsed) {
            return XercesParser.newSAXParser(properties);
        }
        return GenericParser.newSAXParser(properties);
    }

    static {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            if (factory.getClass().getName().startsWith("org.apache.xerces")) {
                isXercesUsed = true;
            }
        }
        catch (Exception ex) {
            isXercesUsed = false;
        }
    }
}

