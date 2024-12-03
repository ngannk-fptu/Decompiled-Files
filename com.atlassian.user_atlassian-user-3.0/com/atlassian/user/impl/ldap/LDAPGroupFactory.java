/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.LDAPEntityFactory;
import javax.naming.directory.Attributes;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface LDAPGroupFactory
extends LDAPEntityFactory<Group> {
    public Group getGroup(Attributes var1, String var2) throws RepositoryException, EntityException;

    public Group getGroup(String var1);
}

