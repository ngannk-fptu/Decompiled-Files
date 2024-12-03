/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.xml.SecureXmlParserFactory
 */
package com.atlassian.confluence.plugins.conversion.extract.xml;

import com.atlassian.security.xml.SecureXmlParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.SAXException;

public class SecureXmlUtils {
    public static XmlOptions createSecureXmlOptions() throws SAXException, ParserConfigurationException {
        XmlOptions opts = new XmlOptions();
        opts.setLoadUseXMLReader(SecureXmlParserFactory.newXmlReader());
        return opts;
    }
}

