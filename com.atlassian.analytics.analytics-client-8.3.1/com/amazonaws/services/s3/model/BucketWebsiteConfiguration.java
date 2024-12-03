/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.RedirectRule;
import com.amazonaws.services.s3.model.RoutingRule;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class BucketWebsiteConfiguration
implements Serializable {
    private String indexDocumentSuffix;
    private String errorDocument;
    private RedirectRule redirectAllRequestsTo;
    private List<RoutingRule> routingRules = new LinkedList<RoutingRule>();

    public BucketWebsiteConfiguration() {
    }

    public BucketWebsiteConfiguration(String indexDocumentSuffix) {
        this.indexDocumentSuffix = indexDocumentSuffix;
    }

    public BucketWebsiteConfiguration(String indexDocumentSuffix, String errorDocument) {
        this.indexDocumentSuffix = indexDocumentSuffix;
        this.errorDocument = errorDocument;
    }

    public String getIndexDocumentSuffix() {
        return this.indexDocumentSuffix;
    }

    public BucketWebsiteConfiguration withIndexDocumentSuffix(String indexDocumentSuffix) {
        this.indexDocumentSuffix = indexDocumentSuffix;
        return this;
    }

    public void setIndexDocumentSuffix(String indexDocumentSuffix) {
        this.indexDocumentSuffix = indexDocumentSuffix;
    }

    public String getErrorDocument() {
        return this.errorDocument;
    }

    public void setErrorDocument(String errorDocument) {
        this.errorDocument = errorDocument;
    }

    public void setRedirectAllRequestsTo(RedirectRule redirectAllRequestsTo) {
        this.redirectAllRequestsTo = redirectAllRequestsTo;
    }

    public RedirectRule getRedirectAllRequestsTo() {
        return this.redirectAllRequestsTo;
    }

    public BucketWebsiteConfiguration withRedirectAllRequestsTo(RedirectRule redirectAllRequestsTo) {
        this.redirectAllRequestsTo = redirectAllRequestsTo;
        return this;
    }

    public void setRoutingRules(List<RoutingRule> routingRules) {
        this.routingRules = routingRules;
    }

    public List<RoutingRule> getRoutingRules() {
        return this.routingRules;
    }

    public BucketWebsiteConfiguration withRoutingRules(List<RoutingRule> routingRules) {
        this.routingRules = routingRules;
        return this;
    }
}

