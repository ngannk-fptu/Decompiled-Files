/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.rest.model.CoverageAreaJson;
import com.atlassian.audit.rest.model.CoverageLevelJson;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditCoverageConfigurationJson {
    private final Map<CoverageAreaJson, CoverageLevelJson> levelByArea;

    @JsonCreator
    public AuditCoverageConfigurationJson(@JsonProperty(value="levelByArea") Map<CoverageAreaJson, CoverageLevelJson> levelByArea) {
        this.levelByArea = levelByArea;
    }

    @JsonProperty(value="levelByArea")
    public Map<CoverageAreaJson, CoverageLevelJson> getLevelByArea() {
        return this.levelByArea;
    }
}

