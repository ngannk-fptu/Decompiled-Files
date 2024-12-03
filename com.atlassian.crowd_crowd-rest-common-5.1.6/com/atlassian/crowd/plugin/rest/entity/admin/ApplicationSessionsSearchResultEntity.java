/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.token.AuthenticationToken
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.token.AuthenticationToken;
import com.atlassian.crowd.plugin.rest.entity.admin.ApplicationEntity;
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
public class ApplicationSessionsSearchResultEntity {
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
    @JsonProperty(value="application")
    private ApplicationEntity application;

    @JsonCreator
    public ApplicationSessionsSearchResultEntity(@JsonProperty(value="randomHash") String randomHash, @JsonProperty(value="id") Long id, @JsonProperty(value="initialization") Date initialization, @JsonProperty(value="lastAccess") Long lastAccess, @JsonProperty(value="application") ApplicationEntity application) {
        this.randomHash = randomHash;
        this.id = id;
        this.initialization = initialization;
        this.lastAccess = lastAccess;
        this.application = application;
    }

    public static ApplicationSessionsSearchResultEntity fromToken(AuthenticationToken session, Application application) {
        return new ApplicationSessionsSearchResultEntity(session.getRandomHash(), session.getId(), session.getCreatedDate(), session.getLastAccessedTime(), new ApplicationEntity(application.getId(), application.getName(), application.getDescription(), application.getType(), application.isActive(), application.isAliasingEnabled(), application.isLowerCaseOutput(), application.isMembershipAggregationEnabled(), application.isCachedDirectoriesAuthenticationOrderOptimisationEnabled()));
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

    public ApplicationEntity getApplication() {
        return this.application;
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
        ApplicationSessionsSearchResultEntity that = (ApplicationSessionsSearchResultEntity)o;
        return Objects.equals(this.randomHash, that.randomHash) && Objects.equals(this.id, that.id) && Objects.equals(this.initialization, that.initialization) && Objects.equals(this.lastAccess, that.lastAccess) && Objects.equals(this.application, that.application);
    }

    public int hashCode() {
        return Objects.hash(this.randomHash, this.id, this.initialization, this.lastAccess, this.application);
    }
}

