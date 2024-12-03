/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.confluence.api.model;

import com.atlassian.annotations.ExperimentalApi;
import java.time.temporal.ChronoUnit;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChronoUnitCaseInsensitive {
    private final ChronoUnit chronoUnit;

    @JsonCreator
    public ChronoUnitCaseInsensitive(String unitString) {
        this.chronoUnit = ChronoUnit.valueOf(unitString.toUpperCase());
    }

    public ChronoUnit getChronoUnit() {
        return this.chronoUnit;
    }
}

