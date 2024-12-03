/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import javax.naming.Name;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.SearchScope;

public interface LdapQuery {
    public Name base();

    public SearchScope searchScope();

    public Integer timeLimit();

    public Integer countLimit();

    public String[] attributes();

    public Filter filter();
}

