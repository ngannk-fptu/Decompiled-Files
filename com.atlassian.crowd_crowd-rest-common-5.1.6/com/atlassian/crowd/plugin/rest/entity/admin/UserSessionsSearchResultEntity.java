/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.token.AuthenticationToken
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.token.AuthenticationToken;
import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryData;
import com.atlassian.crowd.plugin.rest.util.ISO8601DateDeserializer;
import com.atlassian.crowd.plugin.rest.util.ISO8601DateSerializer;
import java.util.Date;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class UserSessionsSearchResultEntity {
    @JsonProperty(value="randomHash")
    private String randomHash;
    @JsonProperty(value="id")
    private Long id;
    @JsonProperty(value="initialization")
    @JsonSerialize(using=ISO8601DateSerializer.class)
    @JsonDeserialize(using=ISO8601DateDeserializer.class)
    private Date initialization;
    @JsonProperty(value="lastAccess")
    private Long lastAccess;
    @JsonProperty(value="directory")
    private DirectoryData directory;
    @JsonProperty(value="username")
    private String username;

    @JsonCreator
    public UserSessionsSearchResultEntity(@JsonProperty(value="randomHash") String randomHash, @JsonProperty(value="id") Long id, @JsonProperty(value="initialization") Date initialization, @JsonProperty(value="lastAccess") Long lastAccess, @JsonProperty(value="directory") DirectoryData directory, @JsonProperty(value="username") String username) {
        this.id = id;
        this.randomHash = randomHash;
        this.initialization = initialization;
        this.lastAccess = lastAccess;
        this.directory = directory;
        this.username = username;
    }

    public static UserSessionsSearchResultEntity fromToken(AuthenticationToken session, Directory directory) {
        return new UserSessionsSearchResultEntity(session.getRandomHash(), session.getId(), session.getCreatedDate(), session.getLastAccessedTime(), DirectoryData.fromDirectory(directory), session.getName());
    }

    public Long getId() {
        return this.id;
    }

    public Date getInitialization() {
        return this.initialization;
    }

    public Long getLastAccess() {
        return this.lastAccess;
    }

    public DirectoryData getDirectory() {
        return this.directory;
    }

    public String getUsername() {
        return this.username;
    }

    public String getRandomHash() {
        return this.randomHash;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UserSessionsSearchResultEntity that = (UserSessionsSearchResultEntity)o;
        return Objects.equals(this.randomHash, that.randomHash) && Objects.equals(this.id, that.id) && Objects.equals(this.initialization, that.initialization) && Objects.equals(this.lastAccess, that.lastAccess) && Objects.equals(this.directory, that.directory) && Objects.equals(this.username, that.username);
    }

    public int hashCode() {
        return Objects.hash(this.randomHash, this.id, this.initialization, this.lastAccess, this.directory, this.username);
    }
}

