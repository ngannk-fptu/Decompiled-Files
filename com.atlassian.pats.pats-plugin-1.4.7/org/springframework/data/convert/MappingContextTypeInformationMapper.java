/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.convert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.mapping.Alias;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class MappingContextTypeInformationMapper
implements TypeInformationMapper {
    private final Map<ClassTypeInformation<?>, Alias> typeMap;
    private final MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext;

    public MappingContextTypeInformationMapper(MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext) {
        Assert.notNull(mappingContext, (String)"MappingContext must not be null!");
        this.typeMap = new ConcurrentHashMap();
        this.mappingContext = mappingContext;
        for (PersistentEntity<?, ?> entity : mappingContext.getPersistentEntities()) {
            this.verify(entity.getTypeInformation().getRawTypeInformation(), entity.getTypeAlias());
        }
    }

    @Override
    public Alias createAliasFor(TypeInformation<?> type) {
        return this.typeMap.computeIfAbsent(type.getRawTypeInformation(), key -> {
            PersistentEntity<?, ?> entity = this.mappingContext.getPersistentEntity(key);
            if (entity == null || entity.getTypeAlias() == null) {
                return Alias.NONE;
            }
            return this.verify((ClassTypeInformation<?>)key, entity.getTypeAlias());
        });
    }

    private Alias verify(ClassTypeInformation<?> key, Alias alias) {
        Alias existingAlias = this.typeMap.getOrDefault(key, Alias.NONE);
        if (existingAlias.isPresentButDifferent(alias)) {
            throw new IllegalArgumentException(String.format("Trying to register alias '%s', but found already registered alias '%s' for type %s!", alias, existingAlias, key));
        }
        if (this.typeMap.containsValue(alias)) {
            this.typeMap.entrySet().stream().filter(it -> ((Alias)it.getValue()).hasSamePresentValueAs(alias) && !((ClassTypeInformation)it.getKey()).equals(key)).findFirst().ifPresent(it -> {
                throw new IllegalArgumentException(String.format("Detected existing type mapping of %s to alias '%s' but attempted to bind the same alias to %s!", key, alias, it.getKey()));
            });
        }
        return alias;
    }

    @Override
    @Nullable
    public TypeInformation<?> resolveTypeFrom(Alias alias) {
        for (Map.Entry<ClassTypeInformation<?>, Alias> entry : this.typeMap.entrySet()) {
            if (!entry.getValue().hasSamePresentValueAs(alias)) continue;
            return entry.getKey();
        }
        for (PersistentEntity persistentEntity : this.mappingContext.getPersistentEntities()) {
            if (!persistentEntity.getTypeAlias().hasSamePresentValueAs(alias)) continue;
            return persistentEntity.getTypeInformation().getRawTypeInformation();
        }
        return null;
    }
}

