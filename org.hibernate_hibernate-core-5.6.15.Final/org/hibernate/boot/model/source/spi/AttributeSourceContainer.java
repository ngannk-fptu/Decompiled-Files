/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.List;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.ToolingHintContextContainer;

public interface AttributeSourceContainer
extends ToolingHintContextContainer {
    public AttributePath getAttributePathBase();

    public AttributeRole getAttributeRoleBase();

    public List<AttributeSource> attributeSources();

    public LocalMetadataBuildingContext getLocalMetadataBuildingContext();
}

