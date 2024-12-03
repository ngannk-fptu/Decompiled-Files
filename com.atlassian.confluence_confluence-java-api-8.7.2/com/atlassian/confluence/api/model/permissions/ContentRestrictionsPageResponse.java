/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class ContentRestrictionsPageResponse
extends PageResponseImpl<ContentRestriction>
implements NavigationAware {
    @JsonProperty
    private final String restrictionsHash;
    @JsonProperty
    private final Map<LinkType, Link> links;
    @JsonIgnore
    private final ContentId contentId;

    @JsonCreator
    private ContentRestrictionsPageResponse() {
        this(ContentRestrictionsPageResponse.builder());
    }

    private ContentRestrictionsPageResponse(ContentRestrictionPageResponseBuilder builder) {
        super(builder);
        this.restrictionsHash = builder.restrictionsHash;
        this.links = Collections.unmodifiableMap(builder.links);
        this.contentId = builder.contentId;
    }

    public Map<LinkType, Link> getLinks() {
        return this.links;
    }

    public static ContentRestrictionPageResponseBuilder builder() {
        return new ContentRestrictionPageResponseBuilder();
    }

    public String getRestrictionsHash() {
        return this.restrictionsHash;
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return navigationService.createNavigation().experimental().content(ContentSelector.fromId(this.contentId)).restrictions();
    }

    public static class ContentRestrictionPageResponseBuilder
    extends PageResponseImpl.Builder<ContentRestriction, ContentRestrictionPageResponseBuilder> {
        private ContentId contentId = ContentId.UNSET;
        private String restrictionsHash = "";
        private final Map<LinkType, Link> links = new HashMap<LinkType, Link>();

        private ContentRestrictionPageResponseBuilder() {
        }

        public ContentRestrictionsPageResponse build() {
            return new ContentRestrictionsPageResponse(this);
        }

        public ContentRestrictionPageResponseBuilder withContentId(ContentId contentId) {
            if (contentId != null) {
                this.contentId = contentId;
            }
            return this;
        }

        public ContentRestrictionPageResponseBuilder withRestrictionsHash(@Nullable String restrictionsHash) {
            this.restrictionsHash = restrictionsHash == null ? "" : restrictionsHash;
            return this;
        }

        public ContentRestrictionPageResponseBuilder addLink(Link link) {
            this.links.put(link.getType(), link);
            return this;
        }

        public ContentRestrictionPageResponseBuilder addLink(LinkType type, String path) {
            return this.addLink(new Link(type, path));
        }
    }
}

