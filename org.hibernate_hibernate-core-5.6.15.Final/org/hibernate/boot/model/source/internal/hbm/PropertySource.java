/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.tuple.GenerationTiming;

interface PropertySource
extends ToolingHintContainer {
    public XmlElementMetadata getSourceType();

    public String getName();

    public String getXmlNodeName();

    public String getPropertyAccessorName();

    public String getCascadeStyleName();

    public GenerationTiming getGenerationTiming();

    public Boolean isInsertable();

    public Boolean isUpdatable();

    public boolean isUsedInOptimisticLocking();

    public boolean isLazy();
}

