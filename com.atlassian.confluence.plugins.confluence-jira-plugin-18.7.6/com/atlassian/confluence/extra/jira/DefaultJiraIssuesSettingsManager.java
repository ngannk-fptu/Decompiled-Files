/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.extra.jira.api.services.JiraIssuesSettingsManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;

public class DefaultJiraIssuesSettingsManager
implements JiraIssuesSettingsManager {
    private static final String BANDANA_KEY_COLUMN_MAPPING = "com.atlassian.confluence.extra.jira:customFieldsFor:";
    private static final String BANDANA_KEY_ICON_MAPPING = "atlassian.confluence.jira.icon.mappings";
    private final BandanaManager bandanaManager;

    public DefaultJiraIssuesSettingsManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    private static String getColumnMapBandanaKey(String jiraIssuesUrl) {
        return BANDANA_KEY_COLUMN_MAPPING + DigestUtils.md5Hex((String)jiraIssuesUrl);
    }

    @Override
    public Map<String, String> getColumnMap(String jiraIssuesUrl) {
        return (Map)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, DefaultJiraIssuesSettingsManager.getColumnMapBandanaKey(jiraIssuesUrl));
    }

    @Override
    public void setColumnMap(String jiraIssuesUrl, Map<String, String> columnMapping) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, DefaultJiraIssuesSettingsManager.getColumnMapBandanaKey(jiraIssuesUrl), new HashMap<String, String>(columnMapping));
    }

    @Override
    public Map<String, String> getIconMapping() {
        return (Map)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BANDANA_KEY_ICON_MAPPING);
    }

    @Override
    public void setIconMapping(Map<String, String> iconMapping) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, BANDANA_KEY_ICON_MAPPING, new HashMap<String, String>(iconMapping));
    }
}

