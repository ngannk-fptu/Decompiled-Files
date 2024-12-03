/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.RootEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.SingularIdentifierAttributeSourceImpl;
import org.hibernate.boot.model.source.spi.IdentifierSourceSimple;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.id.EntityIdentifierNature;

class IdentifierSourceSimpleImpl
implements IdentifierSourceSimple {
    private final SingularIdentifierAttributeSourceImpl attribute;
    private final IdentifierGeneratorDefinition generatorDefinition;
    private final String unsavedValue;
    private final ToolingHintContext toolingHintContext;

    public IdentifierSourceSimpleImpl(RootEntitySourceImpl rootEntitySource) {
        JaxbHbmRootEntityType jaxbEntityMapping = rootEntitySource.jaxbEntityMapping();
        this.attribute = new SingularIdentifierAttributeSourceImpl(rootEntitySource.sourceMappingDocument(), rootEntitySource, jaxbEntityMapping.getId());
        this.generatorDefinition = EntityHierarchySourceImpl.interpretGeneratorDefinition(rootEntitySource.sourceMappingDocument(), rootEntitySource.getEntityNamingSource(), rootEntitySource.jaxbEntityMapping().getId().getGenerator());
        this.unsavedValue = jaxbEntityMapping.getId().getUnsavedValue();
        this.toolingHintContext = Helper.collectToolingHints(rootEntitySource.getToolingHintContext(), jaxbEntityMapping.getId());
    }

    @Override
    public SingularAttributeSource getIdentifierAttributeSource() {
        return this.attribute;
    }

    @Override
    public IdentifierGeneratorDefinition getIdentifierGeneratorDescriptor() {
        return this.generatorDefinition;
    }

    @Override
    public EntityIdentifierNature getNature() {
        return EntityIdentifierNature.SIMPLE;
    }

    @Override
    public String getUnsavedValue() {
        return this.unsavedValue;
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }
}

