/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.EnumWithKey;
import com.atlassian.marketplace.client.model.ApplicationStatus;
import com.atlassian.marketplace.client.model.Entity;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.RequiredLink;
import io.atlassian.fugue.Option;
import java.net.URI;

public class Application
implements Entity {
    Links _links;
    @RequiredLink(rel="self")
    URI selfUri;
    String name;
    ApplicationKey key;
    String introduction;
    ApplicationStatus status;
    ConnectSupport atlassianConnectSupport;
    CompatibilityUpdateMode compatibilityMode;
    Details details;
    HostingSupport hostingSupport;
    @Deprecated
    @ReadOnly
    Option<Integer> cloudFreeUsers;

    @Override
    public Links getLinks() {
        return this._links;
    }

    @Override
    public URI getSelfUri() {
        return this.selfUri;
    }

    public String getName() {
        return this.name;
    }

    public ApplicationKey getKey() {
        return this.key;
    }

    public ApplicationStatus getStatus() {
        return this.status;
    }

    public String getIntroduction() {
        return this.introduction;
    }

    public CompatibilityUpdateMode getCompatibilityUpdateMode() {
        return this.compatibilityMode;
    }

    public String getDescription() {
        return this.details.description;
    }

    public URI getLearnMoreUri() {
        return this.details.learnMore;
    }

    public Option<URI> getDownloadPageUri() {
        return this.details.downloadPage;
    }

    @Deprecated
    public Option<Integer> getCloudFreeUsers() {
        return this.cloudFreeUsers;
    }

    static class HostingSupport {
        HostingModelSupport cloud;
        HostingModelSupport server;

        HostingSupport() {
        }
    }

    static class HostingModelSupport {
        Boolean enabled;

        HostingModelSupport() {
        }
    }

    static class Details {
        String description;
        URI learnMore;
        Option<URI> downloadPage;

        Details() {
        }
    }

    static class ConnectSupport {
        Boolean cloud;
        Boolean server;

        ConnectSupport() {
        }
    }

    public static enum CompatibilityUpdateMode implements EnumWithKey
    {
        MICRO_VERSIONS("micro"),
        MINOR_VERSIONS("minor");

        private String key;

        private CompatibilityUpdateMode(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return this.key;
        }
    }
}

