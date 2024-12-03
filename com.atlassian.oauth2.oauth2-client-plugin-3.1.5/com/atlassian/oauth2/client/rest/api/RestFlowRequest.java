/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequest
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.oauth2.client.rest.api;

import com.atlassian.oauth2.client.api.lib.flow.FlowRequest;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;

public class RestFlowRequest
implements FlowRequest {
    @XmlElement
    private String id;
    @XmlElement
    private String initFlowUrl;

    public RestFlowRequest() {
    }

    public RestFlowRequest(String id, String initFlowUrl) {
        this.id = id;
        this.initFlowUrl = initFlowUrl;
    }

    public static RestFlowRequest valueOf(FlowRequest flowRequest) {
        return new RestFlowRequest(flowRequest.getId(), flowRequest.getInitFlowUrl());
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInitFlowUrl(String initFlowUrl) {
        this.initFlowUrl = initFlowUrl;
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getInitFlowUrl() {
        return this.initFlowUrl;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RestFlowRequest that = (RestFlowRequest)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.initFlowUrl, that.initFlowUrl);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.initFlowUrl);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("initFlowUrl", (Object)this.getInitFlowUrl()).toString();
    }
}

