/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.jaxb.hbm.spi.SingularAttributeInfo;
import org.hibernate.boot.model.source.spi.EmbeddableMapping;

public interface EmbeddedAttributeMapping
extends SingularAttributeInfo {
    public boolean isUnique();

    public EmbeddableMapping getEmbeddableMapping();
}

