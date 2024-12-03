/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.CoverageArea
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.entity.CoverageArea;
import java.util.stream.Stream;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonValue;

public enum CoverageAreaJson {
    AUDIT_LOG("audit-log", CoverageArea.AUDIT_LOG),
    GLOBAL_CONFIG_AND_ADMINISTRATION("global-config-and-administration", CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION),
    USER_MANAGEMENT("user-management", CoverageArea.USER_MANAGEMENT),
    PERMISSIONS("permissions", CoverageArea.PERMISSIONS),
    LOCAL_CONFIG_AND_ADMINISTRATION("local-config-and-administration", CoverageArea.LOCAL_CONFIG_AND_ADMINISTRATION),
    SECURITY("security", CoverageArea.SECURITY),
    END_USER_ACTIVITY("end-user-activity", CoverageArea.END_USER_ACTIVITY),
    ECOSYSTEM("ecosystem", CoverageArea.ECOSYSTEM);

    private final String key;
    private final CoverageArea correspondingArea;

    private CoverageAreaJson(String key, CoverageArea correspondingArea) {
        this.key = key;
        this.correspondingArea = correspondingArea;
    }

    @JsonValue
    public String toString() {
        return this.key;
    }

    @JsonIgnore
    public CoverageArea toCoverageArea() {
        return this.correspondingArea;
    }

    @JsonCreator
    public static CoverageAreaJson fromKey(String key) {
        return Stream.of(CoverageAreaJson.values()).filter(c -> c.key.equals(key)).findFirst().orElseThrow(() -> new IllegalArgumentException("No such value found: " + key));
    }

    public static CoverageAreaJson fromCoverageArea(CoverageArea coverageArea) {
        return Stream.of(CoverageAreaJson.values()).filter(c -> c.correspondingArea.equals((Object)coverageArea)).findFirst().orElseThrow(() -> new IllegalArgumentException("No such value found: " + coverageArea));
    }
}

