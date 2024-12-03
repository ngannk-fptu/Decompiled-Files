/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.ImplicitNameSource;
import org.hibernate.boot.model.source.spi.AttributePath;

public interface ImplicitIdentifierColumnNameSource
extends ImplicitNameSource {
    public EntityNaming getEntityNaming();

    public AttributePath getIdentifierAttributePath();
}

