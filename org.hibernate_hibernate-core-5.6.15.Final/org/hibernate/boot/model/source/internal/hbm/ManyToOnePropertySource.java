/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintType;
import org.hibernate.boot.model.source.internal.hbm.PropertySource;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.tuple.GenerationTiming;

public class ManyToOnePropertySource
implements PropertySource {
    private final JaxbHbmManyToOneType manyToOneMapping;

    public ManyToOnePropertySource(JaxbHbmManyToOneType manyToOneMapping) {
        this.manyToOneMapping = manyToOneMapping;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.MANY_TO_ONE;
    }

    @Override
    public String getName() {
        return this.manyToOneMapping.getName();
    }

    @Override
    public String getXmlNodeName() {
        return this.manyToOneMapping.getNode();
    }

    @Override
    public String getPropertyAccessorName() {
        return this.manyToOneMapping.getAccess();
    }

    @Override
    public String getCascadeStyleName() {
        return this.manyToOneMapping.getCascade();
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return null;
    }

    @Override
    public Boolean isInsertable() {
        return this.manyToOneMapping.isInsert();
    }

    @Override
    public Boolean isUpdatable() {
        return this.manyToOneMapping.isUpdate();
    }

    @Override
    public boolean isUsedInOptimisticLocking() {
        return this.manyToOneMapping.isOptimisticLock();
    }

    @Override
    public boolean isLazy() {
        return false;
    }

    @Override
    public List<JaxbHbmToolingHintType> getToolingHints() {
        return this.manyToOneMapping.getToolingHints();
    }
}

