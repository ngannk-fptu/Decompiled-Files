/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.capabilities.api.LinkedAppWithCapabilities
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.plugins.navlink.producer.capabilities;

import com.atlassian.plugins.capabilities.api.LinkedAppWithCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.ApplicationWithCapabilities;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
public class RemoteApplicationWithCapabilities
extends ApplicationWithCapabilities
implements LinkedAppWithCapabilities {
    private final String applicationLinkId;
    private final String selfUrl;

    public RemoteApplicationWithCapabilities(String applicationLinkId, String selfUrl, String type, ZonedDateTime buildDate, Map<String, String> capabilities) {
        super(type, buildDate, capabilities);
        this.applicationLinkId = applicationLinkId;
        this.selfUrl = selfUrl;
    }

    public String getApplicationLinkId() {
        return this.applicationLinkId;
    }

    public String getSelfUrl() {
        return this.selfUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.applicationLinkId, this.selfUrl, this.id, this.name, this.type, this.buildDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RemoteApplicationWithCapabilities)) {
            return false;
        }
        RemoteApplicationWithCapabilities that = (RemoteApplicationWithCapabilities)obj;
        return Objects.equals(this.applicationLinkId, that.applicationLinkId) && Objects.equals(this.selfUrl, that.selfUrl) && Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && Objects.equals(this.buildDate, that.buildDate) && Objects.equals(this.type, that.type);
    }

    @Override
    public String toString() {
        return "RemoteApplicationWithCapabilities{applicationLinkId='" + this.applicationLinkId + '\'' + ", selfUrl='" + this.selfUrl + '\'' + ", id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", type='" + this.type + '\'' + ", buildDate='" + this.buildDate + '\'' + ", capabilities=" + this.capabilities + '}';
    }
}

