/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.upm.rest.representations.HostLicenseDetailsRepresentation;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class HostStatusRepresentation {
    @JsonProperty
    private final boolean baseUrlInvalid;
    @JsonProperty
    private final boolean safeMode;
    @JsonProperty
    private final boolean pacDisabled;
    @JsonProperty
    private final boolean pacUnavailable;
    @JsonProperty
    private final HostLicenseDetailsRepresentation hostLicense;

    @JsonCreator
    public HostStatusRepresentation(@JsonProperty(value="baseUrlInvalid") boolean baseUrlInvalid, @JsonProperty(value="safeMode") boolean safeMode, @JsonProperty(value="pacDisabled") boolean pacDisabled, @JsonProperty(value="pacUnavailable") boolean pacUnavailable, @JsonProperty(value="hostLicense") HostLicenseDetailsRepresentation hostLicense) {
        this.baseUrlInvalid = baseUrlInvalid;
        this.safeMode = safeMode;
        this.pacDisabled = pacDisabled;
        this.pacUnavailable = pacUnavailable;
        this.hostLicense = hostLicense;
    }

    public boolean isBaseUrlInvalid() {
        return this.baseUrlInvalid;
    }

    public boolean isSafeMode() {
        return this.safeMode;
    }

    public boolean isPacDisabled() {
        return this.pacDisabled;
    }

    public boolean isPacUnavailable() {
        return this.pacUnavailable;
    }

    public HostLicenseDetailsRepresentation getHostLicense() {
        return this.hostLicense;
    }
}

