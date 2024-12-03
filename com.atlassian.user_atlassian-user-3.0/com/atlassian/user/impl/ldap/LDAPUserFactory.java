/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.User;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.LDAPEntityFactory;
import java.util.Collection;
import java.util.Enumeration;
import javax.naming.directory.Attributes;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface LDAPUserFactory
extends LDAPEntityFactory<User> {
    public User getUser(Attributes var1, String var2) throws RepositoryException;

    public Collection<User> getUsers(Enumeration var1) throws RepositoryException;
}

