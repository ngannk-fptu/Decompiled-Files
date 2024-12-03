/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class PersistentEntities
implements Streamable<PersistentEntity<?, ? extends PersistentProperty<?>>> {
    private final Collection<? extends MappingContext<?, ? extends PersistentProperty<?>>> contexts;

    public PersistentEntities(Iterable<? extends MappingContext<?, ?>> contexts) {
        Assert.notNull(contexts, (String)"MappingContexts must not be null!");
        this.contexts = contexts instanceof Collection ? (Collection)contexts : (Collection)StreamSupport.stream(contexts.spliterator(), false).collect(Collectors.toList());
    }

    public static PersistentEntities of(MappingContext<?, ?> ... contexts) {
        Assert.notNull(contexts, (String)"MappingContexts must not be null!");
        return new PersistentEntities(Arrays.asList(contexts));
    }

    public Optional<PersistentEntity<?, ? extends PersistentProperty<?>>> getPersistentEntity(Class<?> type) {
        for (MappingContext<?, PersistentProperty<?>> context : this.contexts) {
            if (!context.hasPersistentEntityFor(type)) continue;
            return Optional.of(context.getRequiredPersistentEntity((PersistentProperty<?>)((Object)type)));
        }
        return Optional.empty();
    }

    public PersistentEntity<?, ? extends PersistentProperty<?>> getRequiredPersistentEntity(Class<?> type) {
        Assert.notNull(type, (String)"Domain type must not be null!");
        if (this.contexts.size() == 1) {
            return this.contexts.iterator().next().getRequiredPersistentEntity((PersistentProperty<?>)((Object)type));
        }
        return this.getPersistentEntity(type).orElseThrow(() -> new MappingException(String.format("Cannot get or create PersistentEntity for type %s! PersistentEntities knows about %s MappingContext instances and therefore cannot identify a single responsible one. Please configure the initialEntitySet through an entity scan using the base package in your configuration to pre initialize contexts.", type.getName(), this.contexts.size())));
    }

    public <T> Optional<T> mapOnContext(Class<?> type, BiFunction<MappingContext<?, ? extends PersistentProperty<?>>, PersistentEntity<?, ?>, T> combiner) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.notNull(combiner, (String)"Combining BiFunction must not be null!");
        if (this.contexts.size() == 1) {
            return this.contexts.stream().filter(it -> it.getPersistentEntity(type) != null).map(it -> combiner.apply((MappingContext<?, ? extends PersistentProperty<?>>)it, (PersistentEntity<?, ?>)it.getRequiredPersistentEntity(type))).findFirst();
        }
        return this.contexts.stream().filter(it -> it.hasPersistentEntityFor(type)).map(it -> combiner.apply((MappingContext<?, ? extends PersistentProperty<?>>)it, (PersistentEntity<?, ?>)it.getRequiredPersistentEntity(type))).findFirst();
    }

    public Streamable<TypeInformation<?>> getManagedTypes() {
        HashSet target = new HashSet();
        for (MappingContext<?, PersistentProperty<?>> context : this.contexts) {
            target.addAll(context.getManagedTypes());
        }
        return Streamable.of(target);
    }

    @Override
    public Iterator<PersistentEntity<?, ? extends PersistentProperty<?>>> iterator() {
        ArrayList target = new ArrayList();
        for (MappingContext<?, PersistentProperty<?>> context : this.contexts) {
            target.addAll(context.getPersistentEntities());
        }
        return target.iterator();
    }

    @Nullable
    public PersistentEntity<?, ?> getEntityUltimatelyReferredToBy(PersistentProperty<?> property) {
        TypeInformation<?> propertyType = property.getTypeInformation().getActualType();
        if (propertyType == null || !property.isAssociation()) {
            return null;
        }
        Class<?> associationTargetType = property.getAssociationTargetType();
        return associationTargetType == null ? this.getEntityIdentifiedBy(propertyType) : this.getPersistentEntity(associationTargetType).orElseGet(() -> this.getEntityIdentifiedBy(propertyType));
    }

    public TypeInformation<?> getTypeUltimatelyReferredToBy(PersistentProperty<?> property) {
        Assert.notNull(property, (String)"PersistentProperty must not be null!");
        PersistentEntity<?, ?> entity = this.getEntityUltimatelyReferredToBy(property);
        return entity == null ? property.getTypeInformation().getRequiredActualType() : entity.getTypeInformation();
    }

    @Nullable
    private PersistentEntity<?, ?> getEntityIdentifiedBy(TypeInformation<?> type) {
        Collection entities = this.contexts.stream().flatMap(it -> it.getPersistentEntities().stream()).map(PersistentEntity::getIdProperty).filter(it -> it != null && type.equals(it.getTypeInformation().getActualType())).map(PersistentProperty::getOwner).collect(Collectors.toList());
        if (entities.size() > 1) {
            String message = "Found multiple entities identified by " + type.getType() + ": ";
            message = message + entities.stream().map(it -> it.getType().getName()).collect(Collectors.joining(", "));
            message = message + "! Introduce dedicated unique identifier types or explicitly define the target type in @Reference!";
            throw new IllegalStateException(message);
        }
        return entities.isEmpty() ? null : (PersistentEntity)entities.iterator().next();
    }
}

