/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.ToolingHintContext;

public interface EmbeddableSourceContainer {
    public AttributeRole getAttributeRoleBase();

    public AttributePath getAttributePathBase();

    public ToolingHintContext getToolingHintContextBaselineForEmbeddable();
}

