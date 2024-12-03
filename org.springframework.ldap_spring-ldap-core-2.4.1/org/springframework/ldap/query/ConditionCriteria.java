/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import org.springframework.ldap.query.ContainerCriteria;

public interface ConditionCriteria {
    public ContainerCriteria is(String var1);

    public ContainerCriteria gte(String var1);

    public ContainerCriteria lte(String var1);

    public ContainerCriteria like(String var1);

    public ContainerCriteria whitespaceWildcardsLike(String var1);

    public ContainerCriteria isPresent();

    public ConditionCriteria not();
}

