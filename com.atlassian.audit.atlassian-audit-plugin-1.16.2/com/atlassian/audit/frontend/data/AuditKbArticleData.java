/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.audit.frontend.data;

import java.net.URI;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditKbArticleData {
    private URI featureDescription;
    private URI reference;
    private URI integrations;
    private URI databaseRetention;

    public AuditKbArticleData(URI featureDescription, URI reference, URI integrations, URI databaseRetention) {
        this.featureDescription = featureDescription;
        this.reference = reference;
        this.integrations = integrations;
        this.databaseRetention = databaseRetention;
    }

    public URI getFeatureDescription() {
        return this.featureDescription;
    }

    public URI getReference() {
        return this.reference;
    }

    public URI getIntegrations() {
        return this.integrations;
    }

    public URI getDatabaseRetention() {
        return this.databaseRetention;
    }

    public String toString() {
        return "AuditKbArticleData{featureDescription=" + this.featureDescription + ", reference=" + this.reference + ", integrations=" + this.integrations + ", databaseRetention=" + this.databaseRetention + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditKbArticleData that = (AuditKbArticleData)o;
        return Objects.equals(this.featureDescription, that.featureDescription) && Objects.equals(this.reference, that.reference) && Objects.equals(this.integrations, that.integrations) && Objects.equals(this.databaseRetention, that.databaseRetention);
    }

    public int hashCode() {
        return Objects.hash(this.featureDescription, this.reference, this.integrations, this.databaseRetention);
    }
}

