/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.IdentifierSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;

public interface IdentifierSourceSimple
extends IdentifierSource {
    public SingularAttributeSource getIdentifierAttributeSource();

    public String getUnsavedValue();
}

