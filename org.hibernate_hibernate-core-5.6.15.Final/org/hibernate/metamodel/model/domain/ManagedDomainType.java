/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.ManagedType
 */
package org.hibernate.metamodel.model.domain;

import javax.persistence.metamodel.ManagedType;
import org.hibernate.metamodel.model.domain.SimpleDomainType;

public interface ManagedDomainType<J>
extends SimpleDomainType<J>,
ManagedType<J> {
}

