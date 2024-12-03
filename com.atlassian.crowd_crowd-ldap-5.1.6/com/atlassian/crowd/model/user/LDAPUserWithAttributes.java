/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserComparator
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.user;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserComparator;
import com.atlassian.crowd.model.LDAPDirectoryEntity;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LDAPUserWithAttributes
implements UserWithAttributes,
LDAPDirectoryEntity {
    private final String dn;
    private final Long directoryId;
    private final String name;
    private final boolean active;
    private final String emailAddress;
    private final String firstName;
    private final String lastName;
    private final String displayName;
    private final String externalId;
    private final Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

    public LDAPUserWithAttributes(String dn, UserTemplateWithAttributes user) {
        Validate.isTrue((boolean)StringUtils.isNotBlank((CharSequence)dn));
        Validate.notNull((Object)user, (String)"user template cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)user.getDirectoryId(), (String)"directoryId cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)user.getName(), (String)"user name cannot be null", (Object[])new Object[0]);
        this.dn = dn;
        this.directoryId = user.getDirectoryId();
        this.name = user.getName();
        this.active = user.isActive();
        this.emailAddress = user.getEmailAddress();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.displayName = user.getDisplayName() == null ? "" : user.getDisplayName();
        this.externalId = user.getExternalId();
        for (Map.Entry entry : user.getAttributes().entrySet()) {
            this.attributes.put((String)entry.getKey(), new HashSet((Collection)entry.getValue()));
        }
    }

    @Override
    public String getDn() {
        return this.dn;
    }

    public long getDirectoryId() {
        return this.directoryId;
    }

    public String getName() {
        return this.name;
    }

    public boolean isActive() {
        return this.active;
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

    public String getDisplayName() {
        return this.displayName;
    }

    public Set<String> getValues(String name) {
        if (this.attributes.containsKey(name)) {
            return Collections.unmodifiableSet(this.attributes.get(name));
        }
        return Collections.emptySet();
    }

    public String getValue(String name) {
        Set<String> values = this.getValues(name);
        if (!values.isEmpty()) {
            return values.iterator().next();
        }
        return null;
    }

    public Set<String> getKeys() {
        return Collections.unmodifiableSet(this.attributes.keySet());
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public String getExternalId() {
        return this.externalId;
    }

    public boolean equals(Object o) {
        return UserComparator.equalsObject((User)this, (Object)o);
    }

    public int hashCode() {
        return UserComparator.hashCode((User)this);
    }

    public int compareTo(User other) {
        return UserComparator.compareTo((User)this, (User)other);
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("dn", (Object)this.dn).append("directoryId", (Object)this.directoryId).append("name", (Object)this.name).append("active", this.active).append("emailAddress", (Object)this.emailAddress).append("firstName", (Object)this.firstName).append("lastName", (Object)this.lastName).append("displayName", (Object)this.displayName).append("externalId", (Object)this.externalId).append("attributes", this.attributes).toString();
    }
}

