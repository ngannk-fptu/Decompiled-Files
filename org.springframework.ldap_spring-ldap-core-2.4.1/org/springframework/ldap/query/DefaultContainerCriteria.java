/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.naming.Name;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.AppendableContainerCriteria;
import org.springframework.ldap.query.ConditionCriteria;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.CriteriaContainerType;
import org.springframework.ldap.query.DefaultConditionCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.SearchScope;

class DefaultContainerCriteria
implements AppendableContainerCriteria {
    private final Set<Filter> filters = new LinkedHashSet<Filter>();
    private final LdapQuery topQuery;
    private CriteriaContainerType type;

    DefaultContainerCriteria(LdapQuery topQuery) {
        this.topQuery = topQuery;
    }

    @Override
    public DefaultContainerCriteria append(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    DefaultContainerCriteria withType(CriteriaContainerType newType) {
        this.type = newType;
        return this;
    }

    @Override
    public ConditionCriteria and(String attribute) {
        CriteriaContainerType.AND.validateSameType(this.type);
        this.type = CriteriaContainerType.AND;
        return new DefaultConditionCriteria(this, attribute);
    }

    @Override
    public ConditionCriteria or(String attribute) {
        CriteriaContainerType.OR.validateSameType(this.type);
        this.type = CriteriaContainerType.OR;
        return new DefaultConditionCriteria(this, attribute);
    }

    @Override
    public ContainerCriteria and(ContainerCriteria nested) {
        if (this.type == CriteriaContainerType.OR) {
            return new DefaultContainerCriteria(this.topQuery).withType(CriteriaContainerType.AND).append(this.filter()).append(nested.filter());
        }
        this.type = CriteriaContainerType.AND;
        this.filters.add(nested.filter());
        return this;
    }

    @Override
    public ContainerCriteria or(ContainerCriteria nested) {
        if (this.type == CriteriaContainerType.AND) {
            return new DefaultContainerCriteria(this.topQuery).withType(CriteriaContainerType.OR).append(this.filter()).append(nested.filter());
        }
        this.type = CriteriaContainerType.OR;
        this.filters.add(nested.filter());
        return this;
    }

    @Override
    public Filter filter() {
        if (this.filters.size() == 1) {
            return this.filters.iterator().next();
        }
        return this.type.constructFilter().appendAll(this.filters);
    }

    @Override
    public Name base() {
        return this.topQuery.base();
    }

    @Override
    public SearchScope searchScope() {
        return this.topQuery.searchScope();
    }

    @Override
    public Integer timeLimit() {
        return this.topQuery.timeLimit();
    }

    @Override
    public Integer countLimit() {
        return this.topQuery.countLimit();
    }

    @Override
    public String[] attributes() {
        return this.topQuery.attributes();
    }
}

