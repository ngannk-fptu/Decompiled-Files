/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.rest.api.model.RestObject
 *  com.atlassian.confluence.rest.serialization.enrich.SchemaType
 */
package com.atlassian.confluence.plugins.restapi.enrich;

import com.atlassian.confluence.plugins.restapi.enrich.CollectionEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.EntityEnricher;
import com.atlassian.confluence.plugins.restapi.enrich.EntityWalker;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.rest.api.model.RestObject;
import com.atlassian.confluence.rest.serialization.enrich.SchemaType;

class VisitorWrapper {
    private final boolean recursive;
    private final EntityWalker.Visitor visitor;

    static VisitorWrapper newTreeFilter(EntityEnricher enricher) {
        return new VisitorWrapper(VisitorWrapper.entityVisitor(enricher), true);
    }

    static VisitorWrapper newRootEntityFilter(EntityEnricher enricher) {
        return new VisitorWrapper(VisitorWrapper.entityVisitor(enricher), false);
    }

    static VisitorWrapper newRootEntityFilter(CollectionEnricher enricher) {
        return new VisitorWrapper(VisitorWrapper.collectionVisitor(enricher), false);
    }

    static VisitorWrapper newTreeFilter(CollectionEnricher enricher) {
        return new VisitorWrapper(VisitorWrapper.collectionVisitor(enricher), true);
    }

    private static EntityWalker.Visitor entityVisitor(EntityEnricher enricher) {
        return (entity, schemaType) -> {
            if (entity instanceof RestEntity) {
                enricher.enrich((RestEntity)entity, schemaType);
            }
        };
    }

    private static EntityWalker.Visitor collectionVisitor(CollectionEnricher enricher) {
        return (entity, schemaType) -> {
            if (entity instanceof RestList) {
                enricher.enrich((RestList)entity, schemaType);
            }
        };
    }

    private VisitorWrapper(EntityWalker.Visitor visitor, boolean recursive) {
        this.visitor = visitor;
        this.recursive = recursive;
    }

    public void enrich(Object entity, SchemaType schemaType) {
        if (entity instanceof Iterable) {
            this.enrichCollection((Iterable)entity, schemaType);
        } else {
            this.enrichObject(entity, schemaType);
        }
    }

    private void enrichCollection(Iterable entities, SchemaType schemaType) {
        if (entities instanceof RestObject) {
            this.enrichObject(entities, schemaType);
        } else {
            for (Object entity : entities) {
                this.enrichObject(entity, schemaType);
            }
        }
    }

    private void enrichObject(Object entity, SchemaType schemaType) {
        if (entity instanceof RestObject) {
            RestObject restObject = (RestObject)entity;
            if (this.recursive) {
                EntityWalker.traverseWith(restObject, schemaType, this.visitor);
            } else {
                this.visitor.visit(restObject, schemaType);
            }
        }
    }
}

