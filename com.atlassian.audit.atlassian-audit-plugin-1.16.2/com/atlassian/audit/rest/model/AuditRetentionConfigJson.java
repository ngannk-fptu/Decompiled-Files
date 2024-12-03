/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfig
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.api.AuditRetentionConfig;
import com.atlassian.audit.rest.model.converter.PeriodDeserializer;
import com.atlassian.audit.rest.model.converter.PeriodSerializer;
import java.time.Period;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class AuditRetentionConfigJson {
    private final Period period;

    public AuditRetentionConfigJson(AuditRetentionConfig config) {
        this(config.getPeriod());
    }

    @JsonCreator
    public AuditRetentionConfigJson(@JsonProperty(value="period") @JsonDeserialize(using=PeriodDeserializer.class) Period period) {
        this.period = period;
    }

    @JsonProperty(value="period")
    @JsonSerialize(using=PeriodSerializer.class)
    public Period getPeriod() {
        return this.period;
    }

    @JsonIgnore
    public AuditRetentionConfig toRetentionConfig() {
        return new AuditRetentionConfig(this.period);
    }
}

