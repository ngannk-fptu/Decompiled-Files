/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.dom4j.IllegalAddException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.output;

import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.dom4j.IllegalAddException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertiesStoreParser {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesStoreParser.class);
    private static final String ATTRIBUTE_KEY = "attribute.";
    private static final int ATTRIBUTE_OFFSET = "attribute.".length();
    private static final String NAME_START_CHAR = "_A-Za-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf\ufdf0-\ufffd";
    private static final String NAME_CHAR = "_A-Za-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf\ufdf0-\ufffd.0-9\u00b7\u0300-\u036f\u203f-\u2040-";
    private static final Pattern NAME_START_CHAR_PATTERN = Pattern.compile("[^_A-Za-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf\ufdf0-\ufffd]");
    private static final Pattern NAME_CHAR_PATTERN = Pattern.compile("[^_A-Za-z\u00c0-\u00d6\u00d8-\u00f6\u00f8-\u02ff\u0370-\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf\ufdf0-\ufffd.0-9\u00b7\u0300-\u036f\u203f-\u2040-]");

    private PropertiesStoreParser() {
    }

    public static void loadStore(PropertyStore store, Element element, Properties xmlElementNamesMapper) {
        PropertiesStoreParser.loadValues(store, element, xmlElementNamesMapper);
        PropertiesStoreParser.loadCategories(store, element, xmlElementNamesMapper);
    }

    private static void loadValues(PropertyStore store, Element element, Properties xmlElementNamesMapper) {
        UnaryOperator keyMapper = key -> xmlElementNamesMapper.getProperty((String)key, (String)key);
        for (Map.Entry<String, String> entry : store.getValues().entrySet()) {
            if (StringUtils.isEmpty((CharSequence)entry.getValue())) continue;
            String key2 = (String)keyMapper.apply(entry.getKey());
            try {
                if (key2.startsWith(ATTRIBUTE_KEY)) {
                    String attributeKey = key2.substring(ATTRIBUTE_OFFSET);
                    element.addAttribute(PropertiesStoreParser.escapeKeysForXMLOutput(attributeKey), entry.getValue());
                    continue;
                }
                Element valueElement = element.addElement(PropertiesStoreParser.escapeKeysForXMLOutput(key2));
                valueElement.setText(entry.getValue());
            }
            catch (IllegalAddException e) {
                LOG.error("Unable to add child element '{}' to element '{}'...", new Object[]{key2, element.getName(), e});
            }
            catch (IllegalArgumentException iae) {
                PropertiesStoreParser.handleBadTagNameException(key2, element, iae);
            }
        }
    }

    private static void loadCategories(PropertyStore store, Element element, Properties xmlElementNamesMapper) {
        for (Map.Entry<String, List<PropertyStore>> entry : store.getCategories().entrySet()) {
            String key = xmlElementNamesMapper.getProperty(entry.getKey(), entry.getKey());
            Iterable categoryList = entry.getValue();
            for (PropertyStore childObject : categoryList) {
                if (childObject != null) {
                    try {
                        Element listChildElement = element.addElement(PropertiesStoreParser.escapeKeysForXMLOutput(key));
                        PropertiesStoreParser.loadStore(childObject, listChildElement, xmlElementNamesMapper);
                    }
                    catch (IllegalArgumentException iae) {
                        PropertiesStoreParser.handleBadTagNameException(key, element, iae);
                    }
                    continue;
                }
                LOG.warn("Couldn't add child object of type '{}' with key '{}' to PropertyStore.", (Object)entry.getValue().getClass().getCanonicalName(), (Object)key);
            }
        }
    }

    private static void handleBadTagNameException(String key, Element element, Exception e) {
        LOG.info("Did not add child element '{}' to element '{}' making application.xml for the support zip.", new Object[]{key, element.getName(), e});
    }

    private static String escapeKeysForXMLOutput(String key) {
        String escapedKey = key;
        if (!(escapedKey = escapedKey.replace(' ', '-')).isEmpty() && NAME_START_CHAR_PATTERN.matcher(escapedKey.subSequence(0, 1)).matches()) {
            escapedKey = "x" + escapedKey;
        }
        escapedKey = NAME_CHAR_PATTERN.matcher(escapedKey).replaceAll(".");
        return escapedKey;
    }
}

