/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.type.AnyType;
import org.hibernate.type.MetaType;
import org.hibernate.type.Type;

public class StandardAnyTypeDefinition
implements AnyMappingDefinition {
    private final AnyType anyType;
    private final boolean definedAsLazy;
    private final List<AnyMappingDefinition.DiscriminatorMapping> discriminatorMappings;

    public StandardAnyTypeDefinition(AnyType anyType, boolean definedAsLazy) {
        this.anyType = anyType;
        this.definedAsLazy = definedAsLazy;
        this.discriminatorMappings = StandardAnyTypeDefinition.interpretDiscriminatorMappings(anyType);
    }

    private static List<AnyMappingDefinition.DiscriminatorMapping> interpretDiscriminatorMappings(AnyType anyType) {
        Type discriminatorType = anyType.getDiscriminatorType();
        if (!MetaType.class.isInstance(discriminatorType)) {
            return Collections.emptyList();
        }
        MetaType metaType = (MetaType)discriminatorType;
        ArrayList<AnyMappingDefinition.DiscriminatorMapping> discriminatorMappings = new ArrayList<AnyMappingDefinition.DiscriminatorMapping>();
        for (final Map.Entry<Object, String> entry : metaType.getDiscriminatorValuesToEntityNameMap().entrySet()) {
            discriminatorMappings.add(new AnyMappingDefinition.DiscriminatorMapping(){
                private final Object discriminatorValue;
                private final String entityName;
                {
                    this.discriminatorValue = entry.getKey();
                    this.entityName = (String)entry.getValue();
                }

                @Override
                public Object getDiscriminatorValue() {
                    return this.discriminatorValue;
                }

                @Override
                public String getEntityName() {
                    return this.entityName;
                }
            });
        }
        return discriminatorMappings;
    }

    @Override
    public AnyType getType() {
        return this.anyType;
    }

    @Override
    public boolean isLazy() {
        return this.definedAsLazy;
    }

    @Override
    public Type getIdentifierType() {
        return this.anyType.getIdentifierType();
    }

    @Override
    public Type getDiscriminatorType() {
        return this.anyType.getDiscriminatorType();
    }

    @Override
    public Iterable<AnyMappingDefinition.DiscriminatorMapping> getMappingDefinedDiscriminatorMappings() {
        return this.discriminatorMappings;
    }
}

