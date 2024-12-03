/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.http.trust.TrustedConnectionStatus
 *  com.google.common.base.Supplier
 *  org.jdom.Element
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.confluence.extra.jira.JiraIssuesManager;
import com.atlassian.confluence.util.http.trust.TrustedConnectionStatus;
import com.google.common.base.Supplier;
import java.io.Serializable;
import org.jdom.Element;

public class Channel
implements Serializable {
    private final String sourceUrl;
    private final Supplier<Element> elementSupplier;
    private final TrustedConnectionStatus trustedConnectionStatus;

    public Channel(String sourceUrl, Element channelElement, TrustedConnectionStatus trustedConnectionStatus) {
        this.sourceUrl = sourceUrl;
        this.elementSupplier = new JiraIssuesManager.DomBasedSupplier(channelElement);
        this.trustedConnectionStatus = trustedConnectionStatus;
    }

    public Channel(String sourceUrl, byte[] bytes, TrustedConnectionStatus trustedConnectionStatus) {
        this.sourceUrl = sourceUrl;
        this.elementSupplier = new JiraIssuesManager.ByteStreamBasedSupplier(bytes);
        this.trustedConnectionStatus = trustedConnectionStatus;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public Element getChannelElement() {
        return (Element)this.elementSupplier.get();
    }

    public TrustedConnectionStatus getTrustedConnectionStatus() {
        return this.trustedConnectionStatus;
    }

    public boolean isTrustedConnection() {
        return this.trustedConnectionStatus != null;
    }
}

