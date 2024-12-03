/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.sal.api.user.UserKey
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonDeserialize
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.people.UserStatus;
import com.atlassian.confluence.api.model.reference.ExpandedReference;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.serialization.RestEnrichableProperty;
import com.atlassian.sal.api.user.UserKey;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@RestEnrichable
public class KnownUser
extends User
implements NavigationAware {
    @JsonIgnore
    @RestEnrichableProperty
    private final String type = "known";
    @JsonDeserialize(as=ExpandedReference.class, contentAs=UserStatus.class)
    @JsonProperty
    private final Reference<UserStatus> status;

    @JsonCreator
    public KnownUser(@JsonProperty(value="profilePicture") Icon profilePicture, @JsonProperty(value="username") String username, @JsonProperty(value="displayName") String displayName, @JsonProperty(value="userKey") String userKey) {
        super(profilePicture, username, displayName, userKey);
        this.status = Reference.collapsed(UserStatus.class);
    }

    @Deprecated
    public KnownUser(Icon profilePicture, String username, String displayName, UserKey userKey) {
        super(profilePicture, username, displayName, userKey);
        this.status = Reference.collapsed(UserStatus.class);
    }

    @Deprecated
    public KnownUser(Icon profilePicture, String username, String displayName) {
        this(profilePicture, username, displayName, (UserKey)null);
    }

    private KnownUser(Builder builder) {
        super(builder.profilePicture, Objects.requireNonNull(builder.username), Objects.requireNonNull(builder.displayName), Objects.requireNonNull(builder.userKey));
        this.status = builder.status != null ? builder.status : Reference.collapsed(UserStatus.class);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return this.optionalUserKey().map(key -> navigationService.createNavigation().user((UserKey)key)).orElse(null);
    }

    public Reference<UserStatus> getStatusRef() {
        return this.status;
    }

    public static class Builder {
        private Icon profilePicture;
        private String username;
        private UserKey userKey;
        private String displayName;
        private Reference<UserStatus> status;

        public Builder profilePicture(Icon profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder userKey(UserKey userKey) {
            this.userKey = userKey;
            return this;
        }

        public Builder userKey(String userKey) {
            this.userKey = new UserKey(userKey);
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = Reference.to(status);
            return this;
        }

        public KnownUser build() {
            return new KnownUser(this);
        }
    }
}

