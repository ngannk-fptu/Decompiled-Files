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
public class Version
implements NavigationAware {
    @JsonProperty
    private final Person by;
    @JsonProperty
    private final DateTime when;
    @JsonProperty
    private final String message;
    @JsonProperty
    private final int number;
    @JsonProperty
    private final boolean minorEdit;
    @JsonProperty
    private final boolean hidden;
    @JsonProperty
    private final String syncRev;
    @JsonDeserialize(as=ExpandedReference.class, contentAs=Content.class)
    @JsonProperty
    private final Reference<Content> content;

    public static VersionBuilder builder() {
        return new VersionBuilder();
    }

    public static VersionBuilder builder(Version version) {
        return new VersionBuilder().by(version.by).message(version.message).minorEdit(version.minorEdit).hidden(version.hidden).number(version.number).when((ReadableDateTime)version.when).syncRev(version.syncRev).content(version.content);
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        if (!this.content.exists()) {
            return null;
        }
        return navigationService.createNavigation().experimental().content(this.content).version(this);
    }

    public static Reference<Version> buildReference(int number) {
        return Reference.collapsed(Version.class, Collections.singletonMap(IdProperties.number, number));
    }

    @Deprecated
    public static Reference<Version> buildReference(Reference<Content> contentRef, int number) {
        return Version.buildReference(number);
    }

    public static int getVersionNumber(Reference<Version> versionRef) {
        if (versionRef == null) {
            return 0;
        }
        Object numberProperty = versionRef.getIdProperty(IdProperties.number);
        if (numberProperty == null) {
            return 0;
        }
        return (Integer)numberProperty;
    }

    @JsonCreator
    private Version() {
        this(Version.builder());
    }

    private Version(VersionBuilder builder) {
        this.by = builder.by;
        this.when = builder.when;
        this.message = builder.message;
        this.number = builder.number;
        this.minorEdit = builder.minorEdit;
        this.hidden = builder.hidden;
        this.syncRev = builder.syncRev;
        this.content = Reference.orEmpty(builder.content, Content.class);
    }

    public Person getBy() {
        return this.by;
    }

    @Deprecated
    public DateTime getWhen() {
        return this.when;
    }

    @JsonIgnore
    public OffsetDateTime getWhenAt() {
        return JodaTimeUtils.convert(this.when);
    }

    public String getMessage() {
        return this.message;
    }

    public int getNumber() {
        return this.number;
    }

    public boolean isMinorEdit() {
        return this.minorEdit;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public String getSyncRev() {
        return this.syncRev;
    }

    public VersionBuilder nextBuilder() {
        return Version.builder().number(this.number + 1);
    }

    public Reference<Content> getContentRef() {
        return this.content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Version version = (Version)o;
        return this.number == version.number;
    }

    public int hashCode() {
        return Objects.hash(this.number);
    }

    public String toString() {
        return "Version{by=" + this.by + ", when=" + this.when + ", message='" + this.message + '\'' + ", number=" + this.number + ", minorEdit=" + this.minorEdit + ", hidden=" + this.hidden + ", syncRev='" + this.syncRev + '\'' + ", content=" + this.content + '}';
    }

    public static class Expansions {
        public static final String BY = "by";
        public static final String CONTENT = "content";
    }

    public static class VersionBuilder {
        private Person by;
        private DateTime when;
        private String message;
        private int number;
        private boolean minorEdit;
        private boolean hidden;
        private String syncRev;
        private Reference<Content> content = Reference.empty(Content.class);

        private VersionBuilder() {
        }

        public Version build() {
            return new Version(this);
        }

        public VersionBuilder by(Person by) {
            this.by = by;
            return this;
        }

        public VersionBuilder when(Date when) {
            if (when != null) {
                this.when = new DateTime((Object)when);
            }
            return this;
        }

        public VersionBuilder when(OffsetDateTime when) {
            return this.when((ReadableDateTime)JodaTimeUtils.convert(when));
        }

        @Deprecated
        public VersionBuilder when(ReadableDateTime readableDateTime) {
            if (readableDateTime != null) {
                this.when = readableDateTime.toDateTime();
            }
            return this;
        }

        public VersionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public VersionBuilder number(int number) {
            this.number = number;
            return this;
        }

        public VersionBuilder minorEdit(boolean minorEdit) {
            this.minorEdit = minorEdit;
            return this;
        }

        public VersionBuilder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        public VersionBuilder syncRev(String syncRev) {
            this.syncRev = syncRev;
            return this;
        }

        public VersionBuilder content(Reference<Content> content) {
            this.content = content;
            return this;
        }
    }

    static enum IdProperties {
        number;

    }
}

