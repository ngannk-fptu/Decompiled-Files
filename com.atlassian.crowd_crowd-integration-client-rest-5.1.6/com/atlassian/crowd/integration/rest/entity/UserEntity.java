/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.integration.rest.entity;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntity;
import com.atlassian.crowd.integration.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.integration.rest.entity.PasswordEntity;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name="user")
@XmlAccessorType(value=XmlAccessType.FIELD)
public class UserEntity
implements UserWithAttributes,
TimestampedUser {
    @XmlAttribute(name="name")
    private final String name;
    @XmlElement(name="first-name")
    private final String firstName;
    @XmlElement(name="last-name")
    private final String lastName;
    @XmlElement(name="display-name")
    private final String displayName;
    @XmlElement(name="email")
    private final String emailAddress;
    @XmlElement(name="password")
    private final PasswordEntity password;
    @XmlElement(name="encrypted-password")
    private final PasswordEntity encryptedPassword;
    @XmlElement(name="active")
    private final boolean active;
    @XmlElement(name="created-date")
    private final Date createdDate;
    @XmlElement(name="updated-date")
    private final Date updatedDate;
    @XmlElement(name="key")
    private final String key;
    @XmlElement(name="attributes")
    @Nullable
    private MultiValuedAttributeEntityList attributes;

    private UserEntity() {
        this(null, null, null, null, null, null, false, null, null, null);
    }

    public UserEntity(String name, String firstName, String lastName, String displayName, String emailAddress, PasswordEntity password, boolean active, String key, Date createdDate, Date updatedDate) {
        this(name, firstName, lastName, displayName, emailAddress, password, active, key, createdDate, updatedDate, false);
    }

    public UserEntity(String name, String firstName, String lastName, String displayName, String emailAddress, PasswordEntity password, boolean active, String key, Date createdDate, Date updatedDate, boolean isPasswordEncrypted) {
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        if (isPasswordEncrypted) {
            this.password = null;
            this.encryptedPassword = password;
        } else {
            this.password = password;
            this.encryptedPassword = null;
        }
        this.active = active;
        this.key = key;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.attributes = new MultiValuedAttributeEntityList((List<MultiValuedAttributeEntity>)ImmutableList.of());
    }

    public UserEntity(String name, String firstName, String lastName, String displayName, String emailAddress, PasswordEntity password, boolean active) {
        this(name, firstName, lastName, displayName, emailAddress, password, active, null, null, null);
    }

    public UserEntity(String name, String firstName, String lastName, String displayName, String emailAddress, PasswordEntity password, boolean active, boolean isPasswordEncrypted) {
        this(name, firstName, lastName, displayName, emailAddress, password, active, null, null, null, isPasswordEncrypted);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public PasswordEntity getPassword() {
        return this.password;
    }

    public PasswordEntity getEncryptedPassword() {
        return this.encryptedPassword;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Date getCreatedDate() {
        return this.createdDate;
    }

    @Nullable
    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public void setAttributes(MultiValuedAttributeEntityList attributes) {
        this.attributes = attributes;
    }

    public MultiValuedAttributeEntityList getAttributes() {
        return this.attributes;
    }

    public Set<String> getValues(String key) {
        return this.attributes != null ? this.attributes.getValues(key) : null;
    }

    public String getValue(String key) {
        return this.attributes != null ? this.attributes.getValue(key) : null;
    }

    public Set<String> getKeys() {
        return this.attributes != null ? this.attributes.getKeys() : Collections.emptySet();
    }

    public boolean isEmpty() {
        return this.attributes == null || this.attributes.isEmpty();
    }

    @Deprecated
    public long getDirectoryId() {
        return 0L;
    }

    public int compareTo(User user) {
        return UserComparator.compareTo((User)this, (User)user);
    }

    public String getExternalId() {
        return this.key;
    }

    public boolean equals(Object o) {
        return UserComparator.equalsObject((User)this, (Object)o);
    }

    public int hashCode() {
        return UserComparator.hashCode((User)this);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.getName()).append("active", this.isActive()).append("emailAddress", (Object)this.getEmailAddress()).append("firstName", (Object)this.getFirstName()).append("lastName", (Object)this.getLastName()).append("displayName", (Object)this.getDisplayName()).append("externalId", (Object)this.getExternalId()).append("attributes", (Object)this.getAttributes()).append("createdDate", (Object)this.getCreatedDate()).append("updatedDate", (Object)this.getUpdatedDate()).toString();
    }

    public static UserEntity newMinimalInstance(String username) {
        return new UserEntity(username, null, null, null, null, null, false, null, null, null);
    }
}

