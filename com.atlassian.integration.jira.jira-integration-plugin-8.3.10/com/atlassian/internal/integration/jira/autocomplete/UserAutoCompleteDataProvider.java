/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProvider;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.RestAutoCompleteContext;
import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class UserAutoCompleteDataProvider
implements AutoCompleteDataProvider {
    private final String baseAutoCompleteUrl;

    public UserAutoCompleteDataProvider(String baseAutoCompleteUrl) {
        this.baseAutoCompleteUrl = baseAutoCompleteUrl;
    }

    @Override
    public String getUrl(RestAutoCompleteContext context) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)this.baseAutoCompleteUrl);
        if (!StringUtils.isEmpty((CharSequence)context.getIssueKey())) {
            uriBuilder.queryParam("issueKey", new Object[]{context.getIssueKey()});
        } else {
            uriBuilder.queryParam("project", new Object[]{context.getProjectKey()});
        }
        if (context.hasTerm()) {
            uriBuilder.queryParam("username", new Object[]{context.getTerm()});
        }
        return uriBuilder.build(new Object[0]).toString();
    }

    @Override
    public Collection<AutoCompleteItem> parseData(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        ArrayList<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject user = jsonArray.getJSONObject(i);
            AutoCompleteItem item = new AutoCompleteItem(user.getString("name"), user.getString("displayName"));
            items.add(item);
        }
        return items;
    }
}

