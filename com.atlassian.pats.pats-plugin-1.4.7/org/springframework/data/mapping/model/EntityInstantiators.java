/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.mapping.model;

import java.util.Collections;
import java.util.Map;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.EntityInstantiator;
import org.springframework.data.mapping.model.KotlinClassGeneratingEntityInstantiator;
import org.springframework.util.Assert;

public class EntityInstantiators {
    private final EntityInstantiator fallback;
    private final Map<Class<?>, EntityInstantiator> customInstantiators;

    public EntityInstantiators() {
        this(Collections.emptyMap());
    }

    public EntityInstantiators(EntityInstantiator fallback) {
        this(fallback, Collections.emptyMap());
    }

    public EntityInstantiators(Map<Class<?>, EntityInstantiator> customInstantiators) {
        this(new KotlinClassGeneratingEntityInstantiator(), customInstantiators);
    }

    public EntityInstantiators(EntityInstantiator defaultInstantiator, Map<Class<?>, EntityInstantiator> customInstantiators) {
        Assert.notNull((Object)defaultInstantiator, (String)"DefaultInstantiator must not be null!");
        Assert.notNull(customInstantiators, (String)"CustomInstantiators must not be null!");
        this.fallback = defaultInstantiator;
        this.customInstantiators = customInstantiators;
    }

    public EntityInstantiator getInstantiatorFor(PersistentEntity<?, ?> entity) {
        Assert.notNull(entity, (String)"Entity must not be null!");
        Class<?> type = entity.getType();
        if (!this.customInstantiators.containsKey(type)) {
            return this.fallback;
        }
        EntityInstantiator instantiator = this.customInstantiators.get(entity.getType());
        return instantiator == null ? this.fallback : instantiator;
    }
}

