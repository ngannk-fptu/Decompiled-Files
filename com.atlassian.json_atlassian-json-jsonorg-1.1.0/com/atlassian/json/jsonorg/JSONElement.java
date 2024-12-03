/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.json.jsonorg;

import com.atlassian.annotations.PublicApi;
import com.atlassian.json.jsonorg.JSONArray;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import java.io.Writer;

@PublicApi
public abstract class JSONElement {
    public abstract boolean isJSONObject();

    public abstract boolean isJSONArray();

    public abstract JSONObject getAsJSONObject();

    public abstract JSONArray getAsJSONArray();

    public abstract Writer write(Writer var1) throws JSONException;
}

