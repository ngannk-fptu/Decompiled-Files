/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.NamedEntityGraph
 */
package org.hibernate.cfg.annotations;

import javax.persistence.NamedEntityGraph;
import org.hibernate.internal.util.StringHelper;

public class NamedEntityGraphDefinition {
    private final NamedEntityGraph annotation;
    private final String jpaEntityName;
    private final String entityName;
    private final String name;

    public NamedEntityGraphDefinition(NamedEntityGraph annotation, String jpaEntityName, String entityName) {
        this.annotation = annotation;
        this.jpaEntityName = jpaEntityName;
        this.entityName = entityName;
        String string = this.name = StringHelper.isNotEmpty(annotation.name()) ? annotation.name() : jpaEntityName;
        if (this.name == null) {
            throw new IllegalArgumentException("Named entity graph name cannot be null");
        }
    }

    public String getRegisteredName() {
        return this.name;
    }

    public String getJpaEntityName() {
        return this.jpaEntityName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public NamedEntityGraph getAnnotation() {
        return this.annotation;
    }
}

