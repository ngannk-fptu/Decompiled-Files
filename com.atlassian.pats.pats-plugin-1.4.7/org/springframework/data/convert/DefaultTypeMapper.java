/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.springframework.data.convert.MappingContextTypeInformationMapper;
import org.springframework.data.convert.SimpleTypeInformationMapper;
import org.springframework.data.convert.TypeAliasAccessor;
import org.springframework.data.convert.TypeInformationMapper;
import org.springframework.data.convert.TypeMapper;
import org.springframework.data.mapping.Alias;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class DefaultTypeMapper<S>
implements TypeMapper<S> {
    private final TypeAliasAccessor<S> accessor;
    private final List<? extends TypeInformationMapper> mappers;
    private final Map<Alias, Optional<TypeInformation<?>>> typeCache;
    private final Function<Alias, Optional<TypeInformation<?>>> getAlias;

    public DefaultTypeMapper(TypeAliasAccessor<S> accessor) {
        this(accessor, Collections.singletonList(new SimpleTypeInformationMapper()));
    }

    public DefaultTypeMapper(TypeAliasAccessor<S> accessor, List<? extends TypeInformationMapper> mappers) {
        this(accessor, null, mappers);
    }

    public DefaultTypeMapper(TypeAliasAccessor<S> accessor, @Nullable MappingContext<? extends PersistentEntity<?, ?>, ?> mappingContext, List<? extends TypeInformationMapper> additionalMappers) {
        Assert.notNull(accessor, (String)"Accessor must not be null!");
        Assert.notNull(additionalMappers, (String)"AdditionalMappers must not be null!");
        ArrayList<? extends TypeInformationMapper> mappers = new ArrayList<TypeInformationMapper>(additionalMappers.size() + 1);
        if (mappingContext != null) {
            mappers.add(new MappingContextTypeInformationMapper(mappingContext));
        }
        mappers.addAll(additionalMappers);
        this.mappers = Collections.unmodifiableList(mappers);
        this.accessor = accessor;
        this.typeCache = new ConcurrentHashMap();
        this.getAlias = key -> {
            for (TypeInformationMapper mapper : mappers) {
                TypeInformation<?> typeInformation = mapper.resolveTypeFrom((Alias)key);
                if (typeInformation == null) continue;
                return Optional.of(typeInformation);
            }
            return Optional.empty();
        };
    }

    @Override
    @Nullable
    public TypeInformation<?> readType(S source) {
        Assert.notNull(source, (String)"Source object must not be null!");
        return this.getFromCacheOrCreate(this.accessor.readAliasFrom(source));
    }

    @Nullable
    private TypeInformation<?> getFromCacheOrCreate(Alias alias) {
        Optional<TypeInformation<?>> typeInformation = this.typeCache.get(alias);
        if (typeInformation == null) {
            typeInformation = this.typeCache.computeIfAbsent(alias, this.getAlias);
        }
        return typeInformation.orElse(null);
    }

    @Override
    public <T> TypeInformation<? extends T> readType(S source, TypeInformation<T> basicType) {
        boolean isMoreConcreteCustomType;
        Assert.notNull(source, (String)"Source must not be null!");
        Assert.notNull(basicType, (String)"Basic type must not be null!");
        Class<?> documentsTargetType = this.getDefaultedTypeToBeUsed(source);
        if (documentsTargetType == null) {
            return basicType;
        }
        Class<T> rawType = basicType.getType();
        boolean bl = isMoreConcreteCustomType = rawType == null || rawType.isAssignableFrom(documentsTargetType) && !rawType.equals(documentsTargetType);
        if (!isMoreConcreteCustomType) {
            return basicType;
        }
        ClassTypeInformation<?> targetType = ClassTypeInformation.from(documentsTargetType);
        return basicType.specialize(targetType);
    }

    @Nullable
    private Class<?> getDefaultedTypeToBeUsed(S source) {
        TypeInformation<?> documentsTargetTypeInformation = this.readType(source);
        documentsTargetTypeInformation = documentsTargetTypeInformation == null ? this.getFallbackTypeFor(source) : documentsTargetTypeInformation;
        return documentsTargetTypeInformation == null ? null : documentsTargetTypeInformation.getType();
    }

    @Nullable
    protected TypeInformation<?> getFallbackTypeFor(S source) {
        return null;
    }

    @Override
    public void writeType(Class<?> type, S dbObject) {
        this.writeType(ClassTypeInformation.from(type), dbObject);
    }

    @Override
    public void writeType(TypeInformation<?> info, S sink) {
        Assert.notNull(info, (String)"TypeInformation must not be null!");
        Alias alias = this.getAliasFor(info);
        if (alias.isPresent()) {
            this.accessor.writeTypeTo(sink, alias.getValue());
        }
    }

    protected final Alias getAliasFor(TypeInformation<?> info) {
        Assert.notNull(info, (String)"TypeInformation must not be null!");
        for (TypeInformationMapper typeInformationMapper : this.mappers) {
            Alias alias = typeInformationMapper.createAliasFor(info);
            if (!alias.isPresent()) continue;
            return alias;
        }
        return Alias.NONE;
    }
}

