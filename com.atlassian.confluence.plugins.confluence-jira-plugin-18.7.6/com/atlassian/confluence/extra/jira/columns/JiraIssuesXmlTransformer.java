/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.jdom.Attribute
 *  org.jdom.Element
 *  org.jdom.filter.ElementFilter
 *  org.jdom.filter.Filter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.columns;

import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.util.JiraIssueDateUtil;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraIssuesXmlTransformer {
    private static final Logger logger = LoggerFactory.getLogger(JiraIssuesXmlTransformer.class);
    private static final String MAIL_DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";
    private static final DateTimeFormatter DEFAULT_JIRA_XML_DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
    private static final String USER_PICKER_KEY = "com.atlassian.jira.plugin.system.customfieldtypes:userpicker";
    private static final String MULTI_USER_PICKER_KEY = "com.atlassian.jira.plugin.system.customfieldtypes:multiuserpicker";
    private static final String DISPLAY_NAME = "displayname";
    private final JiraIssuesManager jiraIssuesManager;

    public JiraIssuesXmlTransformer(JiraIssuesManager jiraIssuesManager) {
        this.jiraIssuesManager = jiraIssuesManager;
    }

    public Element collapseMultiple(Element rootElement, String childName) {
        Element result;
        if (childName.equalsIgnoreCase("comments") || childName.equalsIgnoreCase("attachments")) {
            int count;
            result = new Element(rootElement.getName());
            Element child = rootElement.getChild(childName);
            if (child != null && (count = child.getChildren().size()) != 0) {
                result.setText(Integer.toString(count));
            }
        } else {
            result = this.collapseMultiple(rootElement, childName, ", ");
        }
        return result;
    }

    public Element valueForField(Element rootElement, String fieldName) {
        return this.valueForField(rootElement, fieldName, null);
    }

    public String valueForHTMLField(Element rootElement, String fieldName, String currentServerBaseUrl) {
        String htmlElementName = "img";
        String htmlAttrName = "src";
        Element fieldElement = this.valueForField(rootElement, fieldName, null);
        if (fieldElement == null) {
            logger.debug("Skip HTML parsing for null description field");
            return "";
        }
        String returnValue = fieldElement.getValue();
        if (StringUtils.isEmpty((CharSequence)returnValue)) {
            logger.debug("Skip HTML parsing for empty description field");
            return returnValue;
        }
        if (!returnValue.contains("<img")) {
            logger.debug("Skip HTML parsing for description field which does not have images");
            return returnValue;
        }
        Document descriptionHtmlDoc = Jsoup.parse(returnValue);
        Elements imgs = descriptionHtmlDoc.getElementsByTag("img");
        for (org.jsoup.nodes.Element img : imgs) {
            Object srcValue = img.attr("src");
            if (StringUtils.isEmpty((CharSequence)srcValue)) {
                logger.debug("Skip updating src attribute because it is empty");
                continue;
            }
            if (!((String)srcValue).startsWith("/")) {
                logger.debug("Skip updating src attribute for non relative path");
                continue;
            }
            logger.debug("Updating src attribute: {}", srcValue);
            srcValue = currentServerBaseUrl + (String)srcValue;
            img.attr("src", (String)srcValue);
            logger.debug("Updated src attribute: {}", srcValue);
        }
        return descriptionHtmlDoc.html();
    }

    public String valueForFieldDateFormatted(Element rootElement, String fieldIdOrName, String userDateFormat, ZoneId userZoneId, Locale userLocale) {
        Element valueForField = this.valueForField(rootElement, fieldIdOrName);
        if (valueForField == null) {
            return null;
        }
        String value = valueForField.getValue();
        if (StringUtils.isBlank((CharSequence)value)) {
            return value;
        }
        String date = this.parseDateWithUserLocale(value, userDateFormat, userZoneId, userLocale);
        if (date != null) {
            return date;
        }
        return value;
    }

    public String valueForFieldDecimalFormatted(Element rootElement, String fieldIdOrName, String decimalFormat) {
        Element valueForField = this.valueForField(rootElement, fieldIdOrName);
        if (valueForField == null) {
            return null;
        }
        String value = valueForField.getValue();
        if (StringUtils.isBlank((CharSequence)value)) {
            return value;
        }
        DecimalFormat df = new DecimalFormat(decimalFormat);
        return df.format(Double.parseDouble(value));
    }

    public String valueForFieldDateFormattedWithTimeZoneFix(Element rootElement, String fieldIdOrName, String dateFieldKeyWithCorrectTimeZone, String userDateFormat, ZoneId userZoneId, Locale userLocale) {
        if (StringUtils.isBlank((CharSequence)dateFieldKeyWithCorrectTimeZone) || rootElement.getChild(dateFieldKeyWithCorrectTimeZone) == null) {
            return this.valueForFieldDateFormatted(rootElement, fieldIdOrName, userDateFormat, userZoneId, userLocale);
        }
        Element valueForField = this.valueForField(rootElement, fieldIdOrName);
        if (valueForField == null) {
            return null;
        }
        String value = valueForField.getValue();
        if (StringUtils.isBlank((CharSequence)value)) {
            return value;
        }
        String timeZoneCorrectedDateValue = JiraIssueDateUtil.applyDateTimezoneFix(value, rootElement.getChild(dateFieldKeyWithCorrectTimeZone).getValue(), userLocale);
        String date = this.parseDateWithUserLocale(timeZoneCorrectedDateValue, userDateFormat, userZoneId, userLocale);
        if (date != null) {
            return date;
        }
        return timeZoneCorrectedDateValue;
    }

    private String parseDateWithUserLocale(String value, String userDateFormat, ZoneId userZoneId, Locale userLocale) {
        try {
            OffsetDateTime defaultJiraXmlDate = OffsetDateTime.parse(value.trim(), DEFAULT_JIRA_XML_DATE_FORMAT);
            DateTimeFormatter userDateTimeFormatter = DateTimeFormatter.ofPattern(userDateFormat, userLocale).withZone(userZoneId);
            return defaultJiraXmlDate.format(userDateTimeFormatter);
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }

    public Element valueForField(Element rootElement, String fieldName, Map<String, String> columnMap) {
        Element result = this.findSimpleBuiltinField(rootElement, fieldName);
        if (result == null) {
            result = new Element(rootElement.getName());
            Element customFieldsElement = rootElement.getChild("customfields");
            if (customFieldsElement != null) {
                List customFieldElements = customFieldsElement.getChildren();
                StringJoiner valuesStringJoiner = new StringJoiner(", ");
                for (Element customFieldElement : customFieldElements) {
                    String customFieldName = customFieldElement.getChild("customfieldname").getValue();
                    String customFieldId = customFieldElement.getAttributeValue("id");
                    if (!customFieldId.equals(fieldName) && !customFieldName.equals(fieldName)) continue;
                    this.updateColumnMap(columnMap, customFieldId, customFieldName);
                    Element customFieldValuesElement = customFieldElement.getChild("customfieldvalues");
                    List customFieldValueElements = customFieldValuesElement.getChildren();
                    for (Element customFieldValueElement : customFieldValueElements) {
                        String displayName = "";
                        if (this.isCustomFieldAUserPicker(customFieldElement)) {
                            displayName = this.findDisplayName(customFieldValueElement);
                        }
                        if (!displayName.isEmpty()) {
                            logger.debug("Value for field is display name: " + displayName);
                            valuesStringJoiner.add(displayName);
                            continue;
                        }
                        logger.debug("Value for field is custom field value: " + customFieldValueElement.getValue());
                        valuesStringJoiner.add(customFieldValueElement.getValue());
                    }
                }
                result.setText(valuesStringJoiner.toString());
            }
        }
        return result;
    }

    private boolean isCustomFieldAUserPicker(Element customFieldValueElement) {
        for (Object attributeObject : customFieldValueElement.getAttributes()) {
            Attribute attribute = (Attribute)attributeObject;
            if (!"key".equals(attribute.getName()) || !USER_PICKER_KEY.equals(attribute.getValue()) && !MULTI_USER_PICKER_KEY.equals(attribute.getValue())) continue;
            logger.debug(customFieldValueElement.getName() + " is a user picker");
            return true;
        }
        return false;
    }

    private String findDisplayName(Element customFieldValueElement) {
        for (Object attributeObject : customFieldValueElement.getAttributes()) {
            Attribute attribute = (Attribute)attributeObject;
            if (!DISPLAY_NAME.equals(attribute.getName())) continue;
            logger.debug("Display name is: " + attribute.getValue());
            return attribute.getValue();
        }
        return "";
    }

    public Element findSimpleBuiltinField(Element rootElement, String fieldName) {
        List children = rootElement.getChildren(fieldName);
        if (children.size() == 1) {
            return (Element)children.get(0);
        }
        return null;
    }

    private void updateColumnMap(Map<String, String> columnMap, String columnId, String columnName) {
        if (columnMap != null && !columnMap.containsKey(columnName)) {
            columnMap.put(columnName, columnId);
        }
    }

    protected Element collapseMultiple(Element rootElement, String attrName, String connector) {
        Element result;
        List children = StringUtils.isNotBlank((CharSequence)attrName) ? rootElement.getChildren(attrName) : Collections.emptyList();
        if (children.size() == 1) {
            result = (Element)children.get(0);
        } else {
            result = new Element(rootElement.getName());
            StringBuilder value = new StringBuilder();
            connector = StringUtils.defaultString((String)connector);
            Iterator iter = children.iterator();
            while (iter.hasNext()) {
                Element attrElement = (Element)iter.next();
                value.append(attrElement.getValue());
                if (!iter.hasNext()) continue;
                value.append(connector);
            }
            result.setText(value.toString());
        }
        return result;
    }

    public String findIconUrl(Element xmlItemField) {
        String iconUrl = "";
        if (xmlItemField != null) {
            iconUrl = StringUtils.defaultString((String)xmlItemField.getAttributeValue("iconUrl"));
        }
        return iconUrl;
    }

    public Set<String> getIssueKeyValues(Element issueLinks) {
        HashSet<String> issueKeyValues = new HashSet<String>();
        if (issueLinks != null) {
            Iterator issueKeys = issueLinks.getDescendants((Filter)new ElementFilter("issuekey"));
            while (issueKeys.hasNext()) {
                issueKeyValues.add(((Element)issueKeys.next()).getValue());
            }
        }
        return issueKeyValues;
    }

    public String getRefineValue(Element rootElement, String fieldName) {
        return this.valueForField(rootElement, fieldName).getValue().trim().replaceAll(".*\n.*\n *", "");
    }
}

