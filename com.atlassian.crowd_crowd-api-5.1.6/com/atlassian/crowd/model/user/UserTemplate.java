/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.model.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.model.user.User;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserTemplate
implements User,
Serializable {
    private long directoryId;
    private String name;
    private boolean active;
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String displayName;
    private String externalId;

    public UserTemplate(String username, long directoryId) {
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)username), (String)"username argument cannot be null or blank", (Object[])new Object[0]);
        this.name = username;
        this.directoryId = directoryId;
    }

    public UserTemplate(String name) {
        this(name, -1L);
    }

    public UserTemplate(User user) {
        Validate.notNull((Object)user, (String)"user argument cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)user.getName()), (String)"user.name argument cannot be null or blank", (Object[])new Object[0]);
        this.name = user.getName();
        this.directoryId = user.getDirectoryId();
        this.active = user.isActive();
        this.emailAddress = user.getEmailAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.displayName = user.getDisplayName();
        this.externalId = user.getExternalId();
    }

    public UserTemplate(String username, String firstName, String lastName, String displayName) {
        this(username);
        this.displayName = displayName;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public UserTemplate(com.atlassian.crowd.embedded.api.User user) {
        Validate.notNull((Object)user, (String)"user argument cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)user.getName()), (String)"user.name argument cannot be null or blank", (Object[])new Object[0]);
        this.name = user.getName();
        this.directoryId = user.getDirectoryId();
        this.active = user.isActive();
        this.emailAddress = user.getEmailAddress();
        this.displayName = user.getDisplayName();
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return this.name;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName == null ? "" : displayName;
    }

    public boolean equals(Object o) {
        return UserComparator.equalsObject((com.atlassian.crowd.embedded.api.User)this, (Object)o);
    }

    public int hashCode() {
        return UserComparator.hashCode((com.atlassian.crowd.embedded.api.User)this);
    }

    public int compareTo(com.atlassian.crowd.embedded.api.User other) {
        return UserComparator.compareTo((com.atlassian.crowd.embedded.api.User)this, (com.atlassian.crowd.embedded.api.User)other);
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.name).append("directoryId", this.directoryId).append("active", this.active).append("emailAddress", (Object)this.emailAddress).append("firstName", (Object)this.firstName).append("lastName", (Object)this.lastName).append("displayName", (Object)this.displayName).append("externalId", (Object)this.externalId).toString();
    }
}

