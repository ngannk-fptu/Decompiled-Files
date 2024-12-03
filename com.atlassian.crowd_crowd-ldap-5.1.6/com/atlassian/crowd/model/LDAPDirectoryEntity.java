/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.model.DirectoryEntity
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.crowd.model;

import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.model.DirectoryEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

public interface LDAPDirectoryEntity
extends DirectoryEntity,
Attributes,
Serializable {
    public String getDn();

    @SuppressFBWarnings(value={"LDAP_INJECTION"}, justification="No user input - the String was just retrieved from the LdapName")
    default public LdapName getLdapName() {
        try {
            return new LdapName(this.getDn());
        }
        catch (InvalidNameException e) {
            throw new RuntimeException("Unable to parse DN for entity", e);
        }
    }
}

