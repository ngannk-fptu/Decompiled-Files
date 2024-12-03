/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.source.spi.EmbeddableSourceContributor;
import org.hibernate.boot.model.source.spi.IdentifierSource;

public interface CompositeIdentifierSource
extends IdentifierSource,
EmbeddableSourceContributor {
    public IdentifierGeneratorDefinition getIndividualAttributeIdGenerator(String var1);
}

