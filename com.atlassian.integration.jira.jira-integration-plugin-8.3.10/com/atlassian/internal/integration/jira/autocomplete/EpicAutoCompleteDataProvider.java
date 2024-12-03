/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProvider;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.EpicAutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.RestAutoCompleteContext;
import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class EpicAutoCompleteDataProvider
implements AutoCompleteDataProvider {
    private final String baseAutoCompleteUrl;

    public EpicAutoCompleteDataProvider(String baseAutoCompleteUrl) {
        this.baseAutoCompleteUrl = baseAutoCompleteUrl;
    }

    @Override
    public String getUrl(RestAutoCompleteContext context) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)this.baseAutoCompleteUrl);
        if (context.hasTerm()) {
            uriBuilder.queryParam("searchQuery", new Object[]{context.getTerm()});
        }
        uriBuilder.queryParam("projectKey", new Object[]{context.getProjectKey()});
        uriBuilder.queryParam("maxResults", new Object[]{1000});
        uriBuilder.queryParam("hideDone", new Object[]{true});
        return uriBuilder.build(new Object[0]).toString();
    }

    @Override
    public Collection<AutoCompleteItem> parseData(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        ArrayList<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
        JSONArray lists = jsonObj.getJSONArray("epicLists");
        for (int i = 0; i < lists.length(); ++i) {
            JSONObject list = lists.getJSONObject(i);
            JSONArray epics = list.getJSONArray("epicNames");
            if (epics.length() <= 0) continue;
            items.addAll(this.arrayToItems(epics, list.getString("listDescriptor")));
        }
        return items;
    }

    private Collection<AutoCompleteItem> arrayToItems(JSONArray jsonArray, String listName) throws JSONException {
        ArrayList<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonItem = jsonArray.getJSONObject(i);
            EpicAutoCompleteItem item = new EpicAutoCompleteItem(jsonItem.getString("key"), jsonItem.getString("name"), listName);
            items.add(item);
        }
        return items;
    }
}

