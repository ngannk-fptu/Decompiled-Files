/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.SetAttribute
 */
package org.hibernate.metamodel.model.domain.spi;

import java.util.Set;
import javax.persistence.metamodel.SetAttribute;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;

public interface SetPersistentAttribute<D, E>
extends SetAttribute<D, E>,
PluralPersistentAttribute<D, Set<E>, E> {
}

