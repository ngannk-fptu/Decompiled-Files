/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.InternalEntityTemplate
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.util.InternalEntityUtils
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.InternalDirectoryEntity;
import com.atlassian.crowd.model.InternalEntityTemplate;
import com.atlassian.crowd.model.permission.UserAdministrationGrantToGroup;
import com.atlassian.crowd.model.user.InternalUserAttribute;
import com.atlassian.crowd.model.user.InternalUserCredentialRecord;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.util.InternalEntityUtils;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalUser
extends InternalDirectoryEntity<InternalUserAttribute>
implements TimestampedUser {
    private String emailAddress;
    private String firstName;
    private String lastName;
    private String displayName;
    private PasswordCredential credential;
    private String externalId;
    private String lowerName;
    private String lowerEmailAddress;
    private String lowerFirstName;
    private String lowerLastName;
    private String lowerDisplayName;
    private List<InternalUserCredentialRecord> credentialRecords = new ArrayList<InternalUserCredentialRecord>();
    private Set<UserAdministrationGrantToGroup> groupGrants = new HashSet<UserAdministrationGrantToGroup>();

    protected InternalUser() {
    }

    public InternalUser(InternalEntityTemplate internalEntityTemplate, Directory directory, UserTemplate userTemplate, PasswordCredential credential) {
        super(internalEntityTemplate, directory);
        this.updateDetailsFrom((User)userTemplate);
        this.setCredential(credential);
    }

    public InternalUser(UserTemplateWithCredentialAndAttributes user, Directory directory) {
        this((User)user, directory, user.getCredential());
        if (user.getCreatedDate() != null) {
            this.createdDate = user.getCreatedDate();
        } else {
            this.setCreatedDateToNow();
        }
        if (user.getUpdatedDate() != null) {
            this.updatedDate = user.getUpdatedDate();
        } else {
            this.setUpdatedDateToNow();
        }
        for (PasswordCredential credential : user.getCredentialHistory()) {
            this.getCredentialRecords().add(new InternalUserCredentialRecord(this, credential.getCredential()));
        }
    }

    public InternalUser(User user, Directory directory, PasswordCredential credential) {
        Validate.notNull((Object)user, (String)"user argument cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)directory, (String)"directory argument cannot be null", (Object[])new Object[0]);
        InternalUser.validateCredential(credential);
        this.setName(user.getName());
        this.directory = directory;
        this.updateDetailsFrom(user);
        this.credential = credential;
        if (this.credential != null && this.credential != PasswordCredential.NONE) {
            InternalUserCredentialRecord record = new InternalUserCredentialRecord(this, credential.getCredential());
            this.credentialRecords.add(record);
        }
    }

    private static void validateCredential(PasswordCredential credential) {
        if (credential != null) {
            Validate.notNull((Object)credential.getCredential(), (String)"credential argument cannot have null value", (Object[])new Object[0]);
            Validate.isTrue((boolean)credential.isEncryptedCredential(), (String)"credential must be encrypted", (Object[])new Object[0]);
        }
    }

    private void validateUser(User user) {
        Validate.notNull((Object)user, (String)"user argument cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)user.getDirectoryId(), (String)"user argument cannot have a null directoryId", (Object[])new Object[0]);
        Validate.notNull((Object)user.getName(), (String)"user argument cannot have a null name", (Object[])new Object[0]);
        Validate.isTrue((user.getDirectoryId() == this.getDirectoryId() ? 1 : 0) != 0, (String)("directoryId of updated user (" + user.getDirectoryId() + ") does not match the directoryId of the existing user(" + this.getDirectoryId() + ")."), (Object[])new Object[0]);
        Validate.isTrue((boolean)IdentifierUtils.equalsInLowerCase((String)user.getName(), (String)this.getName()), (String)"username of updated user does not match the username of the existing user.", (Object[])new Object[0]);
    }

    public void updateDetailsFrom(User user) {
        this.validateUser(user);
        this.active = user.isActive();
        this.emailAddress = InternalEntityUtils.truncateValue((String)user.getEmailAddress());
        this.lowerEmailAddress = this.emailAddress == null ? null : this.emailAddress.toLowerCase(Locale.ENGLISH);
        this.firstName = InternalEntityUtils.truncateValue((String)user.getFirstName());
        this.lowerFirstName = this.firstName == null ? null : IdentifierUtils.toLowerCase((String)this.firstName);
        this.lastName = InternalEntityUtils.truncateValue((String)user.getLastName());
        this.lowerLastName = this.lastName == null ? null : IdentifierUtils.toLowerCase((String)this.lastName);
        this.displayName = InternalEntityUtils.truncateValue((String)user.getDisplayName());
        this.lowerDisplayName = this.displayName == null ? null : IdentifierUtils.toLowerCase((String)this.displayName);
        this.externalId = user.getExternalId();
    }

    public void renameTo(String newUsername) {
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)newUsername), (String)"newUsername cannot be null or blank", (Object[])new Object[0]);
        this.setName(newUsername);
    }

    public void updateCredentialTo(PasswordCredential newCredential, int maxCredentialHistory) {
        InternalUser.validateCredential(newCredential);
        this.credential = newCredential;
        if (maxCredentialHistory > 0) {
            if (this.getCredentialRecords().size() > maxCredentialHistory - 1) {
                Iterator<InternalUserCredentialRecord> iterator = this.getCredentialRecords().iterator();
                while (iterator.hasNext()) {
                    iterator.next();
                    if (this.getCredentialRecords().size() <= maxCredentialHistory - 1) continue;
                    iterator.remove();
                }
            }
            InternalUserCredentialRecord record = new InternalUserCredentialRecord(this, newCredential.getCredential());
            this.getCredentialRecords().add(record);
        }
    }

    public void setName(String name) {
        InternalEntityUtils.validateLength((String)name);
        this.name = name;
        this.lowerName = IdentifierUtils.toLowerCase((String)name);
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getLowerEmailAddress() {
        return this.lowerEmailAddress;
    }

    public String getLowerFirstName() {
        return this.lowerFirstName;
    }

    public String getLowerLastName() {
        return this.lowerLastName;
    }

    public String getLowerDisplayName() {
        return this.lowerDisplayName;
    }

    public String getLowerName() {
        return this.lowerName;
    }

    public PasswordCredential getCredential() {
        return this.credential;
    }

    public List<InternalUserCredentialRecord> getCredentialRecords() {
        return this.credentialRecords;
    }

    public List<PasswordCredential> getCredentialHistory() {
        ImmutableList.Builder credentials = ImmutableList.builder();
        for (InternalUserCredentialRecord record : this.getCredentialRecords()) {
            credentials.add((Object)record.getCredential());
        }
        return credentials.build();
    }

    private void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    private void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private void setCredential(PasswordCredential credential) {
        this.credential = credential;
    }

    private void setCredentialRecords(List<InternalUserCredentialRecord> credentialRecords) {
        this.credentialRecords = credentialRecords;
    }

    private Set<UserAdministrationGrantToGroup> getGroupGrants() {
        return this.groupGrants;
    }

    private void setGroupGrants(Set<UserAdministrationGrantToGroup> groupGrants) {
        this.groupGrants = groupGrants;
    }

    private void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    private void setLowerEmailAddress(String lowerEmailAddress) {
        this.lowerEmailAddress = lowerEmailAddress;
    }

    private void setLowerFirstName(String lowerFirstName) {
        this.lowerFirstName = lowerFirstName;
    }

    private void setLowerLastName(String lowerLastName) {
        this.lowerLastName = lowerLastName;
    }

    private void setLowerDisplayName(String lowerDisplayName) {
        this.lowerDisplayName = lowerDisplayName;
    }

    private void setLowerName(String lowerName) {
        this.lowerName = lowerName;
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

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).append("name", (Object)this.getName()).append("createdDate", (Object)this.getCreatedDate()).append("updatedDate", (Object)this.getUpdatedDate()).append("active", this.isActive()).append("emailAddress", (Object)this.getEmailAddress()).append("firstName", (Object)this.getFirstName()).append("lastName", (Object)this.getLastName()).append("displayName", (Object)this.getDisplayName()).append("credential", (Object)this.getCredential()).append("lowerName", (Object)this.getLowerName()).append("lowerEmailAddress", (Object)this.getLowerEmailAddress()).append("lowerFirstName", (Object)this.getLowerFirstName()).append("lowerLastName", (Object)this.getLowerLastName()).append("lowerDisplayName", (Object)this.getLowerDisplayName()).append("directoryId", this.getDirectoryId()).append("externalId", (Object)this.getExternalId()).toString();
    }
}

