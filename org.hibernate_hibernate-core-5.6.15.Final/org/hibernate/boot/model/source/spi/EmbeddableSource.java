/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;

public interface EmbeddableSource
extends AttributeSourceContainer {
    public JavaTypeDescriptor getTypeDescriptor();

    public String getParentReferenceAttributeName();

    public Map<EntityMode, String> getTuplizerClassMap();

    public boolean isDynamic();

    public boolean isUnique();
}

