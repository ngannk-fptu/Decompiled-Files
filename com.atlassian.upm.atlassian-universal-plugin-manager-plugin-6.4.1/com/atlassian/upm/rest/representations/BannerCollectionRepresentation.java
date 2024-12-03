/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import com.atlassian.marketplace.client.api.Page;
import com.atlassian.marketplace.client.model.AddonReference;
import com.atlassian.marketplace.client.model.ImageInfo;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class BannerCollectionRepresentation {
    @JsonProperty
    private final Map<String, URI> links;
    @JsonProperty
    private final int count;
    @JsonProperty
    private final Collection<BannerEntry> banners;

    @JsonCreator
    public BannerCollectionRepresentation(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="banners") Collection<BannerEntry> banners, @JsonProperty(value="count") Integer count) {
        this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
        this.banners = Collections.unmodifiableList(new ArrayList<BannerEntry>(banners));
        this.count = count;
    }

    public BannerCollectionRepresentation(Page<AddonReference> bannerPage, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder) {
        this.links = this.buildLinks(bannerPage, uriBuilder, linkBuilder);
        this.banners = Collections.unmodifiableList(StreamSupport.stream(bannerPage.spliterator(), false).map(BannerCollectionRepresentation.toBannerEntry(uriBuilder, linkBuilder)).filter(Option::isDefined).map(Option::get).collect(Collectors.toList()));
        this.count = bannerPage.totalSize();
    }

    private Map<String, URI> buildLinks(Page<AddonReference> page, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder) {
        int nextPageOffset = page.safeGetNext().map(next -> next.getBounds().getOffset()).orElse(0);
        int prevPageOffset = page.safeGetPrevious().map(prev -> prev.getBounds().getOffset()).orElseGet(() -> page.totalSize() - page.totalSize() % 10);
        if (prevPageOffset == page.totalSize()) {
            prevPageOffset = page.totalSize() - 10;
        }
        return linkBuilder.buildLinkForSelf(uriBuilder.buildBannersUri(page.getOffset())).put("next", uriBuilder.buildBannersUri(nextPageOffset)).put("prev", uriBuilder.buildBannersUri(prevPageOffset)).build();
    }

    public Map<String, URI> getLinks() {
        return this.links;
    }

    public Iterable<BannerEntry> getBanners() {
        return this.banners;
    }

    public int getCount() {
        return this.count;
    }

    private static Function<AddonReference, Option<BannerEntry>> toBannerEntry(UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder) {
        return banner -> {
            Iterator iterator = banner.getImage().iterator();
            if (iterator.hasNext()) {
                ImageInfo image = (ImageInfo)iterator.next();
                return Option.some(new BannerEntry((AddonReference)banner, image, uriBuilder, linkBuilder));
            }
            return Option.none();
        };
    }

    private static class BannerImage {
        @JsonProperty
        private final Map<String, URI> links;
        @JsonProperty
        private final String imageType;
        @JsonProperty
        private final String altText;

        @JsonCreator
        public BannerImage(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="imageType") String imageType, @JsonProperty(value="altText") String altText) {
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
            this.imageType = imageType;
            this.altText = altText;
        }

        public BannerImage(AddonReference addon, ImageInfo image) {
            this.links = Collections.singletonMap("binary", image.getImageUri());
            this.imageType = (String)image.getImageContentType(ImageInfo.Size.DEFAULT_SIZE, ImageInfo.Resolution.DEFAULT_RESOLUTION).getOrElse((Object)"");
            this.altText = addon.getName();
        }
    }

    private static class BannerEntry {
        @JsonProperty
        private final Map<String, URI> links;
        @JsonProperty
        private final String pluginKey;
        @JsonProperty
        private final BannerImage image;

        @JsonCreator
        public BannerEntry(@JsonProperty(value="links") Map<String, URI> links, @JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="image") BannerImage image) {
            this.links = Collections.unmodifiableMap(new HashMap<String, URI>(links));
            this.pluginKey = pluginKey;
            this.image = image;
        }

        public BannerEntry(AddonReference addon, ImageInfo image, UpmUriBuilder uriBuilder, UpmLinkBuilder linkBuilder) {
            this.links = linkBuilder.buildLinkForSelf(uriBuilder.buildAvailablePluginUri(addon.getKey())).putIfPermitted(Permission.GET_AVAILABLE_PLUGINS, "singlePluginViewLink", uriBuilder.buildUpmSinglePluginViewUri(addon.getKey())).build();
            this.pluginKey = addon.getKey();
            this.image = new BannerImage(addon, image);
        }
    }
}

