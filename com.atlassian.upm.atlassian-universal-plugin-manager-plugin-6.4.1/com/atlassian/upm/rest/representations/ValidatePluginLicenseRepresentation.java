/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ValidatePluginLicenseRepresentation {
    @JsonProperty
    private String licenseKey;

    @JsonCreator
    public ValidatePluginLicenseRepresentation(@JsonProperty(value="licenseKey") String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }
}

