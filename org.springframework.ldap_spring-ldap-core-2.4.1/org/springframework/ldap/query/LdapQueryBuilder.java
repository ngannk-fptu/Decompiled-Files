/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import java.text.MessageFormat;
import javax.naming.Name;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.ConditionCriteria;
import org.springframework.ldap.query.DefaultConditionCriteria;
import org.springframework.ldap.query.DefaultContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.ldap.support.LdapUtils;

public final class LdapQueryBuilder
implements LdapQuery {
    private Name base = LdapUtils.emptyLdapName();
    private SearchScope searchScope = null;
    private Integer countLimit = null;
    private Integer timeLimit = null;
    private String[] attributes = null;
    private DefaultContainerCriteria rootContainer = null;

    private LdapQueryBuilder() {
    }

    public static LdapQueryBuilder query() {
        return new LdapQueryBuilder();
    }

    public LdapQueryBuilder base(String baseDn) {
        this.assertFilterNotStarted();
        this.base = LdapUtils.newLdapName(baseDn);
        return this;
    }

    public LdapQueryBuilder base(Name baseDn) {
        this.assertFilterNotStarted();
        this.base = LdapUtils.newLdapName(baseDn);
        return this;
    }

    public LdapQueryBuilder searchScope(SearchScope searchScope) {
        this.assertFilterNotStarted();
        this.searchScope = searchScope;
        return this;
    }

    public LdapQueryBuilder countLimit(int countLimit) {
        this.assertFilterNotStarted();
        this.countLimit = countLimit;
        return this;
    }

    public LdapQueryBuilder attributes(String ... attributesToReturn) {
        this.assertFilterNotStarted();
        this.attributes = attributesToReturn;
        return this;
    }

    public LdapQueryBuilder timeLimit(int timeLimit) {
        this.assertFilterNotStarted();
        this.timeLimit = timeLimit;
        return this;
    }

    public ConditionCriteria where(String attribute) {
        this.initRootContainer();
        return new DefaultConditionCriteria(this.rootContainer, attribute);
    }

    private void initRootContainer() {
        this.assertFilterNotStarted();
        this.rootContainer = new DefaultContainerCriteria(this);
    }

    public LdapQuery filter(String hardcodedFilter) {
        this.initRootContainer();
        this.rootContainer.append(new HardcodedFilter(hardcodedFilter));
        return this;
    }

    public LdapQuery filter(Filter filter) {
        this.initRootContainer();
        this.rootContainer.append(filter);
        return this;
    }

    public LdapQuery filter(String filterFormat, Object ... params) {
        Object[] encodedParams = new String[params.length];
        for (int i = 0; i < params.length; ++i) {
            encodedParams[i] = LdapEncoder.filterEncode(params[i].toString());
        }
        return this.filter(MessageFormat.format(filterFormat, encodedParams));
    }

    private void assertFilterNotStarted() {
        if (this.rootContainer != null) {
            throw new IllegalStateException("Invalid operation - filter condition specification already started");
        }
    }

    @Override
    public Name base() {
        return this.base;
    }

    @Override
    public SearchScope searchScope() {
        return this.searchScope;
    }

    @Override
    public Integer countLimit() {
        return this.countLimit;
    }

    @Override
    public Integer timeLimit() {
        return this.timeLimit;
    }

    @Override
    public String[] attributes() {
        return this.attributes;
    }

    @Override
    public Filter filter() {
        if (this.rootContainer == null) {
            throw new IllegalStateException("No filter conditions have been specified specified");
        }
        return this.rootContainer.filter();
    }
}

