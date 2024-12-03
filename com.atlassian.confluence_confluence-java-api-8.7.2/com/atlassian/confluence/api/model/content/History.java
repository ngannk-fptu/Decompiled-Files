/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableDateTime
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Contributors;
import com.atlassian.confluence.api.model.content.Version;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.util.JodaTimeUtils;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

@ExperimentalApi
@RestEnrichable
@JsonIgnoreProperties(ignoreUnknown=true)
public class History
implements NavigationAware {
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Version.class)
    @JsonProperty
    private final Reference<Version> previousVersion;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Version.class)
    @JsonProperty
    private final Reference<Version> nextVersion;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Version.class)
    @JsonProperty
    private final Reference<Version> lastUpdated;
    @JsonProperty
    private final boolean latest;
    @JsonProperty
    private final Person createdBy;
    @JsonProperty
    private final DateTime createdDate;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Contributors.class)
    @JsonProperty
    private final Reference<Contributors> contributors;
    @JsonIgnore
    private final Reference<Content> contentParent;

    public static HistoryBuilder builder() {
        return new HistoryBuilder();
    }

    public static Reference<History> buildReference(Reference<Content> contentParent) {
        return Reference.collapsed(History.class, Collections.singletonMap(IdProperties.contentParent, contentParent));
    }

    public static Reference<Content> getParentReference(Reference<History> historyReference) {
        return (Reference)historyReference.getIdProperty(IdProperties.contentParent);
    }

    @JsonCreator
    private History() {
        this(History.builder());
    }

    private History(HistoryBuilder builder) {
        this.previousVersion = builder.previousVersion;
        this.nextVersion = builder.nextVersion;
        this.lastUpdated = builder.lastUpdated;
        this.latest = builder.latest;
        this.createdBy = builder.createdBy;
        this.createdDate = builder.createdDate;
        this.contributors = builder.contributors;
        this.contentParent = builder.contentReference;
    }

    public Reference<Version> getLastUpdatedRef() {
        return this.lastUpdated;
    }

    public Reference<Version> getNextVersionRef() {
        return this.nextVersion;
    }

    public Reference<Version> getPreviousVersionRef() {
        return this.previousVersion;
    }

    public boolean isLatest() {
        return this.latest;
    }

    public Person getCreatedBy() {
        return this.createdBy;
    }

    @Deprecated
    public DateTime getCreatedDate() {
        return this.createdDate;
    }

    @JsonIgnore
    public OffsetDateTime getCreatedAt() {
        return JodaTimeUtils.convert(this.createdDate);
    }

    public Reference<Contributors> getContributors() {
        return this.contributors;
    }

    public Reference<Content> getContentParentRef() {
        return this.contentParent;
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        if (!this.contentParent.exists()) {
            return null;
        }
        return navigationService.createNavigation().content(this.contentParent).history();
    }

    public static class Expansions {
        public static final String CONTRIBUTORS = "contributors";
        public static final String CREATED_BY = "createdBy";
        public static final String LATEST = "lastUpdated";
        public static final String NEXT = "nextVersion";
        public static final String PREVIOUS = "previousVersion";
    }

    public static class HistoryBuilder {
        private Reference<Version> previousVersion = Reference.empty(Version.class);
        private Reference<Version> nextVersion = Reference.empty(Version.class);
        private Reference<Version> lastUpdated = Reference.empty(Version.class);
        private boolean latest = false;
        private Person createdBy = null;
        private DateTime createdDate = null;
        private Reference<Content> contentReference;
        private Reference<Contributors> contributors;

        private HistoryBuilder() {
        }

        public History build() {
            return new History(this);
        }

        public HistoryBuilder createdBy(Person createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public HistoryBuilder createdDate(Date createdDate) {
            this.createdDate = new DateTime((Object)Objects.requireNonNull(createdDate));
            return this;
        }

        @Deprecated
        public HistoryBuilder createdDate(ReadableDateTime createdDate) {
            this.createdDate = createdDate.toDateTime();
            return this;
        }

        public HistoryBuilder createdDate(OffsetDateTime createdDate) {
            this.createdDate = JodaTimeUtils.convert(createdDate);
            return this;
        }

        public HistoryBuilder lastUpdated(Reference<Version> lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public HistoryBuilder contributors(Reference<Contributors> contributors) {
            this.contributors = contributors;
            return this;
        }

        public HistoryBuilder latest(boolean latest) {
            this.latest = latest;
            return this;
        }

        public HistoryBuilder nextVersion(Reference<Version> nextVersion) {
            this.nextVersion = nextVersion;
            return this;
        }

        public HistoryBuilder previousVersion(Reference<Version> previousVersion) {
            this.previousVersion = previousVersion;
            return this;
        }

        public HistoryBuilder content(Reference<Content> content) {
            this.contentReference = content;
            return this;
        }
    }

    static enum IdProperties {
        contentParent;

    }
}

