/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.query;

import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.ContainerCriteria;

interface AppendableContainerCriteria
extends ContainerCriteria {
    public ContainerCriteria append(Filter var1);
}

