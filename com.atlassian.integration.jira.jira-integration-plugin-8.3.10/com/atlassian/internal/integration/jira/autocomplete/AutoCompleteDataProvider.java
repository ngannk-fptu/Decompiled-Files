/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.internal.integration.jira.autocomplete;

import com.atlassian.internal.integration.jira.autocomplete.AutoCompleteItem;
import com.atlassian.internal.integration.jira.autocomplete.RestAutoCompleteContext;
import java.util.Collection;
import org.codehaus.jettison.json.JSONException;

public interface AutoCompleteDataProvider {
    public String getUrl(RestAutoCompleteContext var1);

    public Collection<AutoCompleteItem> parseData(String var1) throws JSONException;
}

