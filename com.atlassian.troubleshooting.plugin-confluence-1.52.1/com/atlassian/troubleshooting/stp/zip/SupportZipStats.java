/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.stp.util.DurationUtil;
import com.atlassian.troubleshooting.stp.util.ObjectMapperFactory;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SupportZipStats
implements Serializable {
    @JsonProperty
    private final String durationFormatted;
    @JsonProperty
    private final long durationMillis;

    @JsonCreator
    public SupportZipStats(String durationFormatted, long durationMillis) {
        this.durationFormatted = durationFormatted;
        this.durationMillis = durationMillis;
    }

    public static SupportZipStats supportZipStats(Duration generateDuration) {
        return new SupportZipStats(DurationUtil.humanReadableDuration(generateDuration), generateDuration.toMillis());
    }

    public String toJson() throws IOException {
        return ObjectMapperFactory.getObjectMapper().writeValueAsString((Object)this);
    }
}

