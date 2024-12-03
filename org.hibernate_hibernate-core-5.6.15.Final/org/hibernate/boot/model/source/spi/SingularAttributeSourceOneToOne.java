/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.source.spi.DerivedValueSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceToOne;

public interface SingularAttributeSourceOneToOne
extends SingularAttributeSourceToOne {
    public List<DerivedValueSource> getFormulaSources();

    public boolean isConstrained();
}

