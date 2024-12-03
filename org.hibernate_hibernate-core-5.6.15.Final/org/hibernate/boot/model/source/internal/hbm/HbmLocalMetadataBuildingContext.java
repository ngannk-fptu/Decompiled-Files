/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.EntityInfo;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.mapping.PersistentClass;

public interface HbmLocalMetadataBuildingContext
extends LocalMetadataBuildingContext {
    public ToolingHintContext getToolingHintContext();

    public String determineEntityName(EntityInfo var1);

    public String determineEntityName(String var1, String var2);

    public String qualifyClassName(String var1);

    public PersistentClass findEntityBinding(String var1, String var2);
}

