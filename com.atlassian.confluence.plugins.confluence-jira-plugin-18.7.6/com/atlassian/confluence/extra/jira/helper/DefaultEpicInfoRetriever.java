/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.google.common.collect.Iterators
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.DeserializationConfig$Feature
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.extra.jira.columns.Epic;
import com.atlassian.confluence.extra.jira.helper.EpicInfoRetriever;
import com.atlassian.confluence.extra.jira.helper.RestHelper;
import com.atlassian.confluence.extra.jira.helper.SearchResult;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.google.common.collect.Iterators;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEpicInfoRetriever
implements EpicInfoRetriever {
    private static Logger LOGGER = LoggerFactory.getLogger(DefaultEpicInfoRetriever.class);
    private final RestHelper restHelper;
    private final ReadOnlyApplicationLink appLink;
    private final String SEARCH_URL = "rest/api/2/search?jql=";
    private static int BATCH_SIZE = Integer.getInteger("epic.info.retriever.batch.size", 10);
    private static String searchString = "key in (%s)";
    private static String searchByEpicNameString = "issuetype = EPIC AND 'Epic Name' in (%s)";
    private final Set<String> epicCustomFieldIds;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String epicNameCustomFieldId;
    private final String epicColourCustomFieldId;
    private final String epicStatusCustomFieldId;

    public DefaultEpicInfoRetriever(ReadOnlyApplicationLink appLink, RestHelper restHelper, String epicNameCustomFieldId, String epicColourCustomFieldId, String epicStatusCustomFieldId) {
        this.appLink = appLink;
        this.restHelper = restHelper;
        this.epicNameCustomFieldId = epicNameCustomFieldId;
        this.epicColourCustomFieldId = epicColourCustomFieldId;
        this.epicStatusCustomFieldId = epicStatusCustomFieldId;
        this.epicCustomFieldIds = new HashSet<String>();
        Collections.addAll(this.epicCustomFieldIds, epicNameCustomFieldId, epicColourCustomFieldId, epicStatusCustomFieldId);
        this.objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Collection<Epic> getEpicInformation(Set<String> epicKeys) {
        LOGGER.debug("Missing Epic Information by Key: {}", epicKeys);
        return this.searchForEpicInfo(searchString, epicKeys);
    }

    @Override
    public Collection<Epic> getEpicInformationByEpicName(Set<String> epicNames) {
        LOGGER.debug("Missing Epic Information by Name: {}", epicNames);
        epicNames = epicNames.stream().map(name -> "'" + name + "'").collect(Collectors.toSet());
        return this.searchForEpicInfo(searchByEpicNameString, epicNames);
    }

    private List<Epic> searchForEpicInfo(String jql, Set<String> epicKeys) {
        String customFieldQuery = this.epicCustomFieldIds.stream().collect(Collectors.joining("&fields="));
        ArrayList<Epic> returnEpics = new ArrayList<Epic>();
        AtomicInteger currentBatch = new AtomicInteger();
        Iterators.partition(epicKeys.iterator(), (int)BATCH_SIZE).forEachRemaining(batchIssues -> {
            LOGGER.debug("Execute with batch {}", (Object)currentBatch.incrementAndGet());
            String query = String.format(jql, String.join((CharSequence)",", batchIssues));
            String encodedQuery = JiraUtil.utf8Encode(query);
            String searchUrl = "rest/api/2/search?jql=" + encodedQuery + "&fields=" + customFieldQuery + "&fields=summary";
            String jsonResponse = this.restHelper.executeRest(searchUrl, this.appLink);
            try {
                returnEpics.addAll(this.extracted(jsonResponse));
            }
            catch (IOException e) {
                LOGGER.error("Could not get SearchResult for url [{}] with return Json \n {}", (Object)searchUrl, (Object)jsonResponse);
                throw new RuntimeException(e);
            }
        });
        return returnEpics;
    }

    private List<Epic> extracted(String jsonResponse) throws IOException {
        ArrayList<Epic> returnEpics = new ArrayList<Epic>();
        if (StringUtils.isEmpty((CharSequence)jsonResponse)) {
            LOGGER.warn("JIRA return empty result");
            return returnEpics;
        }
        LOGGER.debug("Response JSON from JIRA: {}", (Object)jsonResponse);
        SearchResult searchResult = (SearchResult)this.objectMapper.readValue(jsonResponse, SearchResult.class);
        searchResult.getIssues().stream().forEach(searchIssue -> {
            String epicColor;
            Map<String, Object> fields = searchIssue.getFields();
            HashMap epicStatus = (HashMap)fields.get(this.epicStatusCustomFieldId);
            String epicName = (String)fields.get(this.epicNameCustomFieldId);
            if (StringUtils.isEmpty((CharSequence)epicName)) {
                LOGGER.warn("Could not get Epic Name from Jira (Rest call). Trying to fallback to summary field");
                epicName = (String)fields.get("summary");
            }
            if (StringUtils.isEmpty((CharSequence)(epicColor = (String)fields.get(this.epicColourCustomFieldId)))) {
                epicColor = "ghx-label-0";
                LOGGER.warn("Could not get Epic Color from Jira (Rest call)");
            } else if (!epicColor.startsWith("ghx-label")) {
                LOGGER.warn("Epic Color is not in pre-define range " + epicColor);
                epicColor = "ghx-label-0";
            }
            String epicStatusStr = "";
            if (epicStatus != null && StringUtils.isEmpty((CharSequence)(epicStatusStr = (String)epicStatus.get("value")))) {
                LOGGER.warn("Status custom field has empty value");
            }
            Epic epic = new Epic(searchIssue.getKey(), epicName, epicColor, epicStatusStr);
            returnEpics.add(epic);
        });
        return returnEpics;
    }
}

