/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.people;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.people.SubjectType;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationAware;
import com.atlassian.confluence.api.nav.NavigationService;
import com.atlassian.confluence.api.serialization.RestEnrichable;
import com.atlassian.confluence.api.serialization.RestEnrichableProperty;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@RestEnrichable
public class Group
implements Subject,
NavigationAware {
    protected static final String GROUP_TYPE = "group";
    @RestEnrichableProperty
    @JsonIgnore
    private final String type = "group";
    @JsonProperty
    private final String name;

    @JsonCreator
    public Group(@JsonProperty(value="name") String name) {
        this.name = Objects.requireNonNull(name).toLowerCase();
    }

    public String getName() {
        return this.name;
    }

    @Override
    @JsonIgnore
    public String getDisplayName() {
        return this.name;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || !other.getClass().equals(this.getClass())) {
            return false;
        }
        String otherName = ((Group)other).name;
        return this.name.equals(otherName);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return "Group[" + this.name + "]";
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return navigationService.createNavigation().group(this);
    }

    @Override
    @JsonIgnore
    public SubjectType getSubjectType() {
        return SubjectType.GROUP;
    }
}

