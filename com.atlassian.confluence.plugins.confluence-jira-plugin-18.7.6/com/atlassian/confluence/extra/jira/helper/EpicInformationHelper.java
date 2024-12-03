/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  org.jdom.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.confluence.extra.jira.Channel;
import com.atlassian.confluence.extra.jira.columns.DefaultJiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.columns.Epic;
import com.atlassian.confluence.extra.jira.columns.JiraColumnInfo;
import com.atlassian.confluence.extra.jira.helper.EpicInfoRetriever;
import com.atlassian.confluence.extra.jira.helper.JiraJqlHelper;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EpicInformationHelper {
    private static final Logger logger = LoggerFactory.getLogger(EpicInformationHelper.class);
    private final EpicInfoRetriever epicInfoRetriever;
    private final String epicColourCustomFieldId;
    private final String epicNameCustomFieldId;
    private final String epicStatusCustomFieldId;

    public EpicInformationHelper(EpicInfoRetriever epicInfoRetriever, String epicNameCustomFieldId, String epicColourCustomFieldId, String epicStatusCustomFieldId) {
        this.epicInfoRetriever = epicInfoRetriever;
        this.epicColourCustomFieldId = epicColourCustomFieldId;
        this.epicNameCustomFieldId = epicNameCustomFieldId;
        this.epicStatusCustomFieldId = epicStatusCustomFieldId;
    }

    public Map<String, Epic> getEpicInformation(Channel channel) {
        HashMap<String, Epic> foundEpicKeys = new HashMap<String, Epic>();
        HashMap<String, String> issueKeyToEpicKeyMap = new HashMap<String, String>();
        Set<Object> notFoundEpics = new HashSet();
        for (Element issue : channel.getChannelElement().getChildren("item")) {
            CustomField customField;
            JiraIssueItem jiraIssueItem = new JiraIssueItem(issue);
            String epicKey = "";
            String epicName = "";
            String epicColour = "";
            String epicStatus = "";
            if (!jiraIssueItem.getIssueKey().isPresent()) {
                logger.debug("Could not find issue key from issue item [{}] xml will keep it", (Object)issue);
                continue;
            }
            boolean issueIsEpic = jiraIssueItem.getIssueType().orElse("").equalsIgnoreCase("epic");
            boolean hasAllEpicFields = false;
            if (issueIsEpic) {
                epicKey = jiraIssueItem.getIssueKey().get();
                for (Element element : issue.getChild("customfields").getChildren()) {
                    customField = new CustomField(element);
                    if (this.isEpicField(customField, this.epicNameCustomFieldId)) {
                        epicName = this.extractFieldValue(element.getValue());
                    } else if (this.isEpicField(customField, this.epicColourCustomFieldId)) {
                        epicColour = this.extractFieldValue(element.getValue());
                    } else if (this.isEpicField(customField, this.epicStatusCustomFieldId)) {
                        epicStatus = this.extractFieldValue(element.getValue());
                    }
                    if (epicName.isEmpty() || epicColour.isEmpty() || epicStatus.isEmpty()) continue;
                    hasAllEpicFields = true;
                    foundEpicKeys.put(epicKey, new Epic(epicKey, epicName, epicColour, epicStatus));
                    logger.debug("Found Epic information in XML search result");
                    break;
                }
                if (!hasAllEpicFields) {
                    logger.debug("Init Epic info {} object with missing fields", (Object)epicKey);
                    notFoundEpics.add(epicKey);
                }
                issueKeyToEpicKeyMap.put(epicKey, epicKey);
                continue;
            }
            for (Element element : issue.getChild("customfields").getChildren()) {
                customField = new CustomField(element);
                if (!DefaultJiraIssuesColumnManager.matchColumnFromSchema("com.pyxis.greenhopper.jira:gh-epic-link", new JiraColumnInfo(customField.getIdAttr().orElse(""), null, null, false, new JiraColumnInfo.JsonSchema(null, customField.getKeyAttr().orElse(""), 0, null)))) continue;
                epicKey = this.extractFieldValue(element.getValue());
                notFoundEpics.add(epicKey);
                break;
            }
            issueKeyToEpicKeyMap.put(jiraIssueItem.getIssueKey().get(), epicKey);
        }
        if ((notFoundEpics = notFoundEpics.stream().filter(notFoundEpic -> !foundEpicKeys.containsKey(notFoundEpic)).collect(Collectors.toSet())) != null && notFoundEpics.size() > 0) {
            logger.debug("Missing epic info need to request to JIRA {}", (Object)notFoundEpics.stream().collect(Collectors.joining(",")));
        }
        HashSet<String> notFoundEpicByKey = new HashSet<String>();
        HashSet<String> notFoundEpicNyName = new HashSet<String>();
        for (String string : notFoundEpics) {
            if (JiraJqlHelper.isJqlKeyType(string)) {
                notFoundEpicByKey.add(string);
                continue;
            }
            notFoundEpicNyName.add(string);
        }
        ArrayList epics = new ArrayList(foundEpicKeys.values());
        epics.addAll(this.epicInfoRetriever.getEpicInformation(notFoundEpicByKey));
        epics.addAll(this.epicInfoRetriever.getEpicInformationByEpicName(notFoundEpicNyName));
        issueKeyToEpicKeyMap.entrySet().forEach(entry -> epics.stream().filter(epicObject -> epicObject.getKey().equals(entry.getValue()) || epicObject.getName().equals(entry.getValue())).findFirst().ifPresent(foundEpic -> foundEpicKeys.put((String)entry.getKey(), (Epic)foundEpic)));
        if (logger.isDebugEnabled()) {
            String string = Joiner.on((String)",").withKeyValueSeparator("=").join(foundEpicKeys);
            logger.debug("Dump issueToEpicMapString key-value: \n \t {}", (Object)string);
        }
        return foundEpicKeys;
    }

    private boolean isEpicField(CustomField customField, String epicCustomFieldId) {
        String elementName = customField.getName();
        String idAttrString = customField.getIdAttr().orElse("");
        return elementName.equals(epicCustomFieldId) || idAttrString.equals(epicCustomFieldId);
    }

    private String extractFieldValue(String field) {
        return field.trim().replaceAll(".*\n.*\n *", "").replaceAll("\n", "").trim();
    }

    private String getElementValueOrNullDefault(Element element) {
        return element == null ? null : element.getValue();
    }

    private class XmlElement {
        String name;

        XmlElement(Element element) {
            this.setName(element.getName());
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            Objects.requireNonNull(name);
            this.name = name;
        }
    }

    private class JiraIssueItem
    extends XmlElement {
        Optional<String> issueKey;
        Optional<String> issueType;
        Optional<String> issueSummary;
        Optional<String> issueStatus;

        JiraIssueItem(Element element) {
            super(element);
            this.setIssueKey(EpicInformationHelper.this.getElementValueOrNullDefault(element.getChild("key")));
            this.setIssueType(EpicInformationHelper.this.getElementValueOrNullDefault(element.getChild("type")));
            this.setIssueSummary(EpicInformationHelper.this.getElementValueOrNullDefault(element.getChild("summary")));
            this.setIssueStatus(EpicInformationHelper.this.getElementValueOrNullDefault(element.getChild("status")));
        }

        public Optional<String> getIssueType() {
            return this.issueType;
        }

        public void setIssueType(String issueType) {
            this.issueType = Optional.ofNullable(issueType);
        }

        public Optional<String> getIssueKey() {
            return this.issueKey;
        }

        public void setIssueKey(String issueKey) {
            this.issueKey = Optional.ofNullable(issueKey);
        }

        public void setIssueSummary(String issueSummary) {
            this.issueSummary = Optional.ofNullable(issueSummary);
        }

        public void setIssueStatus(String issueStatus) {
            this.issueStatus = Optional.ofNullable(issueStatus);
        }

        public Optional<String> getIssueSummary() {
            return this.issueSummary;
        }

        public Optional<String> getIssueStatus() {
            return this.issueStatus;
        }
    }

    private class CustomField
    extends XmlElement {
        Optional<String> idAttr;
        Optional<String> keyAttr;

        CustomField(Element element) {
            super(element);
            this.setIdAttr(element.getAttributeValue("id"));
            this.setKeyAttr(element.getAttributeValue("key"));
        }

        public Optional<String> getKeyAttr() {
            return this.keyAttr;
        }

        public void setKeyAttr(String keyAttr) {
            this.keyAttr = Optional.ofNullable(keyAttr);
        }

        public Optional<String> getIdAttr() {
            return this.idAttr;
        }

        public void setIdAttr(String idAttr) {
            this.idAttr = Optional.ofNullable(idAttr);
        }
    }
}

