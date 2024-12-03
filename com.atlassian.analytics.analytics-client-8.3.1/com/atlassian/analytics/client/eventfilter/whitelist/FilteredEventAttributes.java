/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.analytics.client.eventfilter.whitelist;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class FilteredEventAttributes {
    private static final String NUMERIC_IDENTIFIER = "*";
    private static final String NUMBER_PATTERN = "-?\\d(\\.?\\d+)?";
    private static final Pattern numericPattern = Pattern.compile("\\d+");
    private static final Pattern arrayPattern = Pattern.compile("\\[\\d+\\]");
    private static final Pattern intMapKeyPattern = Pattern.compile("\\.-?\\d(\\.?\\d+)?");
    @JsonProperty
    private List<String> whitelistedAttributes;
    @JsonProperty
    private List<String> hashedAttributes;
    @JsonProperty
    private List<String> dictionaryFilteredAttributes;
    private Map<String, List<String>> allowedPropertyValues;

    public void setWhitelistedAttributes(List<String> whitelistedAttributes) {
        this.whitelistedAttributes = whitelistedAttributes;
    }

    public void setHashedAttributes(List<String> hashedAttributes) {
        this.hashedAttributes = hashedAttributes;
    }

    public void setDictionaryFilteredAttributes(List<String> dictionaryFilteredAttributes) {
        this.dictionaryFilteredAttributes = dictionaryFilteredAttributes;
    }

    public void setAllowedPropertyValues(Map<String, List<String>> allowedPropertyValues) {
        this.allowedPropertyValues = allowedPropertyValues;
    }

    public boolean hasAllowedAttributeValue(String attributeName, String attributeValue) {
        return this.allowedPropertyValues != null && (this.listContainsAttribute(this.allowedPropertyValues.get(attributeName), attributeValue) || this.listContainsAttribute(this.allowedPropertyValues.get(FilteredEventAttributes.getResolvedAttributeName(attributeName)), attributeValue));
    }

    public boolean hasWhitelistedAttribute(String attributeName) {
        return this.listContainsAttribute(this.whitelistedAttributes, attributeName);
    }

    public boolean hasHashedAttribute(String attributeName) {
        return this.listContainsAttribute(this.hashedAttributes, attributeName);
    }

    public boolean hasDictionaryFilteredAttribute(String attributeName) {
        return this.listContainsAttribute(this.dictionaryFilteredAttributes, attributeName);
    }

    private boolean listContainsAttribute(List<String> filterList, String attributeValue) {
        return filterList != null && (filterList.contains(attributeValue) || filterList.contains(FilteredEventAttributes.getResolvedAttributeName(attributeValue)) || filterList.contains(FilteredEventAttributes.getBaseAttributeName(attributeValue)));
    }

    private static String getBaseAttributeName(String attributeValue) {
        String tmp = arrayPattern.matcher(attributeValue).replaceAll("");
        return intMapKeyPattern.matcher(tmp).replaceAll("");
    }

    private static String getResolvedAttributeName(String attributeValue) {
        Matcher matcher = numericPattern.matcher(attributeValue);
        while (matcher.find()) {
            String arrayPattern = matcher.group();
            attributeValue = StringUtils.replaceOnce((String)attributeValue, (String)arrayPattern, (String)NUMERIC_IDENTIFIER);
        }
        return attributeValue;
    }

    public List<String> getWhitelistedAttributes() {
        return this.whitelistedAttributes;
    }

    List<String> getHashedAttributes() {
        return this.hashedAttributes;
    }

    List<String> getDictionaryFilteredAttributes() {
        return this.dictionaryFilteredAttributes;
    }

    Map<String, List<String>> getAllowedPropertyValues() {
        return this.allowedPropertyValues;
    }
}

