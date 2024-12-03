/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.spi.MetadataBuildingContext;

public interface NamingStrategyHelper {
    public Identifier determineImplicitName(MetadataBuildingContext var1);

    public Identifier handleExplicitName(String var1, MetadataBuildingContext var2);

    public Identifier toPhysicalName(Identifier var1, MetadataBuildingContext var2);
}

