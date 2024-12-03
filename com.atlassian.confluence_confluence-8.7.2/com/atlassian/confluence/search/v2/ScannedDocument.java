/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import java.util.Map;
import java.util.Optional;

public class ScannedDocument {
    private final float score;
    private final Map<String, String[]> fieldMap;

    public ScannedDocument(float score, Map<String, String[]> fieldMap) {
        this.score = score;
        this.fieldMap = fieldMap;
    }

    public float getScore() {
        return this.score;
    }

    public Map<String, String[]> getFieldMap() {
        return this.fieldMap;
    }

    public String getFieldValue(String name) {
        return Optional.ofNullable(this.fieldMap.get(name)).map(values -> values[0]).orElse(null);
    }
}

