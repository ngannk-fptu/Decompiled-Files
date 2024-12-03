/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;

public class XMLResourceDescriptor {
    public static final String XML_PARSER_CLASS_NAME_KEY = "org.xml.sax.driver";
    public static final String CSS_PARSER_CLASS_NAME_KEY = "org.w3c.css.sac.driver";
    public static final String RESOURCES = "resources/XMLResourceDescriptor.properties";
    protected static Properties parserProps = null;
    protected static String xmlParserClassName;
    protected static String cssParserClassName;

    protected static synchronized Properties getParserProps() {
        if (parserProps != null) {
            return parserProps;
        }
        parserProps = new Properties();
        try {
            Class<XMLResourceDescriptor> cls = XMLResourceDescriptor.class;
            InputStream is = cls.getResourceAsStream(RESOURCES);
            parserProps.load(is);
        }
        catch (IOException ioe) {
            throw new MissingResourceException(ioe.getMessage(), RESOURCES, null);
        }
        return parserProps;
    }

    public static String getXMLParserClassName() {
        if (xmlParserClassName == null) {
            xmlParserClassName = XMLResourceDescriptor.getParserProps().getProperty(XML_PARSER_CLASS_NAME_KEY);
        }
        return xmlParserClassName;
    }

    public static void setXMLParserClassName(String xmlParserClassName) {
        XMLResourceDescriptor.xmlParserClassName = xmlParserClassName;
    }

    public static String getCSSParserClassName() {
        if (cssParserClassName == null) {
            cssParserClassName = XMLResourceDescriptor.getParserProps().getProperty(CSS_PARSER_CLASS_NAME_KEY);
        }
        return cssParserClassName;
    }

    public static void setCSSParserClassName(String cssParserClassName) {
        XMLResourceDescriptor.cssParserClassName = cssParserClassName;
    }
}

