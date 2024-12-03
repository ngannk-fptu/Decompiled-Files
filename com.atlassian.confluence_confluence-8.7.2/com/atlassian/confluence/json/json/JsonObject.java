/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  net.jcip.annotations.NotThreadSafe
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.json.json;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonBoolean;
import com.atlassian.confluence.json.json.JsonEscapeUtils;
import com.atlassian.confluence.json.json.JsonNumber;
import com.atlassian.confluence.json.json.JsonString;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@NotThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class JsonObject
implements Json {
    private final Map<String, Json> map = new HashMap<String, Json>();

    @Override
    public String serialize() {
        ArrayList properties = new ArrayList();
        this.map.forEach((key, value) -> properties.add(JsonEscapeUtils.quote(key) + ":" + value.serialize()));
        return "{" + StringUtils.join(properties, (char)',') + "}";
    }

    public JsonObject setProperty(String key, Json json) {
        this.map.put(key, json);
        return this;
    }

    public JsonObject setProperty(String key, @Nullable String value) {
        return this.setProperty(key, new JsonString(value));
    }

    public JsonObject setProperty(String key, @Nullable Boolean value) {
        return this.setProperty(key, new JsonBoolean(value));
    }

    public JsonObject setProperty(String key, @Nullable Number number) {
        return this.setProperty(key, new JsonNumber(number));
    }

    public JsonObject setProperty(String key, @Nullable Date date) {
        String dateStr = date != null ? new SimpleDateFormat("dd MMM yyyy").format(date) : "null";
        return this.setProperty(key, new JsonString(dateStr));
    }
}

