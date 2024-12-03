/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.EffectiveCoverageLevel
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.EffectiveCoverageLevel;
import com.atlassian.audit.rest.model.CoverageAreaJson;
import com.atlassian.audit.rest.model.EffectiveCoverageLevelJson;
import java.util.Map;
import java.util.stream.Collectors;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditCoverageConfigJson {
    private final Map<CoverageAreaJson, EffectiveCoverageLevelJson> levelByArea;

    public AuditCoverageConfigJson(AuditCoverageConfig config) {
        this(config.getLevelByArea().entrySet().stream().collect(Collectors.toMap(e -> CoverageAreaJson.fromCoverageArea((CoverageArea)e.getKey()), e -> EffectiveCoverageLevelJson.fromCoverageLevel((EffectiveCoverageLevel)e.getValue()))));
    }

    @JsonCreator
    public AuditCoverageConfigJson(@JsonProperty(value="levelByArea") Map<CoverageAreaJson, EffectiveCoverageLevelJson> levelByArea) {
        this.levelByArea = levelByArea;
    }

    @JsonProperty(value="levelByArea")
    public Map<CoverageAreaJson, EffectiveCoverageLevelJson> getLevelByArea() {
        return this.levelByArea;
    }

    @JsonIgnore
    public AuditCoverageConfig toCoverageConfig() {
        return new AuditCoverageConfig(this.levelByArea.entrySet().stream().collect(Collectors.toMap(e -> ((CoverageAreaJson)((Object)((Object)e.getKey()))).toCoverageArea(), e -> ((EffectiveCoverageLevelJson)((Object)((Object)e.getValue()))).toEffectiveCoverageLevel())));
    }
}

