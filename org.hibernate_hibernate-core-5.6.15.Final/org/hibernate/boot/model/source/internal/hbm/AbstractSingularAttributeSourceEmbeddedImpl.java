/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.EmbeddableSourceContainer;
import org.hibernate.boot.model.source.internal.hbm.EmbeddableSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.EmbeddedAttributeMapping;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceEmbedded;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.tuple.GenerationTiming;

public abstract class AbstractSingularAttributeSourceEmbeddedImpl
extends AbstractHbmSourceNode
implements SingularAttributeSourceEmbedded {
    private final EmbeddedAttributeMapping jaxbEmbeddedAttributeMapping;
    private final EmbeddableSource embeddableSource;
    private NaturalIdMutability naturalIdMutability;

    protected AbstractSingularAttributeSourceEmbeddedImpl(final MappingDocument sourceMappingDocument, final AttributeSourceContainer container, final EmbeddedAttributeMapping embeddedAttributeMapping, List nestedAttributeMappings, boolean isDynamic, NaturalIdMutability naturalIdMutability, String logicalTableName) {
        this(sourceMappingDocument, embeddedAttributeMapping, new EmbeddableSourceImpl(sourceMappingDocument, new EmbeddableSourceContainer(){
            final AttributeRole role;
            final AttributePath path;
            final ToolingHintContext toolingHintContext;
            {
                this.role = container.getAttributeRoleBase().append(embeddedAttributeMapping.getName());
                this.path = container.getAttributePathBase().append(embeddedAttributeMapping.getName());
                this.toolingHintContext = Helper.collectToolingHints(sourceMappingDocument.getToolingHintContext(), embeddedAttributeMapping);
            }

            @Override
            public AttributeRole getAttributeRoleBase() {
                return this.role;
            }

            @Override
            public AttributePath getAttributePathBase() {
                return this.path;
            }

            @Override
            public ToolingHintContext getToolingHintContextBaselineForEmbeddable() {
                return this.toolingHintContext;
            }
        }, embeddedAttributeMapping.getEmbeddableMapping(), nestedAttributeMappings, isDynamic, embeddedAttributeMapping.isUnique(), logicalTableName, naturalIdMutability), naturalIdMutability);
    }

    public AbstractSingularAttributeSourceEmbeddedImpl(MappingDocument sourceMappingDocument, EmbeddedAttributeMapping jaxbEmbeddedAttributeMapping, EmbeddableSource embeddableSource, NaturalIdMutability naturalIdMutability) {
        super(sourceMappingDocument);
        this.jaxbEmbeddedAttributeMapping = jaxbEmbeddedAttributeMapping;
        this.embeddableSource = embeddableSource;
        this.naturalIdMutability = naturalIdMutability;
    }

    @Override
    public EmbeddableSource getEmbeddableSource() {
        return this.embeddableSource;
    }

    @Override
    public String getName() {
        return this.jaxbEmbeddedAttributeMapping.getName();
    }

    @Override
    public boolean isSingular() {
        return true;
    }

    @Override
    public boolean isVirtualAttribute() {
        return false;
    }

    @Override
    public SingularAttributeNature getSingularAttributeNature() {
        return SingularAttributeNature.COMPOSITE;
    }

    @Override
    public HibernateTypeSource getTypeInformation() {
        return null;
    }

    @Override
    public String getPropertyAccessorName() {
        return this.jaxbEmbeddedAttributeMapping.getAccess();
    }

    @Override
    public NaturalIdMutability getNaturalIdMutability() {
        return this.naturalIdMutability;
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return null;
    }
}

