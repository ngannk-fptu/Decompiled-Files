/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.data.convert;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.convert.EntityInstantiator;
import org.springframework.data.convert.EntityInstantiatorAdapter;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.InternalEntityInstantiatorFactory;

@Deprecated
public class EntityInstantiators
extends org.springframework.data.mapping.model.EntityInstantiators {
    public EntityInstantiators() {
    }

    public EntityInstantiators(EntityInstantiator fallback) {
        super(fallback, Collections.emptyMap());
    }

    public EntityInstantiators(Map<Class<?>, EntityInstantiator> customInstantiators) {
        super(InternalEntityInstantiatorFactory.getKotlinClassGeneratingEntityInstantiator(), EntityInstantiators.adaptFromLegacy(customInstantiators));
    }

    public EntityInstantiators(EntityInstantiator defaultInstantiator, Map<Class<?>, EntityInstantiator> customInstantiators) {
        super(defaultInstantiator, EntityInstantiators.adaptFromLegacy(customInstantiators));
    }

    @Override
    public EntityInstantiator getInstantiatorFor(PersistentEntity<?, ?> entity) {
        return new EntityInstantiatorAdapter(super.getInstantiatorFor(entity));
    }

    private static Map<Class<?>, org.springframework.data.mapping.model.EntityInstantiator> adaptFromLegacy(Map<Class<?>, EntityInstantiator> instantiators) {
        return instantiators == null ? null : instantiators.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new EntityInstantiatorAdapter((org.springframework.data.mapping.model.EntityInstantiator)e.getValue())));
    }
}

