/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v1.xml.DomParseUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.cfg;

import com.mchange.v1.xml.DomParseUtils;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.cfg.C3P0ConfigUtils;
import com.mchange.v2.c3p0.cfg.NamedScope;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class C3P0ConfigXmlUtils {
    public static final String XML_CONFIG_RSRC_PATH = "/c3p0-config.xml";
    static final MLogger logger;
    public static final String LINESEP;
    private static final String[] MISSPELL_PFXS;
    private static final char[] MISSPELL_LINES;
    private static final String[] MISSPELL_CONFIG;
    private static final String[] MISSPELL_XML;

    private static final void warnCommonXmlConfigResourceMisspellings() {
        if (logger.isLoggable(MLevel.WARNING)) {
            int lena = MISSPELL_PFXS.length;
            for (int a = 0; a < lena; ++a) {
                StringBuffer sb = new StringBuffer(16);
                sb.append(MISSPELL_PFXS[a]);
                int lenb = MISSPELL_LINES.length;
                for (int b = 0; b < lenb; ++b) {
                    sb.append(MISSPELL_LINES[b]);
                    int lenc = MISSPELL_CONFIG.length;
                    for (int c = 0; c < lenc; ++c) {
                        sb.append(MISSPELL_CONFIG[c]);
                        sb.append('.');
                        int lend = MISSPELL_XML.length;
                        for (int d = 0; d < lend; ++d) {
                            URL hopefullyNull;
                            sb.append(MISSPELL_XML[d]);
                            String test = sb.toString();
                            if (test.equals(XML_CONFIG_RSRC_PATH) || (hopefullyNull = C3P0ConfigXmlUtils.class.getResource(test)) == null) continue;
                            logger.warning("POSSIBLY MISSPELLED c3p0-conf.xml RESOURCE FOUND. Please ensure the file name is c3p0-config.xml, all lower case, with the digit 0 (NOT the letter O) in c3p0. It should be placed  in the top level of c3p0's effective classpath.");
                            return;
                        }
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static C3P0Config extractXmlConfigFromDefaultResource(boolean usePermissiveParser) throws Exception {
        InputStream is = null;
        try {
            is = C3P0ConfigUtils.class.getResourceAsStream(XML_CONFIG_RSRC_PATH);
            if (is == null) {
                C3P0ConfigXmlUtils.warnCommonXmlConfigResourceMisspellings();
                C3P0Config c3P0Config = null;
                return c3P0Config;
            }
            C3P0Config c3P0Config = C3P0ConfigXmlUtils.extractXmlConfigFromInputStream(is, usePermissiveParser);
            return c3P0Config;
        }
        finally {
            block12: {
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (Exception e) {
                    if (!logger.isLoggable(MLevel.FINE)) break block12;
                    logger.log(MLevel.FINE, "Exception on resource InputStream close.", (Throwable)e);
                }
            }
        }
    }

    private static void attemptSetFeature(DocumentBuilderFactory dbf, String featureUri, boolean setting) {
        block2: {
            try {
                dbf.setFeature(featureUri, setting);
            }
            catch (ParserConfigurationException e) {
                if (!logger.isLoggable(MLevel.FINE)) break block2;
                logger.log(MLevel.FINE, "Attempted but failed to set presumably unsupported feature '" + featureUri + "' to " + setting + ".");
            }
        }
    }

    private static void cautionDocumentBuilderFactory(DocumentBuilderFactory dbf) {
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://apache.org/xml/features/disallow-doctype-decl", true);
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://xerces.apache.org/xerces-j/features.html#external-general-entities", false);
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://xerces.apache.org/xerces2-j/features.html#external-general-entities", false);
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://xml.org/sax/features/external-general-entities", false);
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://xerces.apache.org/xerces-j/features.html#external-parameter-entities", false);
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities", false);
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://xml.org/sax/features/external-parameter-entities", false);
        C3P0ConfigXmlUtils.attemptSetFeature(dbf, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
    }

    public static C3P0Config extractXmlConfigFromInputStream(InputStream is, boolean usePermissiveParser) throws Exception {
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        if (!usePermissiveParser) {
            C3P0ConfigXmlUtils.cautionDocumentBuilderFactory(fact);
        }
        DocumentBuilder db = fact.newDocumentBuilder();
        Document doc = db.parse(is);
        return C3P0ConfigXmlUtils.extractConfigFromXmlDoc(doc);
    }

    public static C3P0Config extractConfigFromXmlDoc(Document doc) throws Exception {
        Element docElem = doc.getDocumentElement();
        if (docElem.getTagName().equals("c3p0-config")) {
            HashMap<String, NamedScope> configNamesToNamedScopes = new HashMap<String, NamedScope>();
            Element defaultConfigElem = DomParseUtils.uniqueChild((Element)docElem, (String)"default-config");
            NamedScope defaults = defaultConfigElem != null ? C3P0ConfigXmlUtils.extractNamedScopeFromLevel(defaultConfigElem) : new NamedScope();
            NodeList nl = DomParseUtils.immediateChildElementsByTagName((Element)docElem, (String)"named-config");
            int len = nl.getLength();
            for (int i = 0; i < len; ++i) {
                Element namedConfigElem = (Element)nl.item(i);
                String configName = namedConfigElem.getAttribute("name");
                if (configName != null && configName.length() > 0) {
                    NamedScope namedConfig = C3P0ConfigXmlUtils.extractNamedScopeFromLevel(namedConfigElem);
                    configNamesToNamedScopes.put(configName, namedConfig);
                    continue;
                }
                logger.warning("Configuration XML contained named-config element without name attribute: " + namedConfigElem);
            }
            return new C3P0Config(defaults, configNamesToNamedScopes);
        }
        throw new Exception("Root element of c3p0 config xml should be 'c3p0-config', not '" + docElem.getTagName() + "'.");
    }

    private static NamedScope extractNamedScopeFromLevel(Element elem) {
        HashMap props = C3P0ConfigXmlUtils.extractPropertiesFromLevel(elem);
        HashMap<String, HashMap> userNamesToOverrides = new HashMap<String, HashMap>();
        NodeList nl = DomParseUtils.immediateChildElementsByTagName((Element)elem, (String)"user-overrides");
        int len = nl.getLength();
        for (int i = 0; i < len; ++i) {
            Element perUserConfigElem = (Element)nl.item(i);
            String userName = perUserConfigElem.getAttribute("user");
            if (userName != null && userName.length() > 0) {
                HashMap userProps = C3P0ConfigXmlUtils.extractPropertiesFromLevel(perUserConfigElem);
                userNamesToOverrides.put(userName, userProps);
                continue;
            }
            logger.warning("Configuration XML contained user-overrides element without user attribute: " + LINESEP + perUserConfigElem);
        }
        HashMap extensions = C3P0ConfigXmlUtils.extractExtensionsFromLevel(elem);
        return new NamedScope(props, userNamesToOverrides, extensions);
    }

    private static HashMap extractExtensionsFromLevel(Element elem) {
        HashMap out = new HashMap();
        NodeList nl = DomParseUtils.immediateChildElementsByTagName((Element)elem, (String)"extensions");
        int len = nl.getLength();
        for (int i = 0; i < len; ++i) {
            Element extensionsElem = (Element)nl.item(i);
            out.putAll(C3P0ConfigXmlUtils.extractPropertiesFromLevel(extensionsElem));
        }
        return out;
    }

    private static HashMap extractPropertiesFromLevel(Element elem) {
        HashMap<String, String> out = new HashMap<String, String>();
        try {
            NodeList nl = DomParseUtils.immediateChildElementsByTagName((Element)elem, (String)"property");
            int len = nl.getLength();
            for (int i = 0; i < len; ++i) {
                Element propertyElem = (Element)nl.item(i);
                String propName = propertyElem.getAttribute("name");
                if (propName != null && propName.length() > 0) {
                    String propVal = DomParseUtils.allTextFromElement((Element)propertyElem, (boolean)true);
                    out.put(propName, propVal);
                    continue;
                }
                logger.warning("Configuration XML contained property element without name attribute: " + LINESEP + propertyElem);
            }
        }
        catch (Exception e) {
            logger.log(MLevel.WARNING, "An exception occurred while reading config XML. Some configuration information has probably been ignored.", (Throwable)e);
        }
        return out;
    }

    private C3P0ConfigXmlUtils() {
    }

    static {
        String ls;
        logger = MLog.getLogger(C3P0ConfigXmlUtils.class);
        MISSPELL_PFXS = new String[]{"/c3p0", "/c3pO", "/c3po", "/C3P0", "/C3PO"};
        MISSPELL_LINES = new char[]{'-', '_'};
        MISSPELL_CONFIG = new String[]{"config", "CONFIG"};
        MISSPELL_XML = new String[]{"xml", "XML"};
        try {
            ls = System.getProperty("line.separator", "\r\n");
        }
        catch (Exception e) {
            ls = "\r\n";
        }
        LINESEP = ls;
    }
}

