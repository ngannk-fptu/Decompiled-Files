/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonCreator
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.migration.agent.service.catalogue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@ParametersAreNonnullByDefault
@JsonIgnoreProperties(ignoreUnknown=true)
public class Links {
    @JsonProperty(value="webui")
    public String webui;

    @JsonCreator
    public Links(@JsonProperty(value="webui") String webui) {
        this.webui = webui;
    }

    @Generated
    public Links() {
    }
}

