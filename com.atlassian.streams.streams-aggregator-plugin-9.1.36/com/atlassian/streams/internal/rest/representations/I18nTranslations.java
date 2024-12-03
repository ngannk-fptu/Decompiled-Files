/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.streams.internal.rest.representations;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class I18nTranslations {
    @JsonProperty
    private final Map<String, String> translations;

    @JsonCreator
    public I18nTranslations(@JsonProperty(value="translations") Map<String, String> translations) {
        this.translations = ImmutableMap.copyOf(translations);
    }

    public Map<String, String> getTranslations() {
        return this.translations;
    }
}

