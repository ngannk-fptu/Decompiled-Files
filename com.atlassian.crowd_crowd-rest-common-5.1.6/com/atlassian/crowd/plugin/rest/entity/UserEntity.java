/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.Expander
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlTransient
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.crowd.plugin.rest.entity.NamedEntity;
import com.atlassian.crowd.plugin.rest.entity.PasswordEntity;
import com.atlassian.crowd.plugin.rest.entity.UserEntityExpander;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.Expander;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name="user")
@XmlAccessorType(value=XmlAccessType.FIELD)
@Expander(value=UserEntityExpander.class)
public class UserEntity
implements NamedEntity {
    public static final String ATTRIBUTES_FIELD_NAME = "attributes";
    @XmlAttribute
    private String expand;
    @XmlElement(name="link")
    private Link link;
    @XmlAttribute(name="name")
    private String name;
    @XmlElement(name="first-name")
    private String firstName;
    @XmlElement(name="last-name")
    private String lastName;
    @XmlElement(name="display-name")
    private String displayName;
    @XmlElement(name="directory-id")
    private Long directoryId;
    @XmlElement(name="email")
    private String emailAddress;
    @XmlElement(name="password")
    private PasswordEntity password;
    @XmlElement(name="encrypted-password")
    private PasswordEntity encryptedPassword;
    @XmlElement(name="key")
    private final String key;
    @XmlElement(name="created-date")
    private Date createdDate;
    @XmlElement(name="updated-date")
    private Date updatedDate;
    @XmlTransient
    private String applicationName;
    @XmlElement(name="directory-name")
    private String directoryName;
    @XmlElement(name="active")
    private Boolean active;
    @Expandable
    @XmlElement(name="attributes")
    private MultiValuedAttributeEntityList attributes;

    private UserEntity() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public UserEntity(String name, String firstName, String lastName, String displayName, String emailAddress, PasswordEntity password, Boolean active, Link link, String key, Long directoryId, String directoryName, Date createdDate, Date updatedDate) {
        this(name, firstName, lastName, displayName, emailAddress, password, active, link, key, directoryId, directoryName, createdDate, updatedDate, false);
    }

    public UserEntity(String name, String firstName, String lastName, String displayName, String emailAddress, PasswordEntity password, Boolean active, Link link, String key, Long directoryId, String directoryName, Date createdDate, Date updatedDate, boolean isPasswordEncrypted) {
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.displayName = displayName;
        this.emailAddress = emailAddress;
        if (isPasswordEncrypted) {
            this.encryptedPassword = password;
            this.password = null;
        } else {
            this.password = password;
            this.encryptedPassword = null;
        }
        this.active = active;
        this.link = link;
        this.key = key;
        this.directoryId = directoryId;
        this.directoryName = directoryName;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
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

    public void setEmail(String email) {
        this.emailAddress = email;
    }

    public String getEmail() {
        return this.emailAddress;
    }

    public void setPassword(PasswordEntity password) {
        this.password = password;
    }

    public PasswordEntity getPassword() {
        return this.password;
    }

    public void setEncryptedPassword(PasswordEntity encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public PasswordEntity getEncryptedPassword() {
        return this.encryptedPassword;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Boolean isActive() {
        return this.active;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    public void setAttributes(MultiValuedAttributeEntityList attributes) {
        this.attributes = attributes;
    }

    public MultiValuedAttributeEntityList getAttributes() {
        return this.attributes;
    }

    public String getKey() {
        return this.key;
    }

    String getApplicationName() {
        return this.applicationName;
    }

    public String getDirectoryName() {
        return this.directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static UserEntity newMinimalUserEntity(String name, String applicationName, Link link) {
        UserEntity user = new UserEntity(name, null, null, null, null, null, null, link, null, null, null, null, null);
        user.applicationName = applicationName;
        return user;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.getName()).append("active", (Object)this.isActive()).append("emailAddress", (Object)this.getEmail()).append("firstName", (Object)this.getFirstName()).append("lastName", (Object)this.getLastName()).append("displayName", (Object)this.getDisplayName()).append("key", (Object)this.getKey()).append("directoryId", (Object)this.getDirectoryId()).append("directoryName", (Object)this.getDirectoryName()).append("createdDate", (Object)this.getCreatedDate()).append("updatedDate", (Object)this.getUpdatedDate()).toString();
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return this.link;
    }

    public boolean isExpanded() {
        return this.applicationName == null;
    }
}

