/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.Collapsed
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.rest.api.model.RestObject;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;
import java.util.Collection;
import java.util.Map;

class EntityWalker {
    EntityWalker() {
    }

    public static void traverseWith(RestObject restEntity, SchemaType schemaType, Visitor visitor) {
        visitor.visit(restEntity, schemaType);
        if (restEntity instanceof Iterable) {
            EntityWalker.traverseWith((Iterable)restEntity, schemaType, visitor);
        }
        Collection propertyValues = restEntity.properties().values();
        EntityWalker.traverseWith(propertyValues, schemaType, visitor);
    }

    private static void traverseWith(Iterable<?> entities, SchemaType schemaType, Visitor visitor) {
        for (Object entity : entities) {
            if (entity instanceof RestObject) {
                EntityWalker.traverseWith((RestObject)entity, schemaType, visitor);
                continue;
            }
            if (entity instanceof Collapsed) continue;
            if (entity instanceof Iterable) {
                EntityWalker.traverseWith((Iterable)entity, schemaType, visitor);
                continue;
            }
            if (!(entity instanceof Map)) continue;
            EntityWalker.traverseWith(((Map)entity).values(), schemaType, visitor);
        }
    }

    public static interface Visitor {
        public void visit(RestObject var1, SchemaType var2);
    }
}

