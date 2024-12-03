/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.EmbeddableType
 */
package org.hibernate.metamodel.model.domain;

import javax.persistence.metamodel.EmbeddableType;
import org.hibernate.metamodel.model.domain.SimpleDomainType;

public interface EmbeddedDomainType<J>
extends SimpleDomainType<J>,
EmbeddableType<J> {
}

