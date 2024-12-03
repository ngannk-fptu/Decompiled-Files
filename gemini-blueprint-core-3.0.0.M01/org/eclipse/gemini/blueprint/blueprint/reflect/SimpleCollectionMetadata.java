/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.List;
import java.util.Set;
import org.osgi.service.blueprint.reflect.CollectionMetadata;
import org.osgi.service.blueprint.reflect.Metadata;
import org.springframework.util.StringUtils;

class SimpleCollectionMetadata
implements CollectionMetadata {
    private final List<Metadata> values;
    private final CollectionType collectionType;
    private final String typeName;

    public SimpleCollectionMetadata(List<Metadata> values, CollectionType type, String valueTypeName) {
        this.values = values;
        this.collectionType = type;
        this.typeName = StringUtils.hasText((String)valueTypeName) ? valueTypeName : null;
    }

    public SimpleCollectionMetadata(List<Metadata> values, Class<?> type, String valueTypeName) {
        this(values, CollectionType.resolve(type), valueTypeName);
    }

    @Override
    public Class<?> getCollectionClass() {
        return this.collectionType.type;
    }

    @Override
    public String getValueType() {
        return this.typeName;
    }

    @Override
    public List<Metadata> getValues() {
        return this.values;
    }

    public static enum CollectionType {
        ARRAY(Object[].class),
        LIST(List.class),
        SET(Set.class);

        private final Class<?> type;

        private CollectionType(Class<?> type) {
            this.type = type;
        }

        static CollectionType resolve(Class<?> type) {
            for (CollectionType supportedType : CollectionType.values()) {
                if (!supportedType.type.equals(type)) continue;
                return supportedType;
            }
            throw new IllegalArgumentException("Unsupported class type " + type);
        }
    }
}

