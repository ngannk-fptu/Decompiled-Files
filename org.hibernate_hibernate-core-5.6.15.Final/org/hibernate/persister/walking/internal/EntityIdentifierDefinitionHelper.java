/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.internal;

import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.walking.internal.CompositionSingularSubAttributesHelper;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.NonEncapsulatedEntityIdentifierDefinition;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public final class EntityIdentifierDefinitionHelper {
    private EntityIdentifierDefinitionHelper() {
    }

    public static EntityIdentifierDefinition buildSimpleEncapsulatedIdentifierDefinition(final AbstractEntityPersister entityPersister) {
        return new EncapsulatedEntityIdentifierDefinition(){
            private final AttributeDefinitionAdapter attr;
            {
                this.attr = new AttributeDefinitionAdapter(entityPersister);
            }

            @Override
            public AttributeDefinition getAttributeDefinition() {
                return this.attr;
            }

            @Override
            public boolean isEncapsulated() {
                return true;
            }

            @Override
            public EntityDefinition getEntityDefinition() {
                return entityPersister;
            }
        };
    }

    public static EntityIdentifierDefinition buildEncapsulatedCompositeIdentifierDefinition(final AbstractEntityPersister entityPersister) {
        return new EncapsulatedEntityIdentifierDefinition(){
            private final CompositionDefinitionAdapter compositionDefinition;
            {
                this.compositionDefinition = new CompositionDefinitionAdapter(entityPersister);
            }

            @Override
            public AttributeDefinition getAttributeDefinition() {
                return this.compositionDefinition;
            }

            @Override
            public boolean isEncapsulated() {
                return true;
            }

            @Override
            public EntityDefinition getEntityDefinition() {
                return entityPersister;
            }
        };
    }

    public static EntityIdentifierDefinition buildNonEncapsulatedCompositeIdentifierDefinition(final AbstractEntityPersister entityPersister) {
        return new NonEncapsulatedEntityIdentifierDefinition(){
            private final CompositionDefinitionAdapter compositionDefinition;
            {
                this.compositionDefinition = new CompositionDefinitionAdapter(entityPersister);
            }

            @Override
            public Iterable<AttributeDefinition> getAttributes() {
                return this.compositionDefinition.getAttributes();
            }

            @Override
            public Class getSeparateIdentifierMappingClass() {
                return entityPersister.getEntityMetamodel().getIdentifierProperty().hasIdentifierMapper() ? entityPersister.getEntityMetamodel().getIdentifierProperty().getType().getReturnedClass() : null;
            }

            @Override
            public boolean isEncapsulated() {
                return false;
            }

            @Override
            public EntityDefinition getEntityDefinition() {
                return entityPersister;
            }

            @Override
            public Type getCompositeType() {
                return entityPersister.getEntityMetamodel().getIdentifierProperty().getType();
            }

            @Override
            public AttributeSource getSource() {
                return this.compositionDefinition;
            }

            @Override
            public String getName() {
                return "id";
            }

            @Override
            public CompositeType getType() {
                return (CompositeType)this.getCompositeType();
            }

            @Override
            public boolean isNullable() {
                return this.compositionDefinition.isNullable();
            }
        };
    }

    private static class CompositionDefinitionAdapter
    extends AttributeDefinitionAdapter
    implements CompositionDefinition {
        CompositionDefinitionAdapter(AbstractEntityPersister entityPersister) {
            super(entityPersister);
        }

        @Override
        public String toString() {
            return "<identifier-property:" + this.getName() + ">";
        }

        @Override
        public CompositeType getType() {
            return (CompositeType)super.getType();
        }

        @Override
        public Iterable<AttributeDefinition> getAttributes() {
            return CompositionSingularSubAttributesHelper.getIdentifierSubAttributes(this.getEntityPersister());
        }
    }

    private static class AttributeDefinitionAdapter
    implements AttributeDefinition {
        private final AbstractEntityPersister entityPersister;

        AttributeDefinitionAdapter(AbstractEntityPersister entityPersister) {
            this.entityPersister = entityPersister;
        }

        @Override
        public String getName() {
            return this.entityPersister.getEntityMetamodel().getIdentifierProperty().getName();
        }

        @Override
        public Type getType() {
            return this.entityPersister.getEntityMetamodel().getIdentifierProperty().getType();
        }

        @Override
        public boolean isNullable() {
            return false;
        }

        @Override
        public AttributeSource getSource() {
            return this.entityPersister;
        }

        public String toString() {
            return "<identifier-property:" + this.getName() + ">";
        }

        protected AbstractEntityPersister getEntityPersister() {
            return this.entityPersister;
        }
    }
}

