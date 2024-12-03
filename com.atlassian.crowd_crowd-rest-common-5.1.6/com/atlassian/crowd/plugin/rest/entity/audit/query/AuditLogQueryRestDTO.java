/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.crowd.plugin.rest.entity.audit.query;

import com.atlassian.crowd.audit.AuditLogEventSource;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.plugin.rest.entity.audit.query.AuditLogAuthorRestrictionRestDTO;
import com.atlassian.crowd.plugin.rest.entity.audit.query.AuditLogEntityRestrictionRestDTO;
import com.atlassian.crowd.plugin.rest.util.ISO8601DateDeserializer;
import com.atlassian.crowd.plugin.rest.util.ISO8601DateSerializer;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class AuditLogQueryRestDTO {
    @JsonProperty(value="onOrAfter")
    @JsonSerialize(using=ISO8601DateSerializer.class)
    @JsonDeserialize(using=ISO8601DateDeserializer.class)
    private final Date onOrAfter;
    @JsonProperty(value="beforeOrOn")
    @JsonSerialize(using=ISO8601DateSerializer.class)
    @JsonDeserialize(using=ISO8601DateDeserializer.class)
    private final Date beforeOrOn;
    @JsonProperty(value="actions")
    private final List<AuditLogEventType> actions;
    @JsonProperty(value="sources")
    private final List<AuditLogEventSource> sources;
    @JsonProperty(value="authors")
    private final List<AuditLogAuthorRestrictionRestDTO> authors;
    @JsonProperty(value="users")
    private final List<AuditLogEntityRestrictionRestDTO> users;
    @JsonProperty(value="groups")
    private final List<AuditLogEntityRestrictionRestDTO> groups;
    @JsonProperty(value="applications")
    private final List<AuditLogEntityRestrictionRestDTO> applications;
    @JsonProperty(value="directories")
    private final List<AuditLogEntityRestrictionRestDTO> directories;

    @JsonCreator
    public AuditLogQueryRestDTO(@JsonProperty(value="onOrAfter") Date onOrAfter, @JsonProperty(value="beforeOrOn") Date beforeOrOn, @JsonProperty(value="actions") List<AuditLogEventType> actions, @JsonProperty(value="sources") List<AuditLogEventSource> sources, @JsonProperty(value="authors") List<AuditLogAuthorRestrictionRestDTO> authors, @JsonProperty(value="users") List<AuditLogEntityRestrictionRestDTO> users, @JsonProperty(value="groups") List<AuditLogEntityRestrictionRestDTO> groups, @JsonProperty(value="applications") List<AuditLogEntityRestrictionRestDTO> applications, @JsonProperty(value="directories") List<AuditLogEntityRestrictionRestDTO> directories) {
        this.onOrAfter = onOrAfter;
        this.beforeOrOn = beforeOrOn;
        this.actions = actions != null ? ImmutableList.copyOf(actions) : null;
        this.sources = sources != null ? ImmutableList.copyOf(sources) : null;
        this.authors = authors != null ? ImmutableList.copyOf(authors) : null;
        this.users = users != null ? ImmutableList.copyOf(users) : null;
        this.groups = groups != null ? ImmutableList.copyOf(groups) : null;
        this.applications = applications != null ? ImmutableList.copyOf(applications) : null;
        this.directories = directories != null ? ImmutableList.copyOf(directories) : null;
    }

    public Date getOnOrAfter() {
        return this.onOrAfter;
    }

    public Date getBeforeOrOn() {
        return this.beforeOrOn;
    }

    public List<AuditLogEventType> getActions() {
        return this.actions;
    }

    public List<AuditLogEventSource> getSources() {
        return this.sources;
    }

    public List<AuditLogAuthorRestrictionRestDTO> getAuthors() {
        return this.authors;
    }

    public List<AuditLogEntityRestrictionRestDTO> getUsers() {
        return this.users;
    }

    public List<AuditLogEntityRestrictionRestDTO> getGroups() {
        return this.groups;
    }

    public List<AuditLogEntityRestrictionRestDTO> getApplications() {
        return this.applications;
    }

    public List<AuditLogEntityRestrictionRestDTO> getDirectories() {
        return this.directories;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(AuditLogQueryRestDTO data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditLogQueryRestDTO that = (AuditLogQueryRestDTO)o;
        return Objects.equals(this.getOnOrAfter(), that.getOnOrAfter()) && Objects.equals(this.getBeforeOrOn(), that.getBeforeOrOn()) && Objects.equals(this.getActions(), that.getActions()) && Objects.equals(this.getSources(), that.getSources()) && Objects.equals(this.getAuthors(), that.getAuthors()) && Objects.equals(this.getUsers(), that.getUsers()) && Objects.equals(this.getGroups(), that.getGroups()) && Objects.equals(this.getApplications(), that.getApplications()) && Objects.equals(this.getDirectories(), that.getDirectories());
    }

    public int hashCode() {
        return Objects.hash(this.getOnOrAfter(), this.getBeforeOrOn(), this.getActions(), this.getSources(), this.getAuthors(), this.getUsers(), this.getGroups(), this.getApplications(), this.getDirectories());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("onOrAfter", (Object)this.getOnOrAfter()).add("beforeOrOn", (Object)this.getBeforeOrOn()).add("actions", this.getActions()).add("sources", this.getSources()).add("authors", this.getAuthors()).add("users", this.getUsers()).add("groups", this.getGroups()).add("applications", this.getApplications()).add("directories", this.getDirectories()).toString();
    }

    public static final class Builder {
        private Date onOrAfter;
        private Date beforeOrOn;
        private List<AuditLogEventType> actions = Lists.newArrayList();
        private List<AuditLogEventSource> sources = Lists.newArrayList();
        private List<AuditLogAuthorRestrictionRestDTO> authors = Lists.newArrayList();
        private List<AuditLogEntityRestrictionRestDTO> users = Lists.newArrayList();
        private List<AuditLogEntityRestrictionRestDTO> groups = Lists.newArrayList();
        private List<AuditLogEntityRestrictionRestDTO> applications = Lists.newArrayList();
        private List<AuditLogEntityRestrictionRestDTO> directories = Lists.newArrayList();

        private Builder() {
        }

        private Builder(AuditLogQueryRestDTO initialData) {
            this.onOrAfter = initialData.getOnOrAfter();
            this.beforeOrOn = initialData.getBeforeOrOn();
            this.actions = Lists.newArrayList(initialData.getActions());
            this.sources = Lists.newArrayList(initialData.getSources());
            this.authors = Lists.newArrayList(initialData.getAuthors());
            this.users = Lists.newArrayList(initialData.getUsers());
            this.groups = Lists.newArrayList(initialData.getGroups());
            this.applications = Lists.newArrayList(initialData.getApplications());
            this.directories = Lists.newArrayList(initialData.getDirectories());
        }

        public Builder setOnOrAfter(Date onOrAfter) {
            this.onOrAfter = onOrAfter;
            return this;
        }

        public Builder setBeforeOrOn(Date beforeOrOn) {
            this.beforeOrOn = beforeOrOn;
            return this;
        }

        public Builder setActions(List<AuditLogEventType> actions) {
            this.actions = actions;
            return this;
        }

        public Builder addAction(AuditLogEventType action) {
            this.actions.add(action);
            return this;
        }

        public Builder addActions(Iterable<AuditLogEventType> actions) {
            for (AuditLogEventType action : actions) {
                this.addAction(action);
            }
            return this;
        }

        public Builder setSources(List<AuditLogEventSource> sources) {
            this.sources = sources;
            return this;
        }

        public Builder addSource(AuditLogEventSource source) {
            this.sources.add(source);
            return this;
        }

        public Builder addSources(Iterable<AuditLogEventSource> sources) {
            for (AuditLogEventSource source : sources) {
                this.addSource(source);
            }
            return this;
        }

        public Builder setAuthors(List<AuditLogAuthorRestrictionRestDTO> authors) {
            this.authors = authors;
            return this;
        }

        public Builder addAuthor(AuditLogAuthorRestrictionRestDTO author) {
            this.authors.add(author);
            return this;
        }

        public Builder addAuthors(Iterable<AuditLogAuthorRestrictionRestDTO> authors) {
            for (AuditLogAuthorRestrictionRestDTO author : authors) {
                this.addAuthor(author);
            }
            return this;
        }

        public Builder setUsers(List<AuditLogEntityRestrictionRestDTO> users) {
            this.users = users;
            return this;
        }

        public Builder addUser(AuditLogEntityRestrictionRestDTO user) {
            this.users.add(user);
            return this;
        }

        public Builder addUsers(Iterable<AuditLogEntityRestrictionRestDTO> users) {
            for (AuditLogEntityRestrictionRestDTO user : users) {
                this.addUser(user);
            }
            return this;
        }

        public Builder setGroups(List<AuditLogEntityRestrictionRestDTO> groups) {
            this.groups = groups;
            return this;
        }

        public Builder addGroup(AuditLogEntityRestrictionRestDTO group) {
            this.groups.add(group);
            return this;
        }

        public Builder addGroups(Iterable<AuditLogEntityRestrictionRestDTO> groups) {
            for (AuditLogEntityRestrictionRestDTO group : groups) {
                this.addGroup(group);
            }
            return this;
        }

        public Builder setApplications(List<AuditLogEntityRestrictionRestDTO> applications) {
            this.applications = applications;
            return this;
        }

        public Builder addApplication(AuditLogEntityRestrictionRestDTO application) {
            this.applications.add(application);
            return this;
        }

        public Builder addApplications(Iterable<AuditLogEntityRestrictionRestDTO> applications) {
            for (AuditLogEntityRestrictionRestDTO application : applications) {
                this.addApplication(application);
            }
            return this;
        }

        public Builder setDirectories(List<AuditLogEntityRestrictionRestDTO> directories) {
            this.directories = directories;
            return this;
        }

        public Builder addDirectory(AuditLogEntityRestrictionRestDTO directory) {
            this.directories.add(directory);
            return this;
        }

        public Builder addDirectories(Iterable<AuditLogEntityRestrictionRestDTO> directories) {
            for (AuditLogEntityRestrictionRestDTO directory : directories) {
                this.addDirectory(directory);
            }
            return this;
        }

        public AuditLogQueryRestDTO build() {
            return new AuditLogQueryRestDTO(this.onOrAfter, this.beforeOrOn, this.actions, this.sources, this.authors, this.users, this.groups, this.applications, this.directories);
        }
    }
}

