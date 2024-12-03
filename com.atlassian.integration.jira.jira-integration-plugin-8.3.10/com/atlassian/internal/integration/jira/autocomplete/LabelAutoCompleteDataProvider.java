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
import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class LabelAutoCompleteDataProvider
implements AutoCompleteDataProvider {
    private final String baseAutoCompleteUrl;

    public LabelAutoCompleteDataProvider(String baseAutoCompleteUrl) {
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
        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("suggestions");
        ArrayList<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            JSONObject jsonItem = jsonArray.getJSONObject(i);
            AutoCompleteItem item = new AutoCompleteItem(jsonItem.getString("label"), jsonItem.getString("label"));
            items.add(item);
        }
        return items;
    }
}

