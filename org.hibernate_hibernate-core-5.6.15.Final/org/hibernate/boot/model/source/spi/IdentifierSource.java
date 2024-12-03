/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.source.spi.ToolingHintContextContainer;
import org.hibernate.id.EntityIdentifierNature;

public interface IdentifierSource
extends ToolingHintContextContainer {
    public EntityIdentifierNature getNature();

    public IdentifierGeneratorDefinition getIdentifierGeneratorDescriptor();
}

