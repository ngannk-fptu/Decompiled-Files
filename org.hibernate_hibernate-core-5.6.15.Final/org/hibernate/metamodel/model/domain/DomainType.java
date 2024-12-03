/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Type
 */
package org.hibernate.metamodel.model.domain;

import javax.persistence.metamodel.Type;

public interface DomainType<J>
extends Type<J> {
    public String getTypeName();
}

