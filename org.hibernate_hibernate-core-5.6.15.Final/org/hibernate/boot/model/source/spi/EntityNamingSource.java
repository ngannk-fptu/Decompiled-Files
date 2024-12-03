/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.naming.EntityNaming;

public interface EntityNamingSource
extends EntityNaming {
    public String getTypeName();
}

