/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.ListAttribute
 */
package org.hibernate.metamodel.model.domain.spi;

import java.util.List;
import javax.persistence.metamodel.ListAttribute;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;

public interface ListPersistentAttribute<D, E>
extends ListAttribute<D, E>,
PluralPersistentAttribute<D, List<E>, E> {
}

