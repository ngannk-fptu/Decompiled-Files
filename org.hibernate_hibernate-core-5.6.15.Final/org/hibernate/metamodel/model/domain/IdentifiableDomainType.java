/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.IdentifiableType
 */
package org.hibernate.metamodel.model.domain;

import javax.persistence.metamodel.IdentifiableType;
import org.hibernate.metamodel.model.domain.ManagedDomainType;

public interface IdentifiableDomainType<J>
extends ManagedDomainType<J>,
IdentifiableType<J> {
}

