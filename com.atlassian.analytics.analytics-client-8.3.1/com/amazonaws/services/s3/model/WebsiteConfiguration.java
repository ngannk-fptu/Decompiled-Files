/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.RoutingRule;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class WebsiteConfiguration
implements Serializable {
    private String indexDocumentSuffix;
    private String errorDocument;
    private String redirectAllRequestsTo;
    private List<RoutingRule> routingRules = new LinkedList<RoutingRule>();

    public void setIndexDocumentSuffix(String indexDocumentSuffix) {
        this.indexDocumentSuffix = indexDocumentSuffix;
    }

    public String getIndexDocumentSuffix() {
        return this.indexDocumentSuffix;
    }

    public WebsiteConfiguration withIndexDocumentSuffix(String indexDocumentSuffix) {
        this.indexDocumentSuffix = indexDocumentSuffix;
        return this;
    }

    public void setErrorDocument(String errorDocument) {
        this.errorDocument = errorDocument;
    }

    public String getErrorDocument() {
        return this.errorDocument;
    }

    public WebsiteConfiguration witherrorDocument(String errorDocument) {
        this.errorDocument = errorDocument;
        return this;
    }

    public void setRedirectAllRequestsTo(String redirectAllRequestsTo) {
        this.redirectAllRequestsTo = redirectAllRequestsTo;
    }

    public String getRedirectAllRequestsTo() {
        return this.redirectAllRequestsTo;
    }

    public WebsiteConfiguration withRedirectAllRequestsTo(String redirectAllRequestsTo) {
        this.redirectAllRequestsTo = redirectAllRequestsTo;
        return this;
    }

    public void setRoutingRules(List<RoutingRule> routingRules) {
        this.routingRules = routingRules;
    }

    public List<RoutingRule> getRoutingRule() {
        return this.routingRules;
    }

    public WebsiteConfiguration withRoutingRule(List<RoutingRule> routingRules) {
        this.routingRules = routingRules;
        return this;
    }
}

