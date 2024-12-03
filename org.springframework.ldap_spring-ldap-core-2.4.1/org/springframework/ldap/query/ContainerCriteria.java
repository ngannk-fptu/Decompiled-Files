/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import org.springframework.ldap.query.ConditionCriteria;
import org.springframework.ldap.query.LdapQuery;

public interface ContainerCriteria
extends LdapQuery {
    public ConditionCriteria and(String var1);

    public ConditionCriteria or(String var1);

    public ContainerCriteria and(ContainerCriteria var1);

    public ContainerCriteria or(ContainerCriteria var1);
}

