/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.logging.log4j.layout.json;

import com.atlassian.logging.log4j.layout.json.JsonContextData;
import com.atlassian.logging.log4j.layout.json.JsonLayoutHelper;
import com.atlassian.logging.log4j.layout.json.JsonStaticData;
import java.util.Map;

public interface JsonDataProvider {
    public JsonStaticData getStaticData();

    public JsonContextData getContextData(JsonLayoutHelper.LogEvent var1);

    public Map<String, Object> getExtraData(JsonLayoutHelper.LogEvent var1);

    public String getHostName();
}

