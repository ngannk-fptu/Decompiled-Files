/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.ImplicitNameSource;
import org.hibernate.boot.model.source.spi.AttributePath;

public interface ImplicitAnyDiscriminatorColumnNameSource
extends ImplicitNameSource {
    public AttributePath getAttributePath();
}

