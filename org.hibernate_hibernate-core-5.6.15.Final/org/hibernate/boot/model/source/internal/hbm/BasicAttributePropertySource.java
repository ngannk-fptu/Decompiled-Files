/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintType;
import org.hibernate.boot.model.source.internal.hbm.PropertySource;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.tuple.GenerationTiming;

public class BasicAttributePropertySource
implements PropertySource {
    private final JaxbHbmBasicAttributeType basicAttributeMapping;

    public BasicAttributePropertySource(JaxbHbmBasicAttributeType basicAttributeMapping) {
        this.basicAttributeMapping = basicAttributeMapping;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.PROPERTY;
    }

    @Override
    public String getName() {
        return this.basicAttributeMapping.getName();
    }

    @Override
    public String getXmlNodeName() {
        return this.basicAttributeMapping.getNode();
    }

    @Override
    public String getPropertyAccessorName() {
        return this.basicAttributeMapping.getAccess();
    }

    @Override
    public String getCascadeStyleName() {
        return null;
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return this.basicAttributeMapping.getGenerated();
    }

    @Override
    public Boolean isInsertable() {
        return this.basicAttributeMapping.isInsert();
    }

    @Override
    public Boolean isUpdatable() {
        return this.basicAttributeMapping.isUpdate();
    }

    @Override
    public boolean isUsedInOptimisticLocking() {
        return this.basicAttributeMapping.isOptimisticLock();
    }

    @Override
    public boolean isLazy() {
        return this.basicAttributeMapping.isLazy();
    }

    @Override
    public List<JaxbHbmToolingHintType> getToolingHints() {
        return this.basicAttributeMapping.getToolingHints();
    }
}

