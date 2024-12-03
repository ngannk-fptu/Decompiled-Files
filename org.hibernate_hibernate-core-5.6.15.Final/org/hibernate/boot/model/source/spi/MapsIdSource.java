/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.SingularAttributeSourceToOne;

public interface MapsIdSource {
    public String getMappedIdAttributeName();

    public SingularAttributeSourceToOne getAssociationAttributeSource();
}

