/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteDataProvider;
import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.RestAutoCompleteContext;
import com.atlassian.internal.integration.jira.autocomplete.SprintAutoCompleteItem;
import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class SprintAutoCompleteDataProvider
implements AutoCompleteDataProvider {
    private final String baseAutoCompleteUrl;

    public SprintAutoCompleteDataProvider(String baseAutoCompleteUrl) {
        this.baseAutoCompleteUrl = baseAutoCompleteUrl;
    }

    @Override
    public String getUrl(RestAutoCompleteContext context) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)this.baseAutoCompleteUrl);
        if (context.hasTerm()) {
            uriBuilder.queryParam("query", new Object[]{context.getTerm()});
        }
        return uriBuilder.build(new Object[0]).toString();
    }

    @Override
    public Collection<AutoCompleteItem> parseData(String json) throws JSONException {
        JSONArray allMatches;
        JSONObject jsonObj = new JSONObject(json);
        ArrayList<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
        JSONArray suggestions = jsonObj.getJSONArray("suggestions");
        if (suggestions.length() > 0) {
            items.addAll(this.arrayToItems(suggestions, true));
        }
        if ((allMatches = jsonObj.getJSONArray("allMatches")).length() > 0) {
            items.addAll(this.arrayToItems(allMatches, false));
        }
        return items;
    }

    private Collection<AutoCompleteItem> arrayToItems(JSONArray jsonArray, boolean isSuggestion) throws JSONException {
        ArrayList<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonItem = jsonArray.getJSONObject(i);
            SprintAutoCompleteItem item = new SprintAutoCompleteItem(jsonItem.getString("id"), jsonItem.getString("name"), isSuggestion, jsonItem.getString("stateKey"), jsonItem.getString("boardName"));
            items.add(item);
        }
        return items;
    }
}

