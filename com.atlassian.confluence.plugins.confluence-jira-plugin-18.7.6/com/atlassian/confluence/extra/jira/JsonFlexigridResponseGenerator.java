/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.http.trust.TrustedConnectionStatus
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.security.auth.trustedapps.TransportErrorMessage
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.FlexigridResponseGenerator;
import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesDateFormatter;
import com.atlassian.confluence.extra.jira.columns.JiraIssuesXmlTransformer;
import com.atlassian.confluence.extra.jira.util.JiraIssueDateUtil;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.http.trust.TrustedConnectionStatus;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonFlexigridResponseGenerator
implements FlexigridResponseGenerator {
    private static final Logger log = LoggerFactory.getLogger(JsonFlexigridResponseGenerator.class);
    private static final String DUE_DATE_CONSTANT = "due";
    private final JiraIssuesXmlTransformer xmlXformer;
    private final I18nResolver i18nResolver;
    private final JiraIssuesManager jiraIssuesManager;
    private final JiraIssuesColumnManager jiraIssuesColumnManager;
    private final JiraIssuesDateFormatter jiraIssuesDateFormatter;
    private final TimeZoneManager timeZoneManager;
    private Locale userLocale;

    public JsonFlexigridResponseGenerator(I18nResolver i18nResolver, JiraIssuesManager jiraIssuesManager, JiraIssuesColumnManager jiraIssuesColumnManager, JiraIssuesDateFormatter jiraIssuesDateFormatter, TimeZoneManager timeZoneManager) {
        this.i18nResolver = i18nResolver;
        this.jiraIssuesManager = jiraIssuesManager;
        this.jiraIssuesColumnManager = jiraIssuesColumnManager;
        this.jiraIssuesDateFormatter = jiraIssuesDateFormatter;
        this.xmlXformer = new JiraIssuesXmlTransformer(this.jiraIssuesManager);
        this.timeZoneManager = timeZoneManager;
    }

    public boolean handles(Channel channel) {
        return true;
    }

    private String getText(String i18nKey) {
        return this.i18nResolver.getText(i18nKey);
    }

    private String trustedStatusToMessage(TrustedConnectionStatus trustedConnectionStatus) {
        if (trustedConnectionStatus != null) {
            if (!trustedConnectionStatus.isTrustSupported()) {
                return this.getText("jiraissues.server.trust.unsupported");
            }
            if (trustedConnectionStatus.isTrustedConnectionError()) {
                if (!trustedConnectionStatus.isAppRecognized()) {
                    String linkText = this.getText("jiraissues.server.trust.not.established");
                    String anonymousWarning = this.getText("jiraissues.anonymous.results.warning");
                    return "<a href=\"http://www.atlassian.com/software/jira/docs/latest/trusted_applications.html\">" + linkText + "</a> " + anonymousWarning;
                }
                if (!trustedConnectionStatus.isUserRecognized()) {
                    return this.getText("jiraissues.server.user.not.recognised");
                }
                List trustedErrorsList = trustedConnectionStatus.getTrustedTransportErrorMessages();
                if (!trustedErrorsList.isEmpty()) {
                    StringBuilder errors = new StringBuilder();
                    errors.append(this.getText("jiraissues.server.errors.reported"));
                    Iterator trustedErrorsListIterator = trustedErrorsList.iterator();
                    errors.append("<ul>");
                    while (trustedErrorsListIterator.hasNext()) {
                        errors.append("<li>");
                        errors.append(((TransportErrorMessage)trustedErrorsListIterator.next()).getFormattedMessage());
                        errors.append("</li>");
                    }
                    errors.append("</ul>");
                    return errors.toString();
                }
            }
        }
        return null;
    }

    private String createImageTag(String iconUrl, String altText) {
        StringBuilder imageTagBuilder = new StringBuilder();
        if (StringUtils.isNotBlank((CharSequence)iconUrl)) {
            imageTagBuilder.append("<img src=\"").append(iconUrl).append("\" alt=\"").append(altText).append("\"/>");
        }
        return imageTagBuilder.toString();
    }

    protected DateFormat getDateValueFormat() {
        return new SimpleDateFormat("dd/MMM/yy", this.getUserLocale());
    }

    protected String getElementJson(Element itemElement, Collection<String> columnNames, Map<String, String> columnMap, boolean fromApplink) throws Exception {
        Element keyElement = itemElement.getChild("key");
        String key = null != keyElement ? keyElement.getValue() : "";
        String link = itemElement.getChild("link").getValue();
        StringBuilder jsonIssueElementBuilder = new StringBuilder();
        jsonIssueElementBuilder.append("{id:'").append(key).append("',cell:[");
        Iterator<String> columnNamesIterator = columnNames.iterator();
        while (columnNamesIterator.hasNext()) {
            String columnName = columnNamesIterator.next();
            if (this.jiraIssuesColumnManager.isBuiltInColumnMultivalue(columnName)) {
                this.appendMultivalueBuiltinColumn(itemElement, columnName, jsonIssueElementBuilder);
            } else {
                Element fieldValue;
                String value;
                Element child = itemElement.getChild(columnName);
                String string = value = null != child ? child.getValue() : "";
                if (!(columnName.equalsIgnoreCase("created") || columnName.equalsIgnoreCase("updated") || columnName.equalsIgnoreCase(DUE_DATE_CONSTANT))) {
                    value = StringEscapeUtils.escapeEcmaScript((String)StringEscapeUtils.escapeHtml4((String)value));
                }
                if (columnName.equalsIgnoreCase("type")) {
                    jsonIssueElementBuilder.append("'<a href=\"").append(link).append("\" >").append(this.createImageTag(this.xmlXformer.findIconUrl(child), value)).append("</a>'");
                } else if (columnName.equalsIgnoreCase("key") || columnName.equals("summary")) {
                    jsonIssueElementBuilder.append("'<a href=\"").append(link).append("\" >").append(value).append("</a>'");
                } else if (columnName.equalsIgnoreCase("priority")) {
                    jsonIssueElementBuilder.append("'").append(this.createImageTag(this.xmlXformer.findIconUrl(child), value)).append("'");
                } else if (columnName.equalsIgnoreCase("status")) {
                    this.appendIssueStatus(child, value, jsonIssueElementBuilder);
                } else if (columnName.equalsIgnoreCase("created") || columnName.equalsIgnoreCase("updated")) {
                    this.appendIssueDate(value, jsonIssueElementBuilder);
                } else if (columnName.equalsIgnoreCase(DUE_DATE_CONSTANT)) {
                    String createdValue = itemElement.getChild("created") != null ? itemElement.getChild("created").getValue() : null;
                    String correctedDueDate = JiraIssueDateUtil.applyDateFixes(this.jiraIssuesDateFormatter, value, createdValue, this.getUserLocale());
                    this.appendDueDate(correctedDueDate, jsonIssueElementBuilder, fromApplink);
                } else if (columnName.equals("description")) {
                    fieldValue = this.xmlXformer.valueForField(itemElement, columnName, columnMap);
                    String description = fieldValue.getValue();
                    if (!fromApplink) {
                        description = GeneralUtil.htmlEncode((String)description);
                    }
                    jsonIssueElementBuilder.append("'").append(StringEscapeUtils.escapeEcmaScript((String)description)).append("'");
                } else if (this.jiraIssuesColumnManager.isColumnBuiltIn(columnName)) {
                    fieldValue = this.xmlXformer.valueForField(itemElement, columnName, columnMap);
                    jsonIssueElementBuilder.append("'").append(GeneralUtil.htmlEncode((String)fieldValue.getValue())).append("'");
                } else {
                    this.appendCustomField(itemElement, columnMap, columnName, jsonIssueElementBuilder, fromApplink);
                }
            }
            if (columnNamesIterator.hasNext()) {
                jsonIssueElementBuilder.append(',');
                continue;
            }
            jsonIssueElementBuilder.append("]}\n");
        }
        return jsonIssueElementBuilder.toString();
    }

    private void appendCustomField(Element itemElement, Map<String, String> columnMap, String columnName, StringBuilder jsonIssueElementBuilder, boolean fromApplink) {
        block7: {
            Element fieldValue = this.xmlXformer.valueForField(itemElement, columnName, columnMap);
            String fieldValueText = fieldValue.getValue();
            if (StringUtils.isNotBlank((CharSequence)fieldValueText)) {
                String date = this.jiraIssuesDateFormatter.reformatDateInUserLocale(fieldValueText, this.getUserLocale(), "dd/MMM/yy");
                if (StringUtils.isNotEmpty((CharSequence)date)) {
                    jsonIssueElementBuilder.append("'").append(date).append("'");
                } else {
                    try {
                        String convertedDate = this.jiraIssuesDateFormatter.reformatDateInDefaultLocale(fieldValueText, this.getUserLocale(), "dd/MMM/yy");
                        if (StringUtils.isNotBlank((CharSequence)convertedDate)) {
                            jsonIssueElementBuilder.append("'").append(convertedDate).append("'");
                            break block7;
                        }
                        this.appendCustomFieldUnformatted(fieldValueText, jsonIssueElementBuilder, fromApplink);
                    }
                    catch (DateTimeParseException pe) {
                        log.debug("Unable to parse " + fieldValue.getText() + " into a date", (Throwable)pe);
                        this.appendCustomFieldUnformatted(fieldValueText, jsonIssueElementBuilder, fromApplink);
                    }
                }
            } else {
                this.appendCustomFieldUnformatted(fieldValueText, jsonIssueElementBuilder, fromApplink);
            }
        }
    }

    private void appendCustomFieldUnformatted(String fieldValueText, StringBuilder jsonIssueElementBuilder, boolean fromAppLink) {
        if (!fromAppLink) {
            fieldValueText = StringEscapeUtils.escapeHtml4((String)fieldValueText);
        }
        jsonIssueElementBuilder.append("'").append(StringEscapeUtils.escapeEcmaScript((String)fieldValueText)).append("'");
    }

    private void appendDueDate(String value, StringBuilder jsonIssueElementBuilder, boolean fromApplink) throws ParseException {
        if (StringUtils.isEmpty((CharSequence)value)) {
            jsonIssueElementBuilder.append("''");
            return;
        }
        if (JiraIssueDateUtil.isValidDate(value, this.getUserLocale())) {
            log.debug("The provided date is a valid date in the user locale: " + value);
            jsonIssueElementBuilder.append("'").append(value).append("'");
            return;
        }
        log.debug("The provided date is NOT a valid date in the user locale: " + value);
        this.appendCustomFieldUnformatted(value, jsonIssueElementBuilder, fromApplink);
    }

    private void appendIssueDate(String value, StringBuilder jsonIssueElementBuilder) throws DateTimeParseException {
        if (StringUtils.isNotEmpty((CharSequence)value)) {
            ZonedDateTime mailFormatDate = ZonedDateTime.parse(value.trim(), JiraIssueDateUtil.MAIL_DATE_TIME_FORMATTER.withLocale(this.getUserLocale()));
            String dateTimeValue = mailFormatDate.format(JiraIssueDateUtil.DATE_TIME_VALUE_FORMATTER.withLocale(this.getUserLocale()).withZone(this.timeZoneManager.getUserTimeZone().toZoneId()));
            jsonIssueElementBuilder.append("'").append(dateTimeValue).append("'");
        } else {
            jsonIssueElementBuilder.append("''");
        }
    }

    private void appendIssueStatus(Element child, String value, StringBuilder jsonIssueElementBuilder) {
        String imgTag = this.createImageTag(this.xmlXformer.findIconUrl(child), value);
        jsonIssueElementBuilder.append("'");
        if (StringUtils.isNotBlank((CharSequence)imgTag)) {
            jsonIssueElementBuilder.append(imgTag).append(" ");
        }
        jsonIssueElementBuilder.append(value).append("'");
    }

    private void appendMultivalueBuiltinColumn(Element itemElement, String columnName, StringBuilder jsonIssueElementBuilder) {
        jsonIssueElementBuilder.append("'");
        String fieldValue = StringEscapeUtils.escapeEcmaScript((String)this.xmlXformer.collapseMultiple(itemElement, columnName).getValue());
        jsonIssueElementBuilder.append(GeneralUtil.htmlEncode((String)fieldValue));
        jsonIssueElementBuilder.append("'");
    }

    private String getOutputAsString(String url, Channel jiraResponseChannel, Collection<String> columnNames, int requestedPage, boolean showCount, boolean fromApplink) throws Exception {
        String count;
        Element jiraResponseElement = jiraResponseChannel.getChannelElement();
        HashMap<String, String> columnMap = new HashMap<String, String>();
        List itemElements = jiraResponseElement.getChildren("item");
        String language = jiraResponseElement.getChildText("language");
        if (StringUtils.isNotEmpty((CharSequence)language)) {
            if (language.contains("-")) {
                this.setUserLocale(new Locale(language.substring(0, 2), language.substring(language.indexOf(45) + 1)));
            } else {
                this.setUserLocale(new Locale(language));
            }
        } else {
            this.setUserLocale(Locale.getDefault());
        }
        Element totalItemsElement = jiraResponseElement.getChild("issue");
        String string = count = totalItemsElement != null ? totalItemsElement.getAttributeValue("total") : String.valueOf(itemElements.size());
        if (showCount) {
            return count;
        }
        StringBuilder jiraResponseJsonBuilder = new StringBuilder();
        String trustedMessage = this.trustedStatusToMessage(jiraResponseChannel.getTrustedConnectionStatus());
        if (StringUtils.isNotBlank((CharSequence)trustedMessage)) {
            trustedMessage = jiraResponseJsonBuilder.append("'").append(StringEscapeUtils.escapeEcmaScript((String)trustedMessage)).append("'").toString();
            jiraResponseJsonBuilder.setLength(0);
        }
        jiraResponseJsonBuilder.append("{\npage: ").append(requestedPage).append(",\n").append("total: ").append(count).append(",\n").append("trustedMessage: ").append(trustedMessage).append(",\n").append("rows: [\n");
        Iterator itemIterator = itemElements.iterator();
        while (itemIterator.hasNext()) {
            jiraResponseJsonBuilder.append(this.getElementJson((Element)itemIterator.next(), columnNames, columnMap, fromApplink));
            if (itemIterator.hasNext()) {
                jiraResponseJsonBuilder.append(',');
            }
            jiraResponseJsonBuilder.append('\n');
        }
        jiraResponseJsonBuilder.append("]}");
        this.jiraIssuesManager.setColumnMap(url, columnMap);
        return jiraResponseJsonBuilder.toString();
    }

    @Override
    public String generate(Channel jiraResponseChannel, Collection<String> columnNames, int requestedPage, boolean showCount, boolean fromApplink) throws IOException {
        try {
            return this.getOutputAsString(jiraResponseChannel.getSourceUrl(), jiraResponseChannel, columnNames, requestedPage, showCount, fromApplink);
        }
        catch (Exception e) {
            throw new IOException("Unable to generate JSON output", e);
        }
    }

    public Locale getUserLocale() {
        return this.userLocale;
    }

    public void setUserLocale(Locale userLocale) {
        this.userLocale = userLocale;
    }
}

