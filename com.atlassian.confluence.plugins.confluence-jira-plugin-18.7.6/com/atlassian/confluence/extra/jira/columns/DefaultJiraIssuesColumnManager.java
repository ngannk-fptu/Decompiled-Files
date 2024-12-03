/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.columns;

import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.jira.api.services.JiraConnectorManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesColumnManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesSettingsManager;
import com.atlassian.confluence.extra.jira.columns.JiraColumnInfo;
import com.atlassian.confluence.extra.jira.columns.JiraIssueSortableHelper;
import com.atlassian.confluence.extra.jira.util.JiraConnectorUtils;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.plugins.jira.beans.JiraServerBean;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraIssuesColumnManager
implements JiraIssuesColumnManager {
    public static final String COLUMN_EPIC_LINK = "epic link";
    public static final String COLUMN_EPIC_LINK_DISPLAY = "epic link display";
    public static final String COLUMN_EPIC_NAME = "epic name";
    public static final String COLUMN_EPIC_STATUS = "epic status";
    public static final String COLUMN_EPIC_COLOUR = "epic colour";
    private static final String REST_URL_FIELD_INFO = "/rest/api/2/field";
    private static final String PROP_KEY_PREFIX = "jiraissues.column.";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_ISSUE_TYPE = "issuetype";
    private static final String COLUMN_KEY = "key";
    private static final String COLUMN_ISSUE_KEY = "issuekey";
    private static final String COLUMN_DUE = "due";
    private static final String COLUMN_DUE_DATE = "duedate";
    private static final String COLUMN_SUMMARY = "summary";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_RESOLUTION = "resolution";
    private static final String COLUMN_ISSUE_LINKS = "issuelinks";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_ENVIRONMENT = "environment";
    public static final String EPIC_LABEL_SCHEMA = "com.pyxis.greenhopper.jira:gh-epic-label";
    public static final String EPIC_STATUS_SCHEMA = "com.pyxis.greenhopper.jira:gh-epic-status";
    public static final String EPIC_COLOUR_SCHEMA = "com.pyxis.greenhopper.jira:gh-epic-color";
    public static final String EPIC_LINK_SCHEMA = "com.pyxis.greenhopper.jira:gh-epic-link";
    public static final String TEAM_NAME_SCHEMA = "com.atlassian.teams:rm-teams-custom-field-team";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraIssuesColumnManager.class);
    private final JiraIssuesSettingsManager jiraIssuesSettingsManager;
    private final I18nResolver i18nResolver;
    private final JiraConnectorManager jiraConnectorManager;
    private ReadOnlyApplicationLink applicationLink;
    private Cache<String, Set<JiraColumnInfo>> cache;
    private final Map<String, String> I18N_COLUMN_KEYS_MAP = new ImmutableMap.Builder().put((Object)"duedate", (Object)"due").put((Object)"lastViewed", (Object)"lastviewed").build();

    public DefaultJiraIssuesColumnManager(JiraIssuesSettingsManager jiraIssuesSettingsManager, I18nResolver i18nResolver, JiraConnectorManager jiraConnectorManager, CacheManager cacheManager) {
        this.jiraIssuesSettingsManager = jiraIssuesSettingsManager;
        this.i18nResolver = i18nResolver;
        this.jiraConnectorManager = jiraConnectorManager;
        this.cache = cacheManager.getCache("Jira Issues Macro Fields", (CacheLoader)new CustomFieldsCacheLoader(), new CacheSettingsBuilder().expireAfterWrite(1L, TimeUnit.HOURS).replicateViaInvalidation().build());
    }

    @Override
    public Map<String, String> getColumnMap(String jiraIssuesUrl) {
        return this.jiraIssuesSettingsManager.getColumnMap(jiraIssuesUrl);
    }

    @Override
    public void setColumnMap(String jiraIssuesUrl, Map<String, String> columnMapping) {
        this.jiraIssuesSettingsManager.setColumnMap(jiraIssuesUrl, columnMapping);
    }

    @Override
    public boolean isColumnBuiltIn(String columnName) {
        return ALL_BUILTIN_COLUMN_NAMES.contains(columnName.toLowerCase());
    }

    @Override
    public String getCanonicalFormOfBuiltInField(String columnName) {
        switch (columnName.toLowerCase()) {
            case "issuekey": {
                return COLUMN_KEY;
            }
            case "issuetype": {
                return COLUMN_TYPE;
            }
            case "duedate": {
                return COLUMN_DUE;
            }
            case "fixversion": 
            case "fixversions": {
                return "fixVersion";
            }
            case "versions": {
                return "version";
            }
            case "components": {
                return "component";
            }
            case "resolutiondate": {
                return "resolved";
            }
            case "aggregatetimeestimate": {
                return "aggregatetimeremainingestimate";
            }
        }
        if (this.isColumnBuiltIn(columnName)) {
            return columnName.toLowerCase();
        }
        return columnName;
    }

    @Override
    public boolean isBuiltInColumnMultivalue(String columnName) {
        return ALL_MULTIVALUE_BUILTIN_COLUMN_NAMES.contains(columnName.toLowerCase());
    }

    @Override
    public Set<JiraColumnInfo> getColumnsInfoFromJira(ReadOnlyApplicationLink appLink) {
        LinkedHashSet<JiraColumnInfo> jiraFields;
        if (appLink == null) {
            return new LinkedHashSet<JiraColumnInfo>();
        }
        this.applicationLink = appLink;
        try {
            jiraFields = (LinkedHashSet<JiraColumnInfo>)this.cache.get((Object)appLink.getId().get());
        }
        catch (RuntimeException e) {
            LOGGER.error("Failed to fetch Jira fields: ", (Throwable)e);
            jiraFields = new LinkedHashSet<JiraColumnInfo>();
        }
        return jiraFields;
    }

    public static boolean matchColumnFromSchema(String columnSchemaToMatch, JiraColumnInfo column) {
        return column.getSchema() != null && column.getSchemaString().equalsIgnoreCase(columnSchemaToMatch);
    }

    @Override
    public Set<JiraColumnInfo> getColumnInfo(Map<String, String> params, Set<JiraColumnInfo> columns, ReadOnlyApplicationLink applink) {
        Set<String> columnNames = JiraUtil.getColumnNamesFromParams(params, false);
        JiraServerBean jiraServer = this.jiraConnectorManager.getJiraServer(applink);
        boolean isJiraSupported = JiraIssueSortableHelper.isJiraSupportedOrder(jiraServer);
        LinkedHashSet<JiraColumnInfo> info = new LinkedHashSet<JiraColumnInfo>();
        for (String columnName : columnNames) {
            String key = this.getCanonicalFormOfBuiltInField(columnName);
            JiraColumnInfo jiraColumnInfo = this.getJiraColumnInfo(this.getColumnMapping(columnName, XML_COLUMN_KEYS_MAPPING), columns);
            List<String> clauseNames = Collections.singletonList(key);
            JiraColumnInfo.JsonSchema schema = null;
            boolean isSortable = false;
            if (jiraColumnInfo != null) {
                isSortable = isJiraSupported ? (clauseNames = jiraColumnInfo.getClauseNames()) != null && !clauseNames.isEmpty() && jiraColumnInfo.isNavigable() : jiraColumnInfo.isCustom() && jiraColumnInfo.isNavigable() || JiraIssuesColumnManager.SUPPORT_SORTABLE_COLUMN_NAMES.contains(key);
                schema = jiraColumnInfo.getSchema();
            }
            String title = jiraColumnInfo != null ? jiraColumnInfo.getTitle() : this.getTitleFromParams(columnName, params);
            info.add(new JiraColumnInfo(key, this.getDisplayName(key, columnName, title), clauseNames, isSortable, schema));
        }
        return info;
    }

    private String getTitleFromParams(String columnId, Map<String, String> params) {
        String columnsString;
        ArrayList<String> columnNames;
        List columnList;
        String matchingColumnName;
        String columnIdsString = JiraUtil.getParamValue(params, "columnIds", 1);
        ArrayList<String> columnIds = new ArrayList<String>(Arrays.asList(columnIdsString.split(",")));
        int index = columnIds.indexOf(columnId);
        String title = index != -1 ? ((matchingColumnName = (String)(columnList = (List)(columnNames = new ArrayList<String>(Arrays.asList(StringUtils.split((String)(columnsString = JiraUtil.getParamValue(params, "columns", 1)), (String)",;")))).stream().map(name -> {
            try {
                return URLDecoder.decode(name, StandardCharsets.UTF_8);
            }
            catch (IllegalArgumentException e) {
                return name;
            }
        }).collect(Collectors.toCollection(ArrayList::new))).get(index)) != null ? matchingColumnName : "") : "";
        return title;
    }

    private String getDisplayName(String key, String columnName, String title) {
        if (key.contains("'") || columnName.contains("'")) {
            return columnName;
        }
        String i18nKey = PROP_KEY_PREFIX + this.I18N_COLUMN_KEYS_MAP.getOrDefault(key, key);
        String displayName = this.i18nResolver.getText(i18nKey);
        if ((StringUtils.isBlank((CharSequence)displayName) || displayName.equals(i18nKey)) && (StringUtils.isBlank((CharSequence)(displayName = this.i18nResolver.getText(i18nKey = PROP_KEY_PREFIX + columnName))) || displayName.equals(i18nKey)) && (StringUtils.isBlank((CharSequence)(displayName = this.i18nResolver.getText(i18nKey = PROP_KEY_PREFIX + title))) || displayName.equals(i18nKey))) {
            displayName = columnName.startsWith("customfield_") && !title.startsWith("customfield_") ? title : columnName;
        }
        return displayName;
    }

    private JiraColumnInfo getJiraColumnInfo(String columnName, Set<JiraColumnInfo> columns) {
        JiraColumnInfo byKey = null;
        JiraColumnInfo byName = null;
        if (columns == null) {
            return null;
        }
        for (JiraColumnInfo jiraColumn : columns) {
            if (jiraColumn.getKey().equalsIgnoreCase(columnName)) {
                byKey = jiraColumn;
                break;
            }
            if (!jiraColumn.getTitle().equalsIgnoreCase(columnName)) continue;
            byName = jiraColumn;
        }
        return byKey != null ? byKey : byName;
    }

    @Override
    public String getColumnMapping(String columnKey, Map<String, String> map) {
        String key = map.get(columnKey);
        return StringUtils.isNotBlank((CharSequence)key) ? key : columnKey;
    }

    @Override
    public ImmutableMap<String, ImmutableSet<String>> getI18nColumnNames() {
        ImmutableMap.Builder i18nColumnNamesBuilder = ImmutableMap.builder();
        ImmutableSet.Builder columnEpicLink = ImmutableSet.builder();
        columnEpicLink.add((Object)this.i18nResolver.getText("jiraissue.column.epics.link.upper"));
        columnEpicLink.add((Object)this.i18nResolver.getText("jiraissue.column.epics.link.lower"));
        columnEpicLink.add((Object)"Epic Link");
        columnEpicLink.add((Object)COLUMN_EPIC_LINK);
        i18nColumnNamesBuilder.put((Object)COLUMN_EPIC_LINK, (Object)columnEpicLink.build());
        ImmutableSet.Builder columnEpicLinkDisplay = ImmutableSet.builder();
        columnEpicLinkDisplay.add((Object)this.i18nResolver.getText("jiraissue.column.epics.link.lower"));
        i18nColumnNamesBuilder.put((Object)COLUMN_EPIC_LINK_DISPLAY, (Object)columnEpicLinkDisplay.build());
        ImmutableSet.Builder columnEpicName = ImmutableSet.builder();
        columnEpicName.add((Object)this.i18nResolver.getText("jiraissue.column.epics.name.upper"));
        columnEpicName.add((Object)this.i18nResolver.getText("jiraissue.column.epics.name.lower"));
        columnEpicName.add((Object)"Epic Name");
        columnEpicName.add((Object)COLUMN_EPIC_NAME);
        i18nColumnNamesBuilder.put((Object)COLUMN_EPIC_NAME, (Object)columnEpicName.build());
        ImmutableSet.Builder columnEpicColour = ImmutableSet.builder();
        columnEpicColour.add((Object)this.i18nResolver.getText("jiraissue.column.epics.colour.upper"));
        columnEpicColour.add((Object)this.i18nResolver.getText("jiraissue.column.epics.colour.lower"));
        columnEpicColour.add((Object)"Epic Colour");
        columnEpicColour.add((Object)"Epic Color");
        columnEpicColour.add((Object)COLUMN_EPIC_COLOUR);
        columnEpicColour.add((Object)"epic color");
        i18nColumnNamesBuilder.put((Object)COLUMN_EPIC_COLOUR, (Object)columnEpicColour.build());
        ImmutableSet.Builder columnEpicStatus = ImmutableSet.builder();
        columnEpicStatus.add((Object)this.i18nResolver.getText("jiraissue.column.epics.status.upper"));
        columnEpicStatus.add((Object)this.i18nResolver.getText("jiraissue.column.epics.status.lower"));
        columnEpicStatus.add((Object)"Epic Status");
        columnEpicStatus.add((Object)COLUMN_EPIC_STATUS);
        i18nColumnNamesBuilder.put((Object)COLUMN_EPIC_STATUS, (Object)columnEpicStatus.build());
        ImmutableSet.Builder columnType = ImmutableSet.builder();
        columnType.add((Object)this.i18nResolver.getText("jiraissue.column.type"));
        i18nColumnNamesBuilder.put((Object)COLUMN_TYPE, (Object)columnType.build());
        ImmutableSet.Builder columnKey = ImmutableSet.builder();
        columnKey.add((Object)this.i18nResolver.getText("jiraissue.column.key"));
        i18nColumnNamesBuilder.put((Object)COLUMN_KEY, (Object)columnKey.build());
        ImmutableSet.Builder columnSummary = ImmutableSet.builder();
        columnSummary.add((Object)this.i18nResolver.getText("jiraissue.column.summary"));
        i18nColumnNamesBuilder.put((Object)COLUMN_SUMMARY, (Object)columnSummary.build());
        ImmutableSet.Builder columnPriority = ImmutableSet.builder();
        columnPriority.add((Object)this.i18nResolver.getText("jiraissue.column.priority"));
        i18nColumnNamesBuilder.put((Object)COLUMN_PRIORITY, (Object)columnPriority.build());
        ImmutableSet.Builder columnStatus = ImmutableSet.builder();
        columnStatus.add((Object)this.i18nResolver.getText("jiraissue.column.status"));
        i18nColumnNamesBuilder.put((Object)COLUMN_STATUS, (Object)columnStatus.build());
        ImmutableSet.Builder columnResolution = ImmutableSet.builder();
        columnResolution.add((Object)this.i18nResolver.getText("jiraissue.column.resolution"));
        i18nColumnNamesBuilder.put((Object)COLUMN_RESOLUTION, (Object)columnResolution.build());
        ImmutableSet.Builder columnIssuelinks = ImmutableSet.builder();
        columnIssuelinks.add((Object)this.i18nResolver.getText("jiraissue.column.issuelinks"));
        i18nColumnNamesBuilder.put((Object)COLUMN_ISSUE_LINKS, (Object)columnIssuelinks.build());
        ImmutableSet.Builder columnDescription = ImmutableSet.builder();
        columnDescription.add((Object)this.i18nResolver.getText("jiraissue.column.description"));
        i18nColumnNamesBuilder.put((Object)COLUMN_DESCRIPTION, (Object)columnDescription.build());
        ImmutableSet.Builder columnEnvironment = ImmutableSet.builder();
        columnEnvironment.add((Object)this.i18nResolver.getText("jiraissue.column.environment"));
        i18nColumnNamesBuilder.put((Object)COLUMN_ENVIRONMENT, (Object)columnEnvironment.build());
        return i18nColumnNamesBuilder.build();
    }

    @Override
    public boolean columnsContainsEpicColumns(Set<JiraColumnInfo> columns) {
        Set EPIC_COLUMNS = Stream.of(EPIC_COLOUR_SCHEMA, EPIC_LABEL_SCHEMA, EPIC_LINK_SCHEMA, EPIC_STATUS_SCHEMA).collect(Collectors.toCollection(LinkedHashSet::new));
        for (JiraColumnInfo column : columns) {
            for (String epicSchema : EPIC_COLUMNS) {
                if (!DefaultJiraIssuesColumnManager.matchColumnFromSchema(epicSchema, column)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean columnsContainsTeamColumns(Set<JiraColumnInfo> columns) {
        for (JiraColumnInfo column : columns) {
            if (!DefaultJiraIssuesColumnManager.matchColumnFromSchema(TEAM_NAME_SCHEMA, column)) continue;
            return true;
        }
        return false;
    }

    private class CustomFieldsCacheLoader
    implements CacheLoader<String, Set<JiraColumnInfo>> {
        private CustomFieldsCacheLoader() {
        }

        @Nonnull
        public Set<JiraColumnInfo> load(@Nonnull String id) {
            try {
                ApplicationLinkRequest request = JiraConnectorUtils.getApplicationLinkRequest(DefaultJiraIssuesColumnManager.this.applicationLink, Request.MethodType.GET, DefaultJiraIssuesColumnManager.REST_URL_FIELD_INFO);
                request.addHeader("Content-Type", "application/json");
                return (Set)new Gson().fromJson(request.execute(), new TypeToken<Set<JiraColumnInfo>>(){}.getType());
            }
            catch (CredentialsRequiredException | ResponseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

