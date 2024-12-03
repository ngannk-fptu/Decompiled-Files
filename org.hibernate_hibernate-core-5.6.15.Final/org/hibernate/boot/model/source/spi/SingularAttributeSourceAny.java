/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.AnyMappingSource;
import org.hibernate.boot.model.source.spi.CascadeStyleSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;

public interface SingularAttributeSourceAny
extends SingularAttributeSource,
AnyMappingSource,
CascadeStyleSource {
}

