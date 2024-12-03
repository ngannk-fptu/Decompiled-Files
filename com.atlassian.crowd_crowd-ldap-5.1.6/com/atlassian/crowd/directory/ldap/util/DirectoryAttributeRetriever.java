/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.ldap.UncategorizedLdapException
 */
package com.atlassian.crowd.directory.ldap.util;

import com.atlassian.crowd.directory.ldap.util.GuidHelper;
import com.atlassian.crowd.directory.ldap.util.XmlValidator;
import javax.annotation.Nullable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ldap.UncategorizedLdapException;

public class DirectoryAttributeRetriever {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryAttributeRetriever.class);
    private static final String NULL_OR_EMPTY_ATTRIBUTE_VALUE_PLACEHOLDER = " ";

    public static String getValueFromAttributes(String directoryAttributeName, Attributes directoryAttributes) {
        if (StringUtils.isBlank((CharSequence)directoryAttributeName)) {
            return null;
        }
        String value = null;
        Attribute values = directoryAttributes.get(directoryAttributeName);
        if (values != null && values.size() > 0) {
            try {
                Object attributeValue = values.get(0);
                if (attributeValue != null && (value = attributeValue.toString()) != null && !XmlValidator.isSafe(value)) {
                    String currentLdapContext = MDC.get((String)"crowd.ldap.context");
                    String ldapContextMessage = currentLdapContext != null ? "Context: <" + currentLdapContext + ">. " : "";
                    logger.info("Unsafe attribute value <{}> for attribute <{}>. {}. Attribute was skipped.", new Object[]{StringEscapeUtils.escapeJava((String)value), directoryAttributeName, ldapContextMessage});
                    value = null;
                }
            }
            catch (NamingException e) {
                throw new UncategorizedLdapException((Throwable)e);
            }
        }
        return DirectoryAttributeRetriever.fromSavedLDAPValue(value);
    }

    @Nullable
    public static String getValueFromExternalIdAttribute(String externalIdAttribute, Attributes directoryAttributes) {
        if (StringUtils.isBlank((CharSequence)externalIdAttribute)) {
            return null;
        }
        Attribute values = directoryAttributes.get(externalIdAttribute);
        if (values == null || values.size() == 0) {
            return null;
        }
        if (values.size() > 1) {
            logger.info("Skipping attribute {} because it is multi-valued", (Object)externalIdAttribute);
            return null;
        }
        try {
            Object attributeValue = values.get(0);
            if (attributeValue instanceof String) {
                String stringValue = (String)attributeValue;
                if (stringValue.isEmpty()) {
                    return null;
                }
                if (XmlValidator.isSafe(stringValue)) {
                    return stringValue;
                }
                logger.info("Skipping attribute {} because its value <{}> is not XML safe", (Object)externalIdAttribute, (Object)StringEscapeUtils.escapeJava((String)stringValue));
                return null;
            }
            if (attributeValue instanceof byte[]) {
                String guidAsString = GuidHelper.getGUIDAsString((byte[])attributeValue);
                if (guidAsString.isEmpty()) {
                    return null;
                }
                return guidAsString;
            }
            if (attributeValue == null) {
                return null;
            }
            logger.info("Skipping attribute {} because its value <{}> is not a String or a byte array", (Object)externalIdAttribute, (Object)attributeValue.toString());
            return null;
        }
        catch (NamingException e) {
            throw new UncategorizedLdapException((Throwable)e);
        }
    }

    public static String toSaveableLDAPValue(String value) {
        if (NULL_OR_EMPTY_ATTRIBUTE_VALUE_PLACEHOLDER.equals(value)) {
            throw new IllegalArgumentException("value '" + value + "' conflicts with the placeholder value that is stored for a blank or null (deleted) LDAP attribute");
        }
        if (value == null || value.isEmpty()) {
            return NULL_OR_EMPTY_ATTRIBUTE_VALUE_PLACEHOLDER;
        }
        return value;
    }

    private static String fromSavedLDAPValue(String value) {
        if (NULL_OR_EMPTY_ATTRIBUTE_VALUE_PLACEHOLDER.equals(value)) {
            return "";
        }
        return value;
    }
}

