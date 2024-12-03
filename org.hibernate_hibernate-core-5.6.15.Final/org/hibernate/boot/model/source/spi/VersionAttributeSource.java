/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;

public interface VersionAttributeSource
extends SingularAttributeSource,
RelationalValueSourceContainer,
ImplicitBasicColumnNameSource {
    public String getUnsavedValue();
}

