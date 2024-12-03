/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.LocalDate
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.model.ApplicationVersionStatus;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.RequiredLink;
import java.net.URI;
import org.joda.time.LocalDate;

public final class ApplicationVersion
implements Entity {
    Links _links;
    @RequiredLink(rel="self")
    URI selfUri;
    Integer buildNumber;
    String version;
    LocalDate releaseDate;
    ApplicationVersionStatus status;
    boolean dataCenterCompatible;

    @Override
    public Links getLinks() {
        return this._links;
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public String getName() {
        return this.version;
    }

    public LocalDate getReleaseDate() {
        return this.releaseDate;
    }

    public ApplicationVersionStatus getStatus() {
        return this.status;
    }

    public boolean isDataCenterCompatible() {
        return this.dataCenterCompatible;
    }
}

