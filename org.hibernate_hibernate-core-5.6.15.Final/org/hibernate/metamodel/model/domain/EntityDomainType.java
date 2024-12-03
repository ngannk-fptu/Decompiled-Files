/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.EntityType
 */
package org.hibernate.metamodel.model.domain;

import javax.persistence.metamodel.EntityType;
import org.hibernate.metamodel.model.domain.IdentifiableDomainType;

public interface EntityDomainType<J>
extends IdentifiableDomainType<J>,
EntityType<J> {
}

