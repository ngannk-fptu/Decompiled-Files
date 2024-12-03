/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.User;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.ldap.DefaultLDAPUser;
import com.atlassian.user.impl.ldap.LDAPUserFactory;
import com.atlassian.user.impl.ldap.properties.LdapSearchProperties;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DefaultLDAPUserFactory
implements LDAPUserFactory {
    private final LdapSearchProperties searchProperties;

    public DefaultLDAPUserFactory(LdapSearchProperties searchProperties) {
        this.searchProperties = searchProperties;
    }

    @Override
    public DefaultLDAPUser getUser(Attributes attrs, String distinguishedName) throws RepositoryException {
        DefaultLDAPUser user;
        if (distinguishedName.indexOf(this.searchProperties.getBaseUserNamespace()) == -1) {
            distinguishedName = distinguishedName + "," + this.searchProperties.getBaseUserNamespace();
        }
        try {
            Attribute emailAttr;
            Attribute uidAttr = attrs.get(this.searchProperties.getUsernameAttribute());
            if (uidAttr == null) {
                return null;
            }
            user = new DefaultLDAPUser((String)uidAttr.get(), distinguishedName);
            if (UtilTimerStack.isActive()) {
                String stackKey = this.getClass().getName() + "_getUser(" + user.getName() + ")";
                UtilTimerStack.push((String)stackKey);
            }
            Attribute givenNameAttr = attrs.get(this.searchProperties.getFirstnameAttribute());
            Attribute surNameAttr = attrs.get(this.searchProperties.getSurnameAttribute());
            if (givenNameAttr != null) {
                String givenName = (String)givenNameAttr.get();
                String surName = "";
                if (surNameAttr != null) {
                    surName = (String)surNameAttr.get();
                }
                givenName = givenName.concat(" ");
                user.setFullName(givenName.concat(surName));
            }
            if ((emailAttr = attrs.get(this.searchProperties.getEmailAttribute())) != null) {
                user.setEmail((String)emailAttr.get());
            }
        }
        catch (NamingException e) {
            throw new RepositoryException(e);
        }
        if (UtilTimerStack.isActive()) {
            String stackKey = this.getClass().getName() + "_getUser(" + user.getName() + ")";
            UtilTimerStack.pop((String)stackKey);
        }
        return user;
    }

    @Override
    public Collection<User> getUsers(Enumeration userNamingEnumeration) throws RepositoryException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_getUserCollection"));
        }
        ArrayList<User> users = new ArrayList<User>();
        while (userNamingEnumeration.hasMoreElements()) {
            SearchResult result = (SearchResult)userNamingEnumeration.nextElement();
            Attributes attrs = result.getAttributes();
            users.add(this.getUser(attrs, result.getName()));
        }
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.pop((String)(this.getClass().getName() + "_getUserCollection"));
        }
        return users;
    }

    @Override
    public DefaultLDAPUser getEntity(Attributes attrs, String distinguishedName) throws RepositoryException {
        if (distinguishedName.startsWith("\"") && distinguishedName.endsWith("\"")) {
            distinguishedName = distinguishedName.substring(1, distinguishedName.length() - 1);
        }
        return this.getUser(attrs, distinguishedName);
    }
}

