/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 *  net.jcip.annotations.Immutable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.gadgets.spec;

import com.atlassian.gadgets.spec.Feature;
import com.atlassian.gadgets.spec.UserPrefSpec;
import com.atlassian.gadgets.view.ViewType;
import com.atlassian.plugin.util.Assertions;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Immutable
public final class GadgetSpec {
    private final URI specUri;
    private final Iterable<UserPrefSpec> userPrefs;
    private final boolean scrolling;
    private final int height;
    private final int width;
    private final String title;
    private final URI titleUrl;
    private final URI thumbnail;
    private final String author;
    private final String authorEmail;
    private final String description;
    private final String directoryTitle;
    private final Map<String, Feature> features;
    private final Iterable<String> unsupportedFeatureNames;
    private final Set<String> viewsNames;

    private GadgetSpec(Builder builder) {
        this.specUri = builder.specUri;
        LinkedList<UserPrefSpec> userPrefsCopy = new LinkedList<UserPrefSpec>();
        for (UserPrefSpec userPrefSpec : builder.userPrefs) {
            userPrefsCopy.add(userPrefSpec);
        }
        this.userPrefs = Collections.unmodifiableList(userPrefsCopy);
        this.scrolling = builder.scrolling;
        this.height = builder.height;
        this.width = builder.width;
        this.title = builder.title;
        this.titleUrl = builder.titleUrl;
        this.thumbnail = builder.thumbnail;
        this.author = builder.author;
        this.authorEmail = builder.authorEmail;
        this.description = builder.description;
        this.directoryTitle = builder.directoryTitle;
        this.features = Collections.unmodifiableMap(new HashMap(builder.features));
        LinkedList<String> unsupportedFeatureNamesCopy = new LinkedList<String>();
        for (String unsupportedFeatureName : builder.unsupportedFeatureNames) {
            unsupportedFeatureNamesCopy.add(unsupportedFeatureName);
        }
        this.unsupportedFeatureNames = Collections.unmodifiableList(unsupportedFeatureNamesCopy);
        this.viewsNames = Collections.unmodifiableSet(new HashSet(builder.viewsNames));
    }

    public URI getUrl() {
        return this.specUri;
    }

    public Iterable<UserPrefSpec> getUserPrefs() {
        return this.userPrefs;
    }

    public boolean supportsViewType(ViewType viewType) {
        if (this.viewsNames.isEmpty()) {
            return false;
        }
        if (this.viewsNames.contains(viewType.getCanonicalName())) {
            return true;
        }
        Iterator<String> it = viewType.getAliases().iterator();
        while (it.hasNext()) {
            if (!this.viewsNames.contains(it.next())) continue;
            return true;
        }
        return false;
    }

    public boolean isScrolling() {
        return this.scrolling;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public String getTitle() {
        return this.title;
    }

    public URI getTitleUrl() {
        return this.titleUrl;
    }

    public URI getThumbnail() {
        return this.thumbnail;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getAuthorEmail() {
        return this.authorEmail;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDirectoryTitle() {
        return this.directoryTitle;
    }

    public Map<String, Feature> getFeatures() {
        return this.features;
    }

    public Iterable<String> getUnsupportedFeatureNames() {
        return this.unsupportedFeatureNames;
    }

    public static Builder gadgetSpec(URI specUri) {
        return new Builder(specUri);
    }

    public static Builder gadgetSpec(GadgetSpec gadgetSpec) {
        return new Builder(gadgetSpec);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("title", (Object)this.getTitle()).append("url", (Object)this.getUrl()).toString();
    }

    public static class Builder {
        private final URI specUri;
        private Iterable<UserPrefSpec> userPrefs = Collections.emptySet();
        private boolean scrolling;
        private int height;
        private int width;
        private String title;
        private URI titleUrl;
        private URI thumbnail;
        private String author;
        private String authorEmail;
        private String description;
        private String directoryTitle;
        private Map<String, Feature> features = Collections.emptyMap();
        private Iterable<String> unsupportedFeatureNames = Collections.emptySet();
        private Set<String> viewsNames = Collections.emptySet();

        private Builder(URI specUri) {
            this.specUri = (URI)Assertions.notNull((String)"specUri", (Object)specUri);
        }

        private Builder(GadgetSpec spec) {
            Assertions.notNull((String)"spec", (Object)spec);
            this.specUri = spec.specUri;
            this.userPrefs = spec.userPrefs;
            this.scrolling = spec.scrolling;
            this.height = spec.height;
            this.width = spec.width;
            this.title = spec.title;
            this.titleUrl = spec.titleUrl;
            this.thumbnail = spec.thumbnail;
            this.author = spec.author;
            this.authorEmail = spec.authorEmail;
            this.description = spec.description;
            this.directoryTitle = spec.directoryTitle;
            this.features = spec.features;
            this.unsupportedFeatureNames = spec.unsupportedFeatureNames;
            this.viewsNames = spec.viewsNames;
        }

        public Builder userPrefs(Iterable<UserPrefSpec> userPrefs) {
            this.userPrefs = (Iterable)Assertions.notNull((String)"userPrefs", userPrefs);
            return this;
        }

        public Builder scrolling(boolean scrolling) {
            this.scrolling = scrolling;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder titleUrl(URI titleUrl) {
            this.titleUrl = titleUrl;
            return this;
        }

        public Builder thumbnail(URI thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder authorEmail(String authorEmail) {
            this.authorEmail = authorEmail;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder directoryTitle(String directoryTitle) {
            this.directoryTitle = directoryTitle;
            return this;
        }

        public Builder features(Map<String, Feature> features) {
            this.features = (Map)Assertions.notNull((String)"features", features);
            return this;
        }

        public Builder unsupportedFeatureNames(Iterable<String> unsupportedFeatureNames) {
            this.unsupportedFeatureNames = (Iterable)Assertions.notNull((String)"unsupportedFeatureNames", unsupportedFeatureNames);
            return this;
        }

        public Builder viewsNames(Set<String> viewsNames) {
            this.viewsNames = (Set)Assertions.notNull((String)"viewsNames", viewsNames);
            return this;
        }

        public GadgetSpec build() {
            return new GadgetSpec(this);
        }
    }
}

